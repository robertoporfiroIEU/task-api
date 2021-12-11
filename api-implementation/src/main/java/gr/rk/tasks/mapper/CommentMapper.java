package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.CommentDTO;
import gr.rk.tasks.V1.dto.PaginatedCommentsDTO;
import gr.rk.tasks.entity.Comment;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.util.Util;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = { Util.class, UUID.class }, uses = { UserMapper.class })
public abstract class CommentMapper {

    @Autowired
    protected UserPrincipal userPrincipal;

    public PaginatedCommentsDTO toPaginatedCommentsDTO(Page<Comment> commentsEntity) {
        return new PaginatedCommentsDTO()
                .content(toCommentsDTOList(commentsEntity))
                .totalElements((int)commentsEntity.getTotalElements());

    }

    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getApplicationUser())")
    @Mapping(ignore = true, target = "identifier")
    @Mapping(ignore = true, target = "createdBy")
    public abstract Comment toComment(CommentDTO commentDTO);

    @Mapping(target = "identifier", expression = "java(UUID.fromString(comment.getIdentifier()))")
    @Mapping(target = "createdAt", expression = "java(Util.toDateISO8601WithTimeZone(comment.getCreatedAt()))")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "toUserDTO")
    public abstract CommentDTO toCommentDTO(Comment comment);

    protected abstract List<CommentDTO> toCommentsDTOList(Page<Comment> comments);
}
