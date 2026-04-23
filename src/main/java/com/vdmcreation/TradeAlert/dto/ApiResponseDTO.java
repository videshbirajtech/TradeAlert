package com.vdmcreation.TradeAlert.dto;

public class ApiResponseDTO<T> {

    private boolean success;
    private String message;
    private T data;

    public ApiResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponseDTO(String message, T data, boolean success) {
        this.message = message;
        this.data = data;
        this.success = success;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
