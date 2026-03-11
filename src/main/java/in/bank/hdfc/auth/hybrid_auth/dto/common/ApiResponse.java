package in.bank.hdfc.auth.hybrid_auth.dto.common;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final ApiStatus status;
    private final T data;

    private ApiResponse(ApiStatus status, T data) {
        this.status = status;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(new ApiStatus("0000", "Request succeeded"), data);
    }

    public static <T> ApiResponse<T> failure(String code, String message) {
        return new ApiResponse<>(new ApiStatus(code, message), null);
    }
}
