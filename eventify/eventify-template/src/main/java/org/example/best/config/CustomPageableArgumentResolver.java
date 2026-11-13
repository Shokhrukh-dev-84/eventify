package org.example.best.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.best.common.dto.pagination.Pageable;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CustomPageableArgumentResolver implements HandlerMethodArgumentResolver {

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public boolean supportsParameter(MethodParameter parameter) {

            return parameter.getParameterType().equals(Pageable.class);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

            String json = webRequest.getParameter("pageable");
            if (json == null || json.isBlank()) {
                return new Pageable();
            }

            return objectMapper.readValue(json, Pageable.class);
        }
}
