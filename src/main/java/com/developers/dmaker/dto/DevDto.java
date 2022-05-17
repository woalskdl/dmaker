package com.developers.dmaker.dto;

import lombok.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

//@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Slf4j
//@UtilityClass
public class DevDto {

    // @Data >> @RequiredArgsConstructor 요건 다른 생성자가 쓰이면 무시됨
    // @Data 는 투머치 > @ToString 이런거 필요없는걸 많이 포함 중 (특히 개인정보)

    // @Builder >> 객체 Set 하기에 좀더 안정적인 메소드 (테스트에 구현됨)

    // @UtilityClass >>

    @NonNull
    String name;
    Integer age;
    Integer experienceYears;
    LocalDateTime startAt;

    public void printLog() {
        log.info(getName());
    }
}
