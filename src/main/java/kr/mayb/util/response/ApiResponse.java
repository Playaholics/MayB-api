package kr.mayb.util.response;

import org.springframework.http.ProblemDetail;

public record ApiResponse<T>(
        T data,
        ProblemDetail error
) {
    public static <R> ApiResponse<R> of(R data) {
        return new ApiResponse<>(data, null);
    }

    public static ApiResponse<Void> error(ProblemDetail error) {
        return new ApiResponse<>(null, error);
    }
}
