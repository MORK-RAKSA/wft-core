package com.wft.core.services.resolvers;

import com.wft.core.constant.ApiCodeConstant;
import com.wft.core.services.annotations.SnakeModelAttribute;
import com.wft.core.utilities.ConverterUtils;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import com.wft.core.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class SnakeModelAttributeResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SnakeModelAttribute.class);
    }

    @Override
    public Object resolveArgument(
            @Nonnull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (ObjectUtils.isEmpty(request)) {
            log.error("class=[SnakeModelAttributeResolver], method=[resolveArgument], message=[HttpServletRequest not available]");
            throw new ValidationException(ApiCodeConstant.GENERAL_ERR, "HttpServletRequest not available");
        }

        Map<String, Object> params = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> {
                    params.put(ConverterUtils.toCamelCase(key), value[0]);
                }
        );

        Object target = parameter.getParameterType().getDeclaredConstructor().newInstance();
        if (ObjectUtils.isEmpty(binderFactory)) {
            log.error("class=[SnakeModelAttributeResolver], method=[resolveArgument], message=[WebDataBinderFactory must not be null]");
            throw new ValidationException(ApiCodeConstant.GENERAL_ERR, "WebDataBinderFactory must not be null");
        }

        WebDataBinder binder = binderFactory.createBinder(webRequest, target, Objects.requireNonNull(parameter.getParameterName()));
        binder.bind(new MutablePropertyValues(params));
        return target;
    }
}