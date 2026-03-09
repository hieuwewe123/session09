package org.example.session09.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private String status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponse<Void> error(String message) {
        return ApiResponse.<Void>builder()
                .status("FAIL")
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, T errorDetails) {
        return ApiResponse.<T>builder()
                .status("FAIL")
                .message(message)
                .data(errorDetails)
                .build();
    }
}
