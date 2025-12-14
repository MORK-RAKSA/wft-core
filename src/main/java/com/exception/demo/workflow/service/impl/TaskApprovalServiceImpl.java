package com.exception.demo.workflow.service.impl;

import com.exception.demo.core.exception.BusinessException;
import com.exception.demo.model.Task;
import com.exception.demo.model.Workflow;
import com.exception.demo.model.WorkflowInstance;
import com.exception.demo.model.WorkflowInstanceLog;
import com.exception.demo.model.WorkflowStep;
import com.exception.demo.repository.TaskRepository;
import com.exception.demo.repository.WorkflowInstanceLogRepository;
import com.exception.demo.repository.WorkflowInstanceRepository;
import com.exception.demo.repository.WorkflowRepository;
import com.exception.demo.repository.WorkflowStepRepository;
import com.exception.demo.workflow.WorkflowActionRegistry;
import com.exception.demo.workflow.dto.request.ApproveOneRequestDto;
import com.exception.demo.workflow.dto.request.SubmitWorkflowRequestDto;
import com.exception.demo.workflow.dto.response.SubmitWorkflowResult;
import com.exception.demo.workflow.dto.response.TaskApprovalResponseDto;
import com.exception.demo.workflow.dto.response.TaskResponseDetailDto;
import com.exception.demo.workflow.enums.Status;
import com.exception.demo.workflow.enums.TaskType;
import com.exception.demo.workflow.helper.TaskApprovalHelper;
import com.exception.demo.workflow.mapper.TaskMapper;
import com.exception.demo.workflow.service.TaskApprovalService;
import jakarta.transaction.Transactional;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class TaskApprovalServiceImpl implements TaskApprovalService {

    private static final LocalDateTime NOW = LocalDateTime.now();
    private final TaskRepository taskRepository;
    private final WorkflowInstanceRepository workflowInstanceRepository;
    private final TaskApprovalHelper helper;
    private final WorkflowRepository workflowRepository;
    private final WorkflowStepRepository workflowStepRepository;
    private final WorkflowInstanceLogRepository workflowInstanceLogRepository;
    private final TaskMapper mapper;
    private final WorkflowActionRegistry registry;


    @Override
    public TaskResponseDetailDto getTaskDetails(Long taskId) {

        Task task = helper.findTaskById(taskId);
        TaskType taskType = task.getTaskType();

        Object handler = registry.getHandler(taskType);
        Method method = registry.getDetailMethod(taskType);

        if (handler == null || method == null) {
            throw new BusinessException("No detail TaskType found for taskType: " + taskType);
        }

        TaskResponseDetailDto response =
            registry.invokeDetail(task.getTaskType(), taskId);

        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setRemark(task.getRemark());
        response.setRequestType(task.getRequestType().getDesc());
        response.setTaskType(task.getTaskType().getDesc());
        response.setBranchCode(task.getBranchCode());
        response.setCreatedBy(task.getCreatedBy());
        response.setCreatedOn(task.getCreatedOn());
        response.setModifiedBy(task.getModifiedBy());
        response.setModifiedOn(task.getModifiedOn());

        return response;
    }


//    @Override
//    @Transactional
//    public TaskApprovalResponseDto approveOne(ApproveOneRequestDto requestDto){
//
//        Task task = helper.findTaskById(requestDto.getTaskId());
//        return switch (task.getTaskType()) {
//            case MOBAPP, ACC_BALANCE -> null;
//            default -> throw new BusinessException("Unexpected value: " + task.getTaskType());
//        };
//    }

    @Override
    @Transactional
    public TaskApprovalResponseDto approveOne(ApproveOneRequestDto requestDto) {

        Task task = helper.findTaskById(requestDto.getTaskId());
        TaskType taskType = task.getTaskType();

        Object handler = registry.getHandler(taskType);
        Method approveMethod = registry.getApproveMethod(taskType);

        if (ObjectUtils.isEmpty(handler) || ObjectUtils.isEmpty(approveMethod)) {
            throw new BusinessException("No approval handler for taskType: " + taskType);
        }

        try {
            return (TaskApprovalResponseDto)
                    approveMethod.invoke(handler, requestDto);
        } catch (Exception e) {
            throw new RuntimeException("Approve failed for taskId=" + task.getId(), e);
        }
    }

    @Override
    public SubmitWorkflowResult submitRequest(SubmitWorkflowRequestDto dto) {

        // load workflow
        Workflow workflow = workflowRepository.findById(dto.getWorkflowId())
            .orElseThrow(() -> new BusinessException("Workflow ID does not exist."));

        List<WorkflowStep> steps = workflowStepRepository.findByWorkflow_Id(workflow.getId());
        if (steps == null || steps.isEmpty()) {
            throw new BusinessException("Workflow has no steps.");
        }

        WorkflowStep firstStep = steps.get(0);
        WorkflowStep nextStep = helper.findNextWorkFlowStep(firstStep.getId());

        Task task = helper.setTask(
            dto.getTaskTitle(),
            dto.getTaskDescription(),
            dto.getTaskType(),
            dto.getRequestType(),
            dto.getRemarks(),
            workflow,
            dto.getRequestedBy()
        );

        // completed instance
        WorkflowInstance submitInstance = mapper.toWorkflowInstance(task, workflow);
        submitInstance.setCurrentStep(firstStep);
        submitInstance.setCompletedOn(LocalDateTime.now());
        submitInstance.setStatus(Status.COMPLETED);

        // pending instance
        WorkflowInstance activeInstance = mapper.toWorkflowInstance(task, workflow);
        activeInstance.setCurrentStep(nextStep);
        activeInstance.setStatus(Status.PENDING);

        workflowInstanceRepository.save(submitInstance);
        workflowInstanceRepository.save(activeInstance);

        WorkflowInstanceLog instanceLog = new WorkflowInstanceLog();
        instanceLog.setInstance(submitInstance);
        instanceLog.setStep(firstStep);
        instanceLog.setIsApproved(true);
        instanceLog.setRemark(dto.getRemarks());
        instanceLog.setDecidedBy(dto.getRequestedBy() == null ? "SYSTEM" : dto.getRequestedBy());
        instanceLog.setDecidedOn(LocalDateTime.now());

        workflowInstanceLogRepository.save(instanceLog);

        helper.addTaskApprovalChecker(steps, task, activeInstance);
        return SubmitWorkflowResult.builder()
            .taskId(task.getId())
            .build();
    }
}