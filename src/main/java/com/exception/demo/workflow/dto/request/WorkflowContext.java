package com.exception.demo.workflow.dto.request;

import com.exception.demo.model.TaskApprovalLevel;
import com.exception.demo.model.WorkflowApprovalLevel;
import com.exception.demo.model.WorkflowChecker;
import com.exception.demo.model.WorkflowInstance;
import com.exception.demo.model.WorkflowStep;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkflowContext {
    public WorkflowInstance instance;
    public WorkflowStep step;
    public WorkflowApprovalLevel workflowLevel;
    public TaskApprovalLevel taskLevel;
    public List<WorkflowChecker> staticCheckers;

    public boolean requiresStaticCheckers() {
        return staticCheckers != null && !staticCheckers.isEmpty();
    }
}
