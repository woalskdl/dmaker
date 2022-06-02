package com.developers.dmaker.service;

import com.developers.dmaker.code.StatusCode;
import com.developers.dmaker.dto.CreateDeveloper;
import com.developers.dmaker.dto.DeveloperDetailDto;
import com.developers.dmaker.entity.Developer;
import com.developers.dmaker.exception.DMakerException;
import com.developers.dmaker.repository.DeveloperRepository;
import com.developers.dmaker.type.DeveloperLevel;
import com.developers.dmaker.type.DeveloperSkillType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.developers.dmaker.constant.DMakerConstant.MAX_JUNIOR_EXPERIENCE_YEARS;
import static com.developers.dmaker.constant.DMakerConstant.MIN_SENIOR_EXPERIENCE_YEARS;
import static com.developers.dmaker.exception.DMakerErrorCode.DUPLICATED_MEMBER_ID;
import static com.developers.dmaker.exception.DMakerErrorCode.LEVEL_EXPERIENCE_YEARS_NOT_MATCED;
import static com.developers.dmaker.type.DeveloperLevel.*;
import static com.developers.dmaker.type.DeveloperSkillType.BACK_END;
import static com.developers.dmaker.type.DeveloperSkillType.FRONT_END;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DMakerServiceTest {

    @InjectMocks
    private DMakerService dMakerService;

    @Mock
    private DeveloperRepository developerRepository;

    // 기본 사용할 객체 지정
    private final Developer defaultDeveloper = Developer.builder()
            .developerLevel(SENIOR)
            .developerSkillType(FRONT_END)
            .experienceYears(12)
            .statusCode(StatusCode.EMPLOYED)
            .name("name")
            .age(32)
            .build();

    private CreateDeveloper.Request getCreateRequest(DeveloperLevel developerLevel,
                                                     DeveloperSkillType developerSkillType,
                                                     Integer experienceYears) {
         return CreateDeveloper.Request.builder()
                    .developerLevel(developerLevel)
                    .developerSkillType(developerSkillType)
                    .experienceYears(experienceYears)
                    .memberId("memberId")
                    .name("name")
                    .age(32)
                    .build();
    }

    @Test
    public void testSomething() {
        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.of(defaultDeveloper));

        DeveloperDetailDto developerDetail = dMakerService.getDeveloperDetail("memberId");

        assertEquals(SENIOR, developerDetail.getDeveloperLevel());
        assertEquals(FRONT_END, developerDetail.getDeveloperSkillType());
        assertEquals(32, developerDetail.getAge());
    }

    @Test
    void createDeveloperTest_success() {
        // 1. given
        // 1-1. validation given (inner logic)
        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.empty());

        // 1-2. Repository 에서 저장하는 객체의 사본
        given(developerRepository.save(any()))
                .willReturn(defaultDeveloper);
        ArgumentCaptor<Developer> captor = ArgumentCaptor.forClass(Developer.class);

        // 2. when
        dMakerService.createDeveloper(getCreateRequest(SENIOR, FRONT_END, MIN_SENIOR_EXPERIENCE_YEARS + 1));

        // 3. then
        // 3-1. 특정 Mock이 몇번이나 호출되었다.
        verify(developerRepository, times(1))
                .save(captor.capture());

        Developer savedDeveloper = captor.getValue();
        assertEquals(SENIOR, savedDeveloper.getDeveloperLevel());
        assertEquals(FRONT_END, savedDeveloper.getDeveloperSkillType());
        assertEquals(MIN_SENIOR_EXPERIENCE_YEARS + 1, savedDeveloper.getExperienceYears());

    }

    @Test
    void createDeveloperTest_fail_with_unmatched_level() {
        // 1. given

        // 2. when

        // 3. then
        DMakerException dMakerException = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(getCreateRequest(JUNIOR, BACK_END, MAX_JUNIOR_EXPERIENCE_YEARS + 1)));

        assertEquals(LEVEL_EXPERIENCE_YEARS_NOT_MATCED, dMakerException.getDMakerErrorCode());

        dMakerException = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(getCreateRequest(JUNGNIOR, FRONT_END, MIN_SENIOR_EXPERIENCE_YEARS + 1)));

        assertEquals(LEVEL_EXPERIENCE_YEARS_NOT_MATCED, dMakerException.getDMakerErrorCode());

        dMakerException = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(getCreateRequest(SENIOR, FRONT_END, MIN_SENIOR_EXPERIENCE_YEARS - 1)));

        assertEquals(LEVEL_EXPERIENCE_YEARS_NOT_MATCED, dMakerException.getDMakerErrorCode());
    }

    @Test
    void createDeveloperTest_fail_with_duplicated() {
        // 1. given
        // 1-1. validation given (inner logic)
        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.of(defaultDeveloper));

        // 2. when

        // 3. then
        DMakerException dMakerException = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(getCreateRequest(SENIOR, FRONT_END, 12)));

        assertEquals(DUPLICATED_MEMBER_ID, dMakerException.getDMakerErrorCode());

    }
}