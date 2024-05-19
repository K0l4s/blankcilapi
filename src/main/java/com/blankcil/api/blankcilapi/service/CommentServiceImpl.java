package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.entity.CommentEntity;
import com.blankcil.api.blankcilapi.entity.UserEntity;
import com.blankcil.api.blankcilapi.model.*;
import com.blankcil.api.blankcilapi.repository.CommentRepository;
import com.blankcil.api.blankcilapi.repository.PodcastRepository;
import com.blankcil.api.blankcilapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements ICommentService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<CommentModel> getCommentsForPodcast(int podcastId, Pageable pageable) {
        // Cập nhật totalLikes cho tất cả các bình luận trước khi truy vấn danh sách bình luận
        commentRepository.updateTotalLikesForComments();
        updateTotalRepliesForComments();

        Page<Object[]> commentPage = commentRepository.getCommentsWithTotalLikesAndTotalRepliesForPodcast((long) podcastId, pageable);

        // Chuyển đổi danh sách các đối tượng Object[] thành danh sách các CommentModel
        Page<CommentModel> commentModelPage = commentPage.map(object -> {
                    CommentEntity commentEntity = (CommentEntity) object[0];
                    Long totalLikes = (Long) object[1];
                    long totalReplies = countTotalReplies(commentEntity);

                    // Cập nhật totalLikes cho mỗi CommentEntity
                    commentEntity.setTotalLikes(totalLikes);
                    commentEntity.setTotalReplies(totalReplies);

                    // Cập nhật totalLikes cho các bình luận trong replies
                    List<CommentEntity> replies = commentEntity.getReplies();
                    for (CommentEntity reply : replies) {
                        long replyTotalLikes = reply.getComment_likes().size();
                        long replyTotalReplies = reply.getReplies().size();
                        reply.setTotalLikes(replyTotalLikes);
                        reply.setTotalReplies(replyTotalReplies);

                        // Ánh xạ thông tin user_comment cho parentComment của reply
                        if (reply.getParentComment() != null) {
                            ParentCommentModel parentCommentModel = modelMapper.map(reply.getParentComment(), ParentCommentModel.class);
                            parentCommentModel.setUser_comment(modelMapper.map(reply.getParentComment().getUser_comment(), UserCommentModel.class));
                        }
                    }

                    // Tiếp tục ánh xạ các thông tin còn lại từ CommentEntity sang CommentModel
                    CommentModel commentModel = modelMapper.map(commentEntity, CommentModel.class);
                    commentModel.setTotalReplies((int) totalReplies);

                    // Tiếp tục ánh xạ dữ liệu user_comment cho parentComment nếu có
                    if (commentEntity.getParentComment() != null) {
                        ParentCommentModel parentCommentModel = modelMapper.map(commentEntity.getParentComment(), ParentCommentModel.class);
                        parentCommentModel.setUser_comment(modelMapper.map(commentEntity.getParentComment().getUser_comment(), UserCommentModel.class));
                        commentModel.setParentComment(parentCommentModel);
                    }
                    return commentModel;
        });
        return commentModelPage;
    }

    @Override
    public Page<CommentModel> getCommentsForPodcastWithAuth(int podcastId, Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userEmail = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        // Cập nhật totalLikes cho tất cả các bình luận trước khi truy vấn danh sách bình luận
        commentRepository.updateTotalLikesForComments();
        updateTotalRepliesForComments();

        Page<Object[]> commentPage = commentRepository.getCommentsWithTotalLikesAndTotalRepliesForPodcast((long) podcastId, pageable);

        // Chuyển đổi danh sách các đối tượng Object[] thành danh sách các CommentModel
        Page<CommentModel> commentModelPage = commentPage.map(object -> {
            CommentEntity commentEntity = (CommentEntity) object[0];
            Long totalLikes = (Long) object[1];
            long totalReplies = countTotalReplies(commentEntity);

            // Cập nhật totalLikes cho mỗi CommentEntity
            commentEntity.setTotalLikes(totalLikes);
            commentEntity.setTotalReplies(totalReplies);

            // Cập nhật totalLikes cho các bình luận trong replies
            List<CommentEntity> replies = commentEntity.getReplies();
            for (CommentEntity reply : replies) {
                long replyTotalLikes = reply.getComment_likes().size();
                long replyTotalReplies = reply.getReplies().size();
                reply.setTotalLikes(replyTotalLikes);
                reply.setTotalReplies(replyTotalReplies);

                // Ánh xạ thông tin user_comment cho parentComment của reply
                if (reply.getParentComment() != null) {
                    ParentCommentModel parentCommentModel = modelMapper.map(reply.getParentComment(), ParentCommentModel.class);
                    parentCommentModel.setUser_comment(modelMapper.map(reply.getParentComment().getUser_comment(), UserCommentModel.class));
                }
            }

            // Tiếp tục ánh xạ các thông tin còn lại từ CommentEntity sang CommentModel
            CommentModel commentModel = modelMapper.map(commentEntity, CommentModel.class);
            commentModel.setTotalReplies((int) totalReplies);

            //Kiem tra xem user dang login da like podcast nay chua ?
            boolean hasLiked = commentEntity.getComment_likes().stream()
                    .anyMatch(commentLikeEntity -> commentLikeEntity.getUser_comment_like().equals(userEntity));
            commentModel.setHasLiked(hasLiked);

            // Tiếp tục ánh xạ dữ liệu user_comment cho parentComment nếu có
            if (commentEntity.getParentComment() != null) {
                ParentCommentModel parentCommentModel = modelMapper.map(commentEntity.getParentComment(), ParentCommentModel.class);
                parentCommentModel.setUser_comment(modelMapper.map(commentEntity.getParentComment().getUser_comment(), UserCommentModel.class));
                commentModel.setParentComment(parentCommentModel);
            }
            return commentModel;
        });
        return commentModelPage;
    }

    @Override
    public List<ReplyCommentModel> getReplies(long commentId) {
        commentRepository.updateTotalLikesForComments();
        Optional<CommentEntity> parentComment = commentRepository.findById(commentId);
        if (parentComment.isPresent()) {
            List<CommentEntity> replies = parentComment.get().getReplies();
            return replies.stream()
                    .map(commentEntity -> modelMapper.map(commentEntity, ReplyCommentModel.class))
                    .collect(Collectors.toList());
        }
        else {
            return null;
        }
    }

    @Transactional
    public void updateTotalRepliesForComments() {
        List<CommentEntity> comments = commentRepository.findAll();

        for (CommentEntity comment : comments) {
            long totalReplies = countTotalReplies(comment);
            comment.setTotalReplies(totalReplies);
            commentRepository.save(comment);
        }
    }

    private long countTotalReplies(CommentEntity comment) {
        return comment.getReplies().size();
    }
}
