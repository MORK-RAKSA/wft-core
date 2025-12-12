package com.exception.demo.workflow;

import com.exception.demo.core.exception.BusinessException;
import com.exception.demo.workflow.annotation.WorkflowRequestType;
import com.exception.demo.workflow.annotation.WorkflowTaskDetail;
import com.exception.demo.workflow.annotation.WorkflowTaskType;
import com.exception.demo.workflow.dto.response.TaskResponseDetailDto;
import com.exception.demo.workflow.enums.RequestType;
import com.exception.demo.workflow.enums.TaskType;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class WorkflowActionRegistry implements ApplicationContextAware {

    private final Map<TaskType, Object> taskTypeMap = new EnumMap<>(TaskType.class);
    private final Map<String, Method> requestTypeMap = new HashMap<>();
    private final Map<TaskType, Method> detailMap = new EnumMap<>(TaskType.class);

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        this.context = ctx;
        initialize();
    }

    private void initialize() {
        Map<String,Object> beans = context.getBeansWithAnnotation(WorkflowTaskType.class);

        for (Object bean : beans.values()) {

            WorkflowTaskType taskAnnotation = bean.getClass().getAnnotation(WorkflowTaskType.class);
            TaskType taskType = taskAnnotation.value();

            taskTypeMap.put(taskType, bean);

            for (Method method : bean.getClass().getDeclaredMethods()) {

                // REQUEST
                WorkflowRequestType reqAnnotation = method.getAnnotation(WorkflowRequestType.class);
                if (reqAnnotation != null) {
                    requestTypeMap.put(taskType + ":" + reqAnnotation.value(), method);
                }

                // DETAIL
                WorkflowTaskDetail detailAnno = method.getAnnotation(WorkflowTaskDetail.class);
                if (detailAnno != null) {
                    detailMap.put(taskType, method);
                }
            }
        }
    }

    public TaskResponseDetailDto invokeDetail(TaskType taskType, Long taskId) {

        Object handler = taskTypeMap.get(taskType);
        Method method = detailMap.get(taskType);

        if (handler == null || method == null) {
            throw new BusinessException("Detail handler not found for " + taskType);
        }

        try {
            return (TaskResponseDetailDto) method.invoke(handler, taskId);

        } catch (InvocationTargetException e) {
            // If handler itself throws BusinessException → rethrow it cleanly
            if (e.getTargetException() instanceof BusinessException be) {
                throw be;
            }
            throw new BusinessException(String.format("Error invoking detail method for %s", taskType));

        } catch (Exception e) {
            throw new BusinessException("Unexpected system error in workflow detail");
        }
    }

    public Object getHandler(TaskType t) { return taskTypeMap.get(t); }
    public Method getRequestMethod(TaskType t, RequestType r) { return requestTypeMap.get(String.format("%s:%s", t, r)); }
    public Method getDetailMethod(TaskType t) { return detailMap.get(t); }

}
