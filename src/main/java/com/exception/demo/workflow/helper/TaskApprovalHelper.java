package com.exception.demo.workflow.helper;

import com.exception.demo.core.exception.BusinessException;
import com.exception.demo.model.SystemAuthUser;
import com.exception.demo.model.Task;
import com.exception.demo.model.TaskApprovalLevel;
import com.exception.demo.model.TaskChecker;
import com.exception.demo.model.Workflow;
import com.exception.demo.model.WorkflowApprovalLevel;
import com.exception.demo.model.WorkflowChecker;
import com.exception.demo.model.WorkflowInstance;
import com.exception.demo.model.WorkflowInstanceAssignment;
import com.exception.demo.model.WorkflowInstanceLog;
import com.exception.demo.model.WorkflowStep;
import com.exception.demo.model.WorkflowTransition;
import com.exception.demo.repository.SystemAuthUserRepository;
import com.exception.demo.repository.TaskApprovalLevelRepository;
import com.exception.demo.repository.TaskCheckerRepository;
import com.exception.demo.repository.TaskRepository;
import com.exception.demo.repository.WorkflowApprovalLevelRepository;
import com.exception.demo.repository.WorkflowCheckerRepository;
import com.exception.demo.repository.WorkflowInstanceAssignmentRepository;
import com.exception.demo.repository.WorkflowInstanceLogRepository;
import com.exception.demo.repository.WorkflowInstanceRepository;
import com.exception.demo.repository.WorkflowRepository;
import com.exception.demo.repository.WorkflowStepRepository;
import com.exception.demo.repository.WorkflowTransitionRepository;
import com.exception.demo.workflow.constants.MessageCode;
import com.exception.demo.workflow.dto.request.ApproveOneRequestDto;
import com.exception.demo.workflow.dto.request.WorkflowContext;
import com.exception.demo.workflow.enums.RequestType;
import com.exception.demo.workflow.enums.Status;
import com.exception.demo.workflow.enums.TaskType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class TaskApprovalHelper {

    private static final String BRANCH_CODE = "B001";
    private static final LocalDateTime NOW = LocalDateTime.now();
    private final TaskRepository taskRepository;
    private final WorkflowRepository workflowRepository;
    private final WorkflowInstanceRepository workflowInstanceRepository;
    private final WorkflowStepRepository workflowStepRepository;
    private final WorkflowInstanceLogRepository workflowInstanceLogRepository;
    private final TaskApprovalLevelRepository taskApprovalLevelRepository;
    private final TaskCheckerRepository taskCheckerRepository;
    private final WorkflowTransitionRepository workflowTransitionRepository;
    private final WorkflowApprovalLevelRepository workflowApprovalLevelRepository;
    private final WorkflowCheckerRepository workflowCheckerRepository;
    private final SystemAuthUserRepository systemAuthUserRepository;
    private final WorkflowInstanceAssignmentRepository workflowInstanceAssignmentRepository;

    public void addTaskApprovalChecker(List<WorkflowStep> steps, Task task, WorkflowInstance activeInstance) {
        Integer activeStepId = activeInstance.getCurrentStep().getId();
        for (WorkflowStep step : steps) {
            List<WorkflowApprovalLevel> approvalLevels =
                workflowApprovalLevelRepository.findAllByStep_Id(step.getId());
            for (WorkflowApprovalLevel wa : approvalLevels) {
                TaskApprovalLevel ta = createTaskApprovalLevel(task, wa);
                // Only assign checkers for CURRENT step
                if (!step.getId().equals(activeStepId)) {
                    continue;
                }
                // check line or workflow checker?
                if (Boolean.TRUE.equals(wa.getIsLineManagerApproval())) {
                    assignLineManagerCheckers(task, wa, ta, step, activeInstance);
                } else {
                    assignStaticCheckers(wa, ta, step, activeInstance);
                }
            }
        }
    }

    public String resolveSubmittedBy(String username){
        return username == null ? MessageCode.SYSTEM : username;
    }

    public Task setTask(String title, String desc, TaskType taskType, RequestType requestType, String remark, Workflow workflow, String username) {
        Task task = new Task();
        task.setTaskType(taskType);
        task.setRequestType(requestType);
        task.setBranchCode(BRANCH_CODE);
        task.setRemark(remark);
        task.setTitle(title);
        task.setDescription(desc);
        task.setStatus(Status.PENDING);
        task.setCreatedBy(username == null? MessageCode.SYSTEM : username);
        task.setCreatedOn(LocalDateTime.now());
        task.setModifiedBy(username == null? MessageCode.SYSTEM : username);
        task.setWorkflow(workflow);
        taskRepository.save(task);
        return task;
    }

    public List<SystemAuthUser> getLineManagers(SystemAuthUser maker, Integer requiredApproval) {
        List<SystemAuthUser> managers = new ArrayList<>();
        SystemAuthUser current = maker.getLineManagerId();

        for (int i = 0; i < requiredApproval; i++) {
            if (ObjectUtils.isEmpty(current)) break;
            managers.add(current);
            current = current.getLineManagerId();
        }
        return managers;
    }

    public TaskApprovalLevel createTaskApprovalLevel(Task task, WorkflowApprovalLevel wa) {
        TaskApprovalLevel ta = new TaskApprovalLevel();
        ta.setTask(task);
        ta.setLevelNumber(wa.getLevelNumber());
        ta.setRequiredApproval(wa.getRequiredApproval());
        ta.setStatus(Status.PENDING);
        return taskApprovalLevelRepository.save(ta);
    }

    public void assignLineManagerCheckers(
        Task task,
        WorkflowApprovalLevel wa,
        TaskApprovalLevel ta,
        WorkflowStep step,
        WorkflowInstance instance
    ) throws BusinessException {
        SystemAuthUser maker = systemAuthUserRepository.findByUsername(task.getCreatedBy());
        List<SystemAuthUser> managers = getLineManagers(maker, wa.getRequiredApproval());

        if (managers.size() < wa.getRequiredApproval()) {
            throw new BusinessException(String.format(MessageCode.TASK_NOT_ENOUGH_LINE_MANAGERS, wa.getLevelNumber()));
        }

        for (SystemAuthUser user : managers) {
            TaskChecker tc = createTaskChecker(user, ta);
            createAssignment(instance, step, ta, tc);
        }
    }

    public void assignStaticCheckers(
        WorkflowApprovalLevel wa,
        TaskApprovalLevel ta,
        WorkflowStep step,
        WorkflowInstance instance
    ) {
        List<WorkflowChecker> checkers = workflowCheckerRepository.findById_Level_Id(wa.getId());

        for (WorkflowChecker wc : checkers) {
            TaskChecker tc = createTaskChecker(wc.getId().getUser(), ta);
            createAssignment(instance, step, ta, tc);
        }
    }

    public TaskChecker createTaskChecker(SystemAuthUser user, TaskApprovalLevel ta) {
        TaskChecker tc = new TaskChecker();
        tc.setUser(user);
        tc.setLevel(ta);
        tc.setIsActive(true);
        return taskCheckerRepository.save(tc);
    }

    public void createAssignment(
        WorkflowInstance instance,
        WorkflowStep step,
        TaskApprovalLevel ta,
        TaskChecker tc
    ) {
        WorkflowInstanceAssignment assign = new WorkflowInstanceAssignment();
        assign.setInstance(instance);
        assign.setStep(step);
        assign.setApprovalLevel(ta);
        assign.setChecker(tc);
        workflowInstanceAssignmentRepository.save(assign);
    }

    public WorkflowStep findNextWorkFlowStep(Integer stepId){
        WorkflowTransition transition =
            workflowTransitionRepository.findByFromStep_IdAndIsApproved(
                stepId, true);
        return transition.getToStep();
    }

    public Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
            .orElseThrow(()-> new BusinessException(String.format(MessageCode.TASK_DOES_NOT_EXIST, taskId)));
    }

    public void createNextInstance(WorkflowInstance instance, WorkflowStep nextStep) {
        WorkflowInstance nextInstance = new WorkflowInstance();
        nextInstance.setWorkflow(instance.getWorkflow());
        nextInstance.setTask(instance.getTask());
        nextInstance.setCurrentStep(nextStep);
        nextInstance.setStartedOn(NOW);
        nextInstance.setStatus(Status.PENDING);
        workflowInstanceRepository.save(nextInstance);
    }

    public void saveApprovalLog(WorkflowContext ctx, ApproveOneRequestDto dto) {

        WorkflowInstanceLog log = new WorkflowInstanceLog();
        log.setInstance(ctx.instance);
        log.setStep(ctx.step);
        log.setIsApproved(true);
        log.setDecidedBy(dto.getApprovedBy());
        log.setRemark(dto.getRemark());
        log.setDecidedOn(LocalDateTime.now());

        workflowInstanceLogRepository.save(log);
    }
}
