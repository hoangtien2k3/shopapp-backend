package com.hoangtien2k3.shopappbackend.services.impl;

import com.hoangtien2k3.shopappbackend.components.TranslateMessages;
import com.hoangtien2k3.shopappbackend.dtos.CommentDTO;
import com.hoangtien2k3.shopappbackend.exceptions.payload.DataNotFoundException;
import com.hoangtien2k3.shopappbackend.models.Comment;
import com.hoangtien2k3.shopappbackend.models.Product;
import com.hoangtien2k3.shopappbackend.models.User;
import com.hoangtien2k3.shopappbackend.repositories.CommentRepository;
import com.hoangtien2k3.shopappbackend.repositories.ProductRepository;
import com.hoangtien2k3.shopappbackend.repositories.UserRepository;
import com.hoangtien2k3.shopappbackend.responses.comment.CommentResponse;
import com.hoangtien2k3.shopappbackend.services.CommentService;
import com.hoangtien2k3.shopappbackend.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends TranslateMessages
        implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public Comment insertComment(CommentDTO commentDTO) {
        User user = userRepository.findById(commentDTO.getUserId()).orElse(null);
        Product product = productRepository.findById(commentDTO.getProductId()).orElse(null);

        if (user == null) {
            throw new DataNotFoundException(translate(MessageKeys.USER_NOT_FOUND));
        }
        if (product == null) {
            throw new DataNotFoundException(translate(MessageKeys.PRODUCT_NOT_FOUND));
        }

        Comment newComment = Comment.builder()
                .user(userRepository.findById(commentDTO.getUserId()).get())
                .product(productRepository.findById(commentDTO.getProductId()).get())
                .content(commentDTO.getContent())
                .build();

        return commentRepository.save(newComment);
    }

    @Transactional
    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void updateComment(Long id, CommentDTO commentDTO) throws DataNotFoundException {
        Comment existsComment = commentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(translate(MessageKeys.COMMENT_NOT_FOUND, id)));

        existsComment.setContent(commentDTO.getContent());
        commentRepository.save(existsComment);
    }

    @Override
    public List<CommentResponse> getCommentByUserAndProduct(Long userId, Long productId) {
        List<Comment> comments = commentRepository.findByUserIdAndProductId(userId, productId);
        return comments.stream().map(CommentResponse::fromComment).toList();
    }

    @Override
    public List<CommentResponse> getCommentByProduct(Long productId) {
        List<Comment> comments = commentRepository.findByProductId(productId);
        return comments.stream().map(CommentResponse::fromComment).toList();
    }

}
