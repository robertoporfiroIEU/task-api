package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.CommentDTO;
import gr.rk.tasks.V1.dto.UserDTO;
import gr.rk.tasks.entity.Comment;
import gr.rk.tasks.entity.User;
import gr.rk.tasks.repository.UserRepository;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CommentMapper {

    private final UserPrincipal userPrincipal;
    private final UserRepository userRepository;

    @Autowired
    public CommentMapper(UserPrincipal userPrincipal, UserRepository userRepository) {
        this.userPrincipal = userPrincipal;
        this.userRepository = userRepository;
    }

    public Page<CommentDTO> toPageCommentDTO(Page<Comment> commentsEntity) {
        List<CommentDTO> commentsDTO = new ArrayList<>();
        commentsEntity.forEach(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setIdentifier(UUID.fromString(comment.getIdentifier()));
            commentDTO.setText(comment.getText());

            UserDTO userDTO = new UserDTO();
            commentDTO.setCreatedBy(userDTO.name(comment.getCreatedBy().getUsername()));
            commentDTO.setCreationDate(Util.toDateISO8601WithTimeZone(comment.getCreatedAt()));
            commentsDTO.add(commentDTO);
        });

        return new PageImpl<>(commentsDTO, commentsEntity.getPageable(), commentsEntity.getTotalElements());
    }

    public Comment toComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setText(commentDTO.getText());

        User user = this.userRepository.findById(commentDTO.getCreatedBy().getName()).get();
        comment.setCreatedBy(user);
        comment.setApplicationUser(userPrincipal.getApplicationUser());
        return comment;
    }

    public CommentDTO toCommentDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        // set values
        commentDTO.setIdentifier(UUID.fromString(comment.getIdentifier()));
        commentDTO.setText(comment.getText());
        // create userDTO to connect with the CommentDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setName(comment.getCreatedBy().getUsername());
        commentDTO.setCreatedBy(userDTO);
        commentDTO.setCreationDate(Util.toDateISO8601WithTimeZone(comment.getCreatedAt()));
        return commentDTO;
    }
}
