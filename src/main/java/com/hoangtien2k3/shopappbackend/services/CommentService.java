package com.hoangtien2k3.shopappbackend.services;

import com.hoangtien2k3.shopappbackend.dtos.CommentDTO;
import com.hoangtien2k3.shopappbackend.exceptions.payload.DataNotFoundException;
import com.hoangtien2k3.shopappbackend.models.Comment;
import com.hoangtien2k3.shopappbackend.responses.comment.CommentResponse;

import java.util.List;

public interface CommentService {
    Comment insertComment(CommentDTO comment);

    void deleteComment(Long id);

    void updateComment(Long id, CommentDTO comment) throws DataNotFoundException;

    List<CommentResponse> getCommentByUserAndProduct(Long userId, Long productId);

    List<CommentResponse> getCommentByProduct(Long productId);
}
