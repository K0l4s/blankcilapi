package com.blankcil.api.blankcilapi.service;

import com.blankcil.api.blankcilapi.entity.CommentEntity;
import com.blankcil.api.blankcilapi.model.CommentModel;
import com.blankcil.api.blankcilapi.model.ParentCommentModel;
import com.blankcil.api.blankcilapi.model.UserCommentModel;
import com.blankcil.api.blankcilapi.model.UserModel;
import com.blankcil.api.blankcilapi.repository.CommentRepository;
import com.blankcil.api.blankcilapi.repository.PodcastRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements ICommentService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Page<CommentModel> getCommentsForPodcast(int podcastId, Pageable pageable) {
        // Cập nhật totalLikes cho tất cả các bình luận trước khi truy vấn danh sách bình luận
        commentRepository.updateTotalLikesForComments();

        Page<Object[]> commentPage = commentRepository.getCommentsWithTotalLikesForPodcast((long) podcastId, pageable);

        // Chuyển đổi danh sách các đối tượng Object[] thành danh sách các CommentModel
        Page<CommentModel> commentModelPage = commentPage.map(object -> {
                    CommentEntity commentEntity = (CommentEntity) object[0];
                    Long totalLikes = (Long) object[1];

                    // Cập nhật totalLikes cho mỗi CommentEntity
                    commentEntity.setTotalLikes(totalLikes);

                    // Cập nhật totalLikes cho các bình luận trong replies
                    List<CommentEntity> replies = commentEntity.getReplies();
                    for (CommentEntity reply : replies) {
                        long replyTotalLikes = reply.getComment_likes().size();
                        reply.setTotalLikes(replyTotalLikes);

                        // Ánh xạ thông tin user_comment cho parentComment của reply
                        if (reply.getParentComment() != null) {
                            ParentCommentModel parentCommentModel = modelMapper.map(reply.getParentComment(), ParentCommentModel.class);
                            parentCommentModel.setUser_comment(modelMapper.map(reply.getParentComment().getUser_comment(), UserCommentModel.class));
                        }
                    }

                    // Tiếp tục ánh xạ các thông tin còn lại từ CommentEntity sang CommentModel
                    CommentModel commentModel = modelMapper.map(commentEntity, CommentModel.class);

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
}
