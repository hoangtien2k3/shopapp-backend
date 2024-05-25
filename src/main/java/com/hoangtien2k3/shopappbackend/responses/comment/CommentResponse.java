package com.hoangtien2k3.shopappbackend.responses.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hoangtien2k3.shopappbackend.models.Comment;
import com.hoangtien2k3.shopappbackend.responses.user.UserResponse;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    @JsonProperty("content")
    private String content;

    // user's infomation
    @JsonProperty("user")
    private UserResponse userResponse;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public static CommentResponse fromComment(Comment comment) {
        return CommentResponse.builder()
                .content(comment.getContent())
                .userResponse(UserResponse.fromUser(comment.getUser()))
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

}
