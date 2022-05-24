package com.developers.dmaker.service;

import com.developers.dmaker.dto.CreateDeveloper;
import com.developers.dmaker.dto.DeveloperDetailDto;
import com.developers.dmaker.dto.DeveloperDto;
import com.developers.dmaker.dto.EditDeveloper;
import com.developers.dmaker.entity.Developer;
import com.developers.dmaker.entity.RetiredDeveloper;
import com.developers.dmaker.exception.DMakerException;
import com.developers.dmaker.repository.DeveloperRepository;
import com.developers.dmaker.repository.RetiredDeveloperRepository;
import com.developers.dmaker.type.DeveloperLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.developers.dmaker.code.StatusCode.EMPLOYED;
import static com.developers.dmaker.code.StatusCode.RETIRED;
import static com.developers.dmaker.exception.DMakerErrorCode.*;

@Service
@RequiredArgsConstructor
public class DMakerService {

    private final DeveloperRepository developerRepository;
    private final RetiredDeveloperRepository retiredDeveloperRepository;
    private final EntityManager en;

    // ACID Transaction
    // Atomic
    // Consistency
    // Isolation
    // Durability

    /* raw level transaction 처리
    public void createDeveloper() {
        EntityTransaction transaction = en.getTransaction();

        try {
            transaction.begin();

            // business logic start
            Developer developer = Developer.builder()
                    .developerLevel(DeveloperLevel.JUNIOR)
                    .developerSkillType(DeveloperSkillType.BACK_END)
                    .experienceYears(2)
                    .name("Olaf")
                    .age(5)
                    .build();
            developerRepository.save(developer);
            developerRepository.save(developer);
            developerRepository.save(developer);

            transaction.commit();

        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
     */

    // AOP 활용 > 위의 transaction 처리
    @Transactional
    public CreateDeveloper.Response createDeveloper(CreateDeveloper.Request request) {

        validateCreateDeveloperRequest(request);

        Developer developer = Developer.builder()
                .developerLevel(request.getDeveloperLevel())
                .developerSkillType(request.getDeveloperSkillType())
                .experienceYears(request.getExperienceYears())
                .name(request.getName())
                .age(request.getAge())
                .memberId(request.getMemberId())
                .statusCode(EMPLOYED)
                .build();

        developerRepository.save(developer);
        // assert multiple developer has been built
//        developerRepository.save(developer);
//        developerRepository.save(developer);

        return CreateDeveloper.Response.fromEntity(developer);
    }

    public List<DeveloperDto> getAllEmployedDevelopers() {
        return developerRepository.findDevelopersByStatusCodeEquals(EMPLOYED)
                .stream().map(DeveloperDto::fromEntity)
                .collect(Collectors.toList());
    }

    public DeveloperDetailDto getDeveloperDetail(String memberId) {
        return developerRepository.findByMemberId(memberId)
                .map(DeveloperDetailDto::fromEntity)
                .orElseThrow(() -> new DMakerException(NO_DEVELOPER));
    }

    @Transactional
    public DeveloperDetailDto editDeveloper(String memberId, EditDeveloper.Request request) {

        validateEditDeveloperRequest(memberId, request);

        Developer developer = developerRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DMakerException(NO_DEVELOPER));

        developer.setDeveloperLevel(request.getDeveloperLevel());
        developer.setDeveloperSkillType(request.getDeveloperSkillType());
        developer.setExperienceYears(request.getExperienceYears());

        return DeveloperDetailDto.fromEntity(developer);
    }


    @Transactional
    public DeveloperDetailDto deleteDeveloper(String memberId) {
        // 1. EMPLOYED >> RETIRED
        Developer developer = developerRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DMakerException(NO_DEVELOPER));

        developer.setStatusCode(RETIRED);

        // 2. save into RetiredDeveloper
        RetiredDeveloper retiredDeveloper = RetiredDeveloper.builder()
                .memberId(memberId)
                .name(developer.getName())
                .build();

        retiredDeveloperRepository.save(retiredDeveloper);
        return DeveloperDetailDto.fromEntity(developer);

    }

    private void validateCreateDeveloperRequest(CreateDeveloper.Request request) {
        // business validation
        validateDeveloperLevel(request.getDeveloperLevel(), request.getExperienceYears());

        developerRepository.findByMemberId(request.getMemberId())
                .ifPresent((developer -> {throw new DMakerException(DUPLICATED_MEMBER_ID);}));
    }

    private void validateEditDeveloperRequest(String memberId, EditDeveloper.Request request) {
        validateDeveloperLevel(request.getDeveloperLevel(), request.getExperienceYears());
    }

    private void validateDeveloperLevel(DeveloperLevel developerLevel, Integer experienceYears) {
        // business validation
        if (developerLevel == DeveloperLevel.SENIOR
                && experienceYears < 10)
            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCED);

        if (developerLevel == DeveloperLevel.JUNGNIOR
                && (experienceYears < 4 || experienceYears > 10))
            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCED);

        if (developerLevel == DeveloperLevel.JUNIOR && experienceYears > 4)
            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCED);
    }
}
