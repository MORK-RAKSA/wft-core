package com.exception.demo.workflow.mapper;

import com.exception.demo.model.Task;
import com.exception.demo.model.Workflow;
import com.exception.demo.model.WorkflowInstance;
import com.exception.demo.workflow.dto.request.FeaturePreparedData;
import com.exception.demo.workflow.dto.request.SubmitWorkflowRequestDto;
import com.exception.demo.workflow.dto.request.WorkflowPayloadRequestDto;
import com.exception.demo.workflow.dto.response.TaskResponseDetailDto;
import java.time.LocalDateTime;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {

    @Mapping(target = "requestType", expression = "java(task.getRequestType().getDesc())")
    @Mapping(target = "taskType", expression = "java(task.getTaskType().getDesc())")
    @Mapping(target = "workflow", expression = "java(task.getWorkflow().getName())")
    TaskResponseDetailDto toDto(Task task);

    default WorkflowInstance toWorkflowInstance(Task task, Workflow workflow) {
        WorkflowInstance response = new WorkflowInstance();
        response.setStartedOn(LocalDateTime.now());
        response.setTask(task);
        response.setWorkflow(workflow);
        return response;
    }

    @Mapping(target = "workflowId", source = "api.workflowId")
    @Mapping(target = "requestedBy", source = "api.requestedBy")
    @Mapping(target = "remarks", source = "api.remarks")

    @Mapping(target = "taskType", source = "feature.taskType")
    @Mapping(target = "requestType", source = "feature.requestType")
    @Mapping(target = "taskTitle", source = "feature.taskTitle")
    @Mapping(target = "taskDescription", source = "feature.taskDescription")
    @Mapping(target = "metadata", source = "feature.metadata")
    SubmitWorkflowRequestDto toWorkflowRequest(
        FeaturePreparedData feature,
        WorkflowPayloadRequestDto api
    );
}