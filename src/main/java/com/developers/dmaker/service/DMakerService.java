package com.developers.dmaker.service;

import com.developers.dmaker.entity.Developer;
import com.developers.dmaker.repository.DeveloperRepository;
import com.developers.dmaker.type.DeveloperLevel;
import com.developers.dmaker.type.DeveloperSkillType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@Service
@RequiredArgsConstructor
public class DMakerService {

    private final DeveloperRepository developerRepository;
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
    public void createDeveloper() {
        Developer developer = Developer.builder()
                .developerLevel(DeveloperLevel.JUNIOR)
                .developerSkillType(DeveloperSkillType.BACK_END)
                .experienceYears(2)
                .name("Olaf")
                .age(5)
                .build();

        developerRepository.save(developer);
        // assert multiple developer has benn built
        developerRepository.save(developer);
        developerRepository.save(developer);
    }

}
