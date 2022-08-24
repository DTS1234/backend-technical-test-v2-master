package com.tui.proof.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class RequestHelper {

    private ObjectMapper objectMapper;

    public Map<String, Object> convertRequestToParams(Object request) {
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return objectMapper.convertValue(request, new TypeReference<Map<String, Object>>() {
        });
    }
}

