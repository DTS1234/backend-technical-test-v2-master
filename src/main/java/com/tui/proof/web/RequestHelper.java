package com.tui.proof.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class RequestHelper {

    private ObjectMapper objectMapper;


    public Map<String, Object> convertRequestToParams(Object request) {
        return objectMapper.convertValue(request, new TypeReference<Map<String, Object>>() {
        });
    }
}

