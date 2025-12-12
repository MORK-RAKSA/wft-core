package com.exception.demo.workflow.service.impl;

import com.exception.demo.model.WorkflowStep;
import com.exception.demo.model.WorkflowTransition;
import com.exception.demo.repository.TaskApprovalLevelRepository;
import com.exception.demo.repository.TaskRepository;
import com.exception.demo.repository.WorkflowInstanceRepository;
import com.exception.demo.repository.WorkflowStepRepository;
import com.exception.demo.repository.WorkflowTransitionRepository;
import com.exception.demo.workflow.dto.request.WorkflowContext;
import com.exception.demo.workflow.dto.response.TaskApprovalResponseDto;
import com.exception.demo.workflow.enums.ActionType;
import com.exception.demo.workflow.enums.Status;
import com.exception.demo.workflow.helper.TaskApprovalHelper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowTransitionService {

    private final WorkflowTransitionRepository transitionRepo;
    private final WorkflowStepRepository workflowStepRepository;
    private final WorkflowInstanceRepository instanceRepo;
    private final TaskRepository taskRepo;
    private final TaskApprovalLevelRepository taskApprovalLevelRepo;
    private final TaskApprovalHelper helper;

    public void completeApprovalLevel(WorkflowContext ctx) {
        ctx.taskLevel.setStatus(Status.APPROVED);
        taskApprovalLevelRepo.save(ctx.taskLevel);

        ctx.instance.setStatus(Status.COMPLETED);
        instanceRepo.save(ctx.instance);
    }

    public TaskApprovalResponseDto moveToNextStepOrFinish(WorkflowContext ctx) {

        WorkflowTransition transition = transitionRepo
            .findByFromStep_IdAndIsApproved(ctx.step.getId(), true);

        // No next step
        if (transition == null) {
            ctx.instance.getTask().setStatus(Status.APPROVED);
            taskRepo.save(ctx.instance.getTask());
            return new TaskApprovalResponseDto("WORKFLOW_COMPLETED");
        }

        WorkflowStep nextStep = transition.getToStep();

        // Handle Done
        if (nextStep.getActionType() == ActionType.REVIEW) {
            return autoCompleteReview(ctx, nextStep);
        }

        helper.createNextInstance(ctx.instance, nextStep);
        return new TaskApprovalResponseDto("MOVED_TO_NEXT_STEP");
    }

    private TaskApprovalResponseDto autoCompleteReview(WorkflowContext ctx, WorkflowStep nextStep) {
        ctx.instance.setCurrentStep(nextStep);
        ctx.instance.setCompletedOn(LocalDateTime.now());
        ctx.instance.setStatus(Status.COMPLETED);
        ctx.instance.getTask().setStatus(Status.APPROVED);

        instanceRepo.save(ctx.instance);
        taskRepo.save(ctx.instance.getTask());
        return new TaskApprovalResponseDto("AUTO_REVIEW_COMPLETED");
    }
}
