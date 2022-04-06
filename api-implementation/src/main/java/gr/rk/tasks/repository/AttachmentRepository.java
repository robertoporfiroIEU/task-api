package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    Optional<Attachment> findAttachmentByIdentifierAndApplicationUserAndDeleted(String identifier, String applicationUser, boolean deleted);
    Set<Attachment> findByIdentifierIn(Set<String> identifiers);
}
