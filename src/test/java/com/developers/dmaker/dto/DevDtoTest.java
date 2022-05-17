package com.developers.dmaker.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DevDtoTest {

    @Test
    void test() {

        DevDto devDto = DevDto.builder()
                .name("snow")
                .age(21)
                .startAt(LocalDateTime.now())
                .experienceYears(3)
                .build();

        System.out.println(devDto);
        devDto.printLog();
    }

}