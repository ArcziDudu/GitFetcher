package com.fetcher.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(staticName = "of")
public class ErrorResponse {
    int status;
    String message;

}
