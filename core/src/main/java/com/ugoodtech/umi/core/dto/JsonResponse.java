package com.ugoodtech.umi.core.dto;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Stone Shaw, 15/11/18
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonResponse<T> implements Serializable {
    private boolean success;
    private String error;
    private String errorDescription;
    private Long total;
    private T data;

    public static <T> JsonResponse<T> successResponse() {
        JsonResponse<T> jsonResponse = new JsonResponse<>();
        jsonResponse.setSuccess(true);
        return jsonResponse;
    }

    public static <T> JsonResponse<T> successResponseWithData(T data) {
        JsonResponse<T> jsonResponse = JsonResponse.successResponse();
        jsonResponse.setData(data);
        return jsonResponse;
    }

    public static <T> JsonResponse<List<T>> successResponseWithPageData(Page<T> pageData) {
        JsonResponse<List<T>> jsonResponse = JsonResponse.successResponse();
        jsonResponse.setData(pageData.getContent());
        jsonResponse.setTotal(pageData.getTotalElements());
        return jsonResponse;
    }

    public static <T> JsonResponse<List<T>> successResponseWithPageData(List<T> data, Long total) {
        JsonResponse<List<T>> jsonResponse = JsonResponse.successResponse();
        jsonResponse.setData(data);
        jsonResponse.setTotal(total);
        return jsonResponse;
    }

    public static JsonResponse errorResponseWithError(String error, String errorDescription) {
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.setSuccess(false);
        jsonResponse.setError(error);
        jsonResponse.setErrorDescription(errorDescription);
        return jsonResponse;
    }

    public static JsonResponse errorResponse(Integer error, String errorDescription) {
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.setSuccess(false);
        jsonResponse.setError(error + "");
        jsonResponse.setErrorDescription(errorDescription);
        return jsonResponse;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @JsonProperty("error_description")
    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
