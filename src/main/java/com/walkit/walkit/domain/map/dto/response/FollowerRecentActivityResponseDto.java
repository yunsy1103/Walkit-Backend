package com.walkit.walkit.domain.map.dto.response;

import com.walkit.walkit.domain.character.dto.response.ResponseCharacterWearDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowerRecentActivityResponseDto {

    private Long userId;
    private String nickName;
    private boolean walkedYesterday;
    private MapCharacterDto responseCharacterDto;
    private ResponseCharacterWearDto headImage;
    private ResponseCharacterWearDto bodyImage;
}
