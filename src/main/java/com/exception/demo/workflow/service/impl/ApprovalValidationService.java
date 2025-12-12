package com.exception.demo.workflow.service.impl;

import com.exception.demo.core.exception.BusinessException;
import com.exception.demo.model.SystemAuthUser;
import com.exception.demo.model.TaskApprovalLevel;
import com.exception.demo.model.WorkflowApprovalLevel;
import com.exception.demo.model.WorkflowChecker;
import com.exception.demo.model.WorkflowInstance;
import com.exception.demo.model.WorkflowStep;
import com.exception.demo.repository.SystemAuthUserRepository;
import com.exception.demo.repository.TaskApprovalLevelRepository;
import com.exception.demo.repository.TaskCheckerRepository;
import com.exception.demo.repository.TaskRepository;
import com.exception.demo.repository.WorkflowApprovalLevelRepository;
import com.exception.demo.repository.WorkflowCheckerRepository;
import com.exception.demo.repository.WorkflowInstanceLogRepository;
import com.exception.demo.repository.WorkflowInstanceRepository;
import com.exception.demo.repository.WorkflowStepRepository;
import com.exception.demo.workflow.constants.MessageCode;
import com.exception.demo.workflow.dto.request.ApproveOneRequestDto;
import com.exception.demo.workflow.dto.request.WorkflowContext;
import com.exception.demo.workflow.enums.Status;
import com.exception.demo.workflow.helper.TaskApprovalHelper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class ApprovalValidationService {
    private final WorkflowInstanceRepository instanceRepo;
    private final WorkflowStepRepository stepRepo;
    private final WorkflowApprovalLevelRepository approvalLevelRepo;
    private final TaskApprovalLevelRepository taskApprovalLevelRepo;
    private final WorkflowInstanceLogRepository workflowInstanceLogRepo;
    private final TaskRepository taskRepo;
    private final SystemAuthUserRepository systemAuthUserRepo;
    private final TaskApprovalHelper helper;
    private final TaskCheckerRepository taskCheckerRepository;
    private final WorkflowCheckerRepository workflowCheckerRepository;

    public WorkflowContext validateAndLoadContext(ApproveOneRequestDto requestDto) {

        WorkflowInstance instance = instanceRepo.findByTask_IdAndStatus(requestDto.getTaskId(), Status.PENDING);
        if (ObjectUtils.isEmpty(instance)) throw new BusinessException("No active workflow instance found");

        WorkflowStep step = instance.getCurrentStep();
        if (ObjectUtils.isEmpty(step)) throw new BusinessException("No active workflow step found");

        WorkflowApprovalLevel workflowLevel = approvalLevelRepo.findByStep_Id(step.getId());
        if (ObjectUtils.isEmpty(workflowLevel)) throw new BusinessException("No active workflow level found");

        TaskApprovalLevel taskLevel = taskApprovalLevelRepo.findByTask_IdAndLevelNumber(
            requestDto.getTaskId(),
            workflowLevel.getLevelNumber()
        );
        if (ObjectUtils.isEmpty(taskLevel)) throw new BusinessException("No active task level found");

        List<WorkflowChecker> staticChecker = workflowCheckerRepository.findById_Level_Id(taskLevel.getLevelNumber());

        return new WorkflowContext(instance, step, workflowLevel, taskLevel, staticChecker);
    }

    public void validateApprover(WorkflowContext ctx, ApproveOneRequestDto dto) {

        if (!checkerAssigned(ctx, dto))
            throw new BusinessException("Not authorized");

        if (userAlreadyApproved(ctx, dto))
            throw new BusinessException(MessageCode.TASK_ALREADY_APPROVED);

        validateApprovalOrder(ctx, dto);
    }

    public boolean isLevelCompleted(WorkflowContext ctx) {
        int approved = workflowInstanceLogRepo.countValidApprovals(
            ctx.instance.getId(), ctx.step.getId(), ctx.taskLevel.getId()
        );
        return approved >= ctx.taskLevel.getRequiredApproval();
    }

    private void validateApprovalOrder(WorkflowContext ctx, ApproveOneRequestDto dto) {
        SystemAuthUser maker = systemAuthUserRepo.findByUsername(ctx.instance.getTask().getCreatedBy());

        List<SystemAuthUser> approvers =
            helper.getLineManagers(maker, ctx.taskLevel.getRequiredApproval());

        int approvedCount = workflowInstanceLogRepo.countValidApprovals(
            ctx.instance.getId(), ctx.step.getId(), ctx.taskLevel.getId());

        SystemAuthUser expected = approvers.get(approvedCount);

        if (!expected.getUsername().equals(dto.getApprovedBy())) {
            throw new BusinessException(
                String.format("You cannot approve yet. Waiting for: %s",
                    expected.getUsername()));
        }
    }

    private boolean checkerAssigned(WorkflowContext ctx, ApproveOneRequestDto dto) {
        return taskCheckerRepository.existsByLevel_IdAndUser_Username(
            ctx.taskLevel.getId(),
            dto.getApprovedBy()
        );
    }

    private boolean userAlreadyApproved(WorkflowContext ctx, ApproveOneRequestDto dto) {
        return workflowInstanceLogRepo.hasUserAlreadyApproved(
            ctx.instance.getId(),
            ctx.step.getId(),
            ctx.taskLevel.getId(),
            dto.getApprovedBy()
        );
    }

}
