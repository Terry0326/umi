package com.ugoodtech.umi.core.dto;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-8-27
 * Time: 上午9:40
 * To change this template use File | Settings | File Templates.
 */
public class JsonException extends RuntimeException {

    private static final long serialVersionUID = -5365630128856068164L;
    /**
     * Constructor for JsonObjectException.
     *
     * @param message exception message
     */

    private boolean success;
    private String error;
    private String errorDescription;

    private String code;

    public String getErrCode() {
        return code;
    }

    public JsonException(String message) {
        super(message);
        this.success = false;
        this.errorDescription = message;
    }

    public JsonException(String message, String errCode) {
        super(message);
        this.errorDescription = message;
        this.success = false;
        this.code = errCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public JsonException() {
        super();
    }

    public JsonException(Throwable cause) {
        super(cause);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    @Override
    public void printStackTrace() {
        this.getCause().printStackTrace();
    }

}
