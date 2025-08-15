package com.cjg.book.code;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusCode {
    good("정상"),
    damage("훼손"),
    lost("분실");

    private final String message;
}
