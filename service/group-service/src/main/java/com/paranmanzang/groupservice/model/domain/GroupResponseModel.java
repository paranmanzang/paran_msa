package com.paranmanzang.groupservice.model.domain;

import com.paranmanzang.groupservice.model.entity.Group;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupResponseModel {

    private Long id;
    private String name;
    private String categoryName;
    private LocalDateTime createAt;
    private boolean enabled;
    private String detail;
    private String nickname;  // 관리자 nickname
    private String chatRoomId;

    public static GroupResponseModel fromEntity(Group group) {
        return GroupResponseModel.builder()
                .id(group.getId())
                .name(group.getName())
                .categoryName(group.getCategoryName())
                .createAt(group.getCreateAt())
                .enabled(group.isEnabled())
                .detail(group.getDetail())
                .nickname(group.getNickname())
                .chatRoomId(group.getChatRoomId())
                .build();
    }
}