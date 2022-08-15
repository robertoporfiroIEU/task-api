package gr.rk.tasks.resource;

import gr.rk.tasks.V1.api.FileApi;
import gr.rk.tasks.V1.dto.AttachmentDTO;
import gr.rk.tasks.dto.AttachmentWithFileContentDTO;
import gr.rk.tasks.entity.Attachment;
import gr.rk.tasks.mapper.AttachmentMapper;
import gr.rk.tasks.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileResource implements FileApi {

    private final FileService fileService;
    private final AttachmentMapper attachmentMapper;

    @Autowired
    public FileResource(FileService fileService, AttachmentMapper attachmentMapper) {
        this.fileService = fileService;
        this.attachmentMapper = attachmentMapper;
    }

    @Override
    @Secured("ROLE_READ_FILE")
    public ResponseEntity<Resource> getFileContent(String identifier) {
        HttpHeaders headers = new HttpHeaders();

        AttachmentWithFileContentDTO attachmentWithFileContentDTO = fileService.find(identifier);

        headers.add(HttpHeaders.CONTENT_DISPOSITION, "filename=" + attachmentWithFileContentDTO.getAttachment().getName());
        headers.add(HttpHeaders.CONTENT_TYPE, attachmentWithFileContentDTO.getAttachment().getMimeType());

        return ResponseEntity.ok().headers(headers).body(attachmentWithFileContentDTO.getFileContent());
    }

    @Override
    @Secured("ROLE_CREATE_FILE")
    public ResponseEntity<AttachmentDTO> upload(String createdBy, String description, MultipartFile fileContent){
        Attachment attachment = null;
        try {
            attachment = fileService.upload(fileContent.getOriginalFilename(), createdBy, description, fileContent.getBytes());
        } catch (IOException e) {
            new RuntimeException(e);
        }

        AttachmentDTO attachmentDTOResponse = attachmentMapper.toAttachmentDTO(attachment);
        return ResponseEntity.ok(attachmentDTOResponse);
    }
}
