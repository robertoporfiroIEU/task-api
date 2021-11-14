package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.CommentDTO;
import gr.rk.tasks.entity.Comment;
import gr.rk.tasks.repository.UserRepository;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.util.Util;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = { Util.class, UUID.class }, uses = { UserMapper.class })
public abstract class CommentMapper {

    @Autowired
    protected UserPrincipal userPrincipal;
    @Autowired
    protected UserRepository userRepository;

    public Page<CommentDTO> toPageCommentsDTO(Page<Comment> commentsEntity) {
        return new PageImpl<>( toCommentsDTOList(commentsEntity), commentsEntity.getPageable(), commentsEntity.getTotalElements());
    }

    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getApplicationUser())")
    @Mapping(target = "createdBy", expression = "java(userRepository.findById(commentDTO.getCreatedBy().getName()).orElse(null))")
    public abstract Comment toComment(CommentDTO commentDTO);

    @Mapping(target = "identifier", expression = "java(UUID.fromString(comment.getIdentifier()))")
    @Mapping(target = "creationDate", expression = "java(Util.toDateISO8601WithTimeZone(comment.getCreatedAt()))")
    public abstract CommentDTO toCommentDTO(Comment comment);

    protected abstract List<CommentDTO> toCommentsDTOList(Page<Comment> comments);
}
