package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findCommentByTaskIdentifierAndApplicationUserAndDeleted(String identifier, String applicationUser, boolean deleted, Pageable pageable);
    Optional<Comment> findCommentByIdentifierAndApplicationUserAndDeleted(String identifier, String applicationUser, boolean deleted);
}
