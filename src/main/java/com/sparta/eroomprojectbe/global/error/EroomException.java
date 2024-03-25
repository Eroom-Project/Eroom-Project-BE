package com.sparta.eroomprojectbe.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EroomException extends RuntimeException{

    private ErrorCode errorCode;
}
