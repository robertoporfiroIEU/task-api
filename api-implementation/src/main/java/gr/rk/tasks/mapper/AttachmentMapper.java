package gr.rk.tasks.mapper;

import gr.rk.tasks.V1.dto.AttachmentDTO;
import gr.rk.tasks.entity.Attachment;
import gr.rk.tasks.security.UserPrincipal;
import gr.rk.tasks.util.Util;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = { Util.class, UUID.class })
public abstract class AttachmentMapper {

    @Autowired
    protected UserPrincipal userPrincipal;

    @Mapping(target = "applicationUser", expression = "java(userPrincipal.getClientName())")
    @Mapping(target = "identifier", expression = "java((attachmentDTO.getIdentifier().toString()))")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "uploadedAt")
    @Mapping(ignore = true, target = "deleted")
    @Mapping(ignore = true, target = "comments")
    public abstract Attachment toAttachment(AttachmentDTO attachmentDTO);

    @Mapping(target = "identifier", expression = "java(UUID.fromString(attachment.getIdentifier()))")
    @Mapping(target = "uploadedAt", expression = "java(Util.toDateISO8601WithTimeZone(attachment.getUploadedAt()))")
    @Mapping(target = "createdBy", source = "createdBy")
    public abstract AttachmentDTO toAttachmentDTO(Attachment attachment);

    protected abstract List<AttachmentDTO> toAttachmentsDTOList(List<Attachment> attachments);
}
