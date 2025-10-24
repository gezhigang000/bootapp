package com.renlijia.bootapp.core.admin.controller.data;

import java.io.Serializable;

public class ApiResponse<T> implements Serializable {

    private T data;

    private boolean success;

    private String code;

    private String msg;

    public static <T> ApiResponse<T> success(T v) {
        ApiResponse<T> result = new ApiResponse<>();
        result.setSuccess(true);
        result.setData(v);
        result.setCode(Code.SUCCESS);
        return result;
    }

    public static <T> ApiResponse<T> paramError(String message) {
        return error(Code.PARAM_ERROR,message);
    }

    public static <T> ApiResponse<T> commonError(String message) {
        return error(Code.COMMON_ERROR,message);
    }

    public static <T> ApiResponse<T> bizError(String message) {
        return error(Code.BIZ_ERROR,message);
    }

    private static <T> ApiResponse<T> error(String errorCode,String message) {
        ApiResponse<T> result = new ApiResponse<>();
        result.setSuccess(false);
        result.setCode(errorCode);
        result.setMsg(message);
        return result;
    }

    public static class Code{
        public static final String SUCCESS = "000";
        public static final String COMMON_ERROR = "ERR_000";
        public static final String PARAM_ERROR = "ERR_001";
        public static final String BIZ_ERROR = "ERR_002";

    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
