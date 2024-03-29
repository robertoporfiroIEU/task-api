package gr.rk.tasks.service;

import gr.rk.tasks.dto.AttachmentWithFileContentDTO;
import gr.rk.tasks.entity.Attachment;
import gr.rk.tasks.exception.ApplicationException;
import gr.rk.tasks.exception.i18n.I18nErrorMessage;
import gr.rk.tasks.repository.AttachmentRepository;
import gr.rk.tasks.security.UserPrincipal;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class FileService {

    private final UserPrincipal userPrincipal;
    private final AttachmentRepository attachmentRepository;
    private final UserService userService;

    @Value("${applicationConfigurations.FileService.uploadDir}")
    private String UPLOAD_DIR;

    @Autowired
    public FileService(UserPrincipal userPrincipal, AttachmentRepository attachmentRepository, UserService userService) {
        this.userPrincipal = userPrincipal;
        this.attachmentRepository = attachmentRepository;
        this.userService = userService;
    }

    @Transactional
    public Attachment upload(String actualName, String createdBy, String description, byte[] fileContent) {
        try {
            // validations
            if (!userService.isUserExist(createdBy)) {
                throw new ApplicationException(I18nErrorMessage.USER_NOT_FOUND);
            }

            Attachment attachment = new Attachment();
            attachment.setName(actualName);

            // Get MIME TYPE
            Tika tika = new Tika();
            String mimeType = tika.detect(fileContent);

            attachment.setMimeType(mimeType);

            attachment.setDescription(description);
            attachment.setCreatedBy(createdBy);
            attachment.setApplicationUser(userPrincipal.getClientName());
            attachment = attachmentRepository.save(attachment);

            Path newFile = Paths.get(UPLOAD_DIR + attachment.getIdentifier() + "-" + actualName);
            Files.createDirectories(newFile.getParent());
            Files.write(newFile, fileContent);
            return attachment;

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public AttachmentWithFileContentDTO find(String identifier) {
        Optional<Attachment> oAttachment = attachmentRepository
                .findAttachmentByIdentifierAndApplicationUserAndDeleted(identifier, userPrincipal.getClientName(), false);

        if (oAttachment.isEmpty()) {
            throw new ApplicationException(I18nErrorMessage.ATTACHMENT_NOT_FOUND);
        }

        Attachment attachment = oAttachment.get();
        Resource fileContent = new FileSystemResource(Paths.get(UPLOAD_DIR + oAttachment.get().getFileSystemName()));
        AttachmentWithFileContentDTO attachmentWithFileContentDTO = new AttachmentWithFileContentDTO(attachment, fileContent);

        return attachmentWithFileContentDTO;
    }
}
