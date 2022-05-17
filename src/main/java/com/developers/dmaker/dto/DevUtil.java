package com.developers.dmaker.dto;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class DevUtil {

    public static void printLog() {
        System.out.println(LocalDateTime.of(2022, 5, 17, 9, 22));
    }
}
