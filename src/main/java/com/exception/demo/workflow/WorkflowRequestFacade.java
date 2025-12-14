package com.exception.demo.workflow;

import com.exception.demo.workflow.dto.request.FeaturePreparedData;
import com.exception.demo.workflow.dto.request.SubmitWorkflowRequestDto;
import com.exception.demo.workflow.dto.request.WorkflowPayloadRequestDto;
import com.exception.demo.workflow.dto.response.SubmitWorkflowResult;
import com.exception.demo.workflow.mapper.TaskMapper;
import com.exception.demo.workflow.service.TaskApprovalService;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowRequestFacade {

    private final WorkflowActionRegistry registry;
    private final TaskMapper mapper;
    private final TaskApprovalService workflowService;

    public SubmitWorkflowResult handle(WorkflowPayloadRequestDto req) {

        Object handler = registry.getHandler(req.getTaskType());
        Method method = registry.getRequestMethod(req.getTaskType(), req.getRequestType());

        FeaturePreparedData prepared;
        try {
            prepared = (FeaturePreparedData) method.invoke(handler, req.getPayload());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        SubmitWorkflowRequestDto dto = mapper.toWorkflowRequest(prepared, req);
        return workflowService.submitRequest(dto);
    }
}
