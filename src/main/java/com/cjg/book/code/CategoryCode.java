package com.cjg.book.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CategoryCode {

    literature(1, "문학"),
    economic_management(2,"경제경영"),
    humanity(3,"인문학"),
    it(4,"IT"),
    science(5,"과학"),

    cook(6,"요리"),
    cook_general(7,"요리일반");

    private final long code;
    private final String name;
}
