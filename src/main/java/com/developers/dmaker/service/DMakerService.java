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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.developers.dmaker.code.StatusCode.EMPLOYED;
import static com.developers.dmaker.code.StatusCode.RETIRED;
import static com.developers.dmaker.exception.DMakerErrorCode.DUPLICATED_MEMBER_ID;
import static com.developers.dmaker.exception.DMakerErrorCode.NO_DEVELOPER;

@Service
@RequiredArgsConstructor
public class DMakerService {

    private final DeveloperRepository developerRepository;
    private final RetiredDeveloperRepository retiredDeveloperRepository;

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
//        Developer developer = createDeveloperFromRequest(request);
//        developerRepository.save(developer);

        // if multiple developer has been built
//        developerRepository.save(developer);
//        developerRepository.save(developer);

        return CreateDeveloper.Response.fromEntity(
                developerRepository.save(createDeveloperFromRequest(request))
        );
    }

    @Transactional(readOnly = true)
    public List<DeveloperDto> getAllEmployedDevelopers() {
        return developerRepository.findDevelopersByStatusCodeEquals(EMPLOYED)
                .stream().map(DeveloperDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeveloperDetailDto getDeveloperDetail(@NonNull String memberId) {
        return DeveloperDetailDto.fromEntity(getDeveloperByMemberId(memberId));
    }

    private Developer getDeveloperByMemberId(String memberId) {
        return developerRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DMakerException(NO_DEVELOPER));
    }

    @Transactional
    public DeveloperDetailDto editDeveloper(String memberId, EditDeveloper.Request request) {

//        validateDeveloperLevel(request.getDeveloperLevel(), request.getExperienceYears());
        request.getDeveloperLevel().validateExperienceYears(request.getExperienceYears());

//        Developer developer = getDeveloperByMemberId(memberId);
//        setDeveloperFromRequest(request, developer);

        return DeveloperDetailDto.fromEntity(getUpdatedDeveloperFromRequest(request, getDeveloperByMemberId(memberId)));
    }

    private Developer getUpdatedDeveloperFromRequest(EditDeveloper.Request request, Developer developer) {
        developer.setDeveloperLevel(request.getDeveloperLevel());
        developer.setDeveloperSkillType(request.getDeveloperSkillType());
        developer.setExperienceYears(request.getExperienceYears());
        return developer;
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

    private Developer createDeveloperFromRequest(CreateDeveloper.Request request) {
        return Developer.builder()
                .developerLevel(request.getDeveloperLevel())
                .developerSkillType(request.getDeveloperSkillType())
                .experienceYears(request.getExperienceYears())
                .name(request.getName())
                .age(request.getAge())
                .memberId(request.getMemberId())
                .statusCode(EMPLOYED)
                .build();
    }

    private void validateCreateDeveloperRequest(@NonNull CreateDeveloper.Request request) {
        // business validation
//        validateDeveloperLevel(request.getDeveloperLevel(), request.getExperienceYears());
        request.getDeveloperLevel().validateExperienceYears(request.getExperienceYears());

        developerRepository.findByMemberId(request.getMemberId())
                .ifPresent((developer -> {
                    throw new DMakerException(DUPLICATED_MEMBER_ID);
                }));
    }

    private void validateDeveloperLevel(@NonNull DeveloperLevel developerLevel, @NonNull Integer experienceYears) {
        // business validation
        developerLevel.validateExperienceYears(experienceYears);

//        if(experienceYears < developerLevel.getMinExperienceYears() || experienceYears > developerLevel.getMaxExperienceYears())
//            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCED);

//        if (developerLevel == DeveloperLevel.SENIOR
//                && experienceYears < MIN_SENIOR_EXPERIENCE_YEARS)
//            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCED);
//
//        if (developerLevel == DeveloperLevel.JUNGNIOR
//                && (experienceYears < MAX_JUNIOR_EXPERIENCE_YEARS || experienceYears > MIN_SENIOR_EXPERIENCE_YEARS))
//            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCED);
//
//        if (developerLevel == DeveloperLevel.JUNIOR && experienceYears > MAX_JUNIOR_EXPERIENCE_YEARS)
//            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCED);
    }
}
