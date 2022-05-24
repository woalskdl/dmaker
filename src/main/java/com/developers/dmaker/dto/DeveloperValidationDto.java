package com.developers.dmaker.dto;

import com.developers.dmaker.entity.Developer;
import com.developers.dmaker.exception.DMakerErrorCode;
import com.developers.dmaker.type.DeveloperLevel;
import com.developers.dmaker.type.DeveloperSkillType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeveloperValidationDto {
    private DMakerErrorCode errorCode;
    private String errorMessage;
}
