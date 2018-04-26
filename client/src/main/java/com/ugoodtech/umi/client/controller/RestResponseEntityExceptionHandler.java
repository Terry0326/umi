package com.ugoodtech.umi.client.controller;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.ugoodtech.umi.core.dto.JsonResponse;
import com.ugoodtech.umi.core.exception.UmiException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UmiException.class)
    protected ResponseEntity<Object> handleConflict(UmiException ex, WebRequest request) {
        JsonResponse jsonResponse = JsonResponse.errorResponse(ex.getErrorCode(), ex.getMessage());
        return handleExceptionInternal(ex, jsonResponse, new HttpHeaders(), HttpStatus.OK, request);

    }
}
