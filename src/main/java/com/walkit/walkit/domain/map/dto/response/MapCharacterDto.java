package com.walkit.walkit.domain.map.dto.response;

import com.walkit.walkit.domain.character.entity.Character;
import com.walkit.walkit.domain.character.enums.Grade;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MapCharacterDto {

    private Grade grade;
    private String headImageName;
    private String bodyImageName;

    public static MapCharacterDto from(Character character) {
        return MapCharacterDto.builder()
                .grade(character.getGrade())
                .headImageName(character.getHeadImageName())
                .bodyImageName(character.getBodyImageName())
                .build();
    }
}
