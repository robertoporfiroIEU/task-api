package gr.rk.tasks.dto;

import gr.rk.tasks.entity.Attachment;
import org.springframework.core.io.Resource;

public class AttachmentWithFileContentDTO {
    private Attachment attachment;
    private Resource fileContent;

    public AttachmentWithFileContentDTO(Attachment attachment, Resource fileContent) {
        this.attachment = attachment;
        this.fileContent = fileContent;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public Resource getFileContent() {
        return fileContent;
    }

    public void setFileContent(Resource fileContent) {
        this.fileContent = fileContent;
    }
}
