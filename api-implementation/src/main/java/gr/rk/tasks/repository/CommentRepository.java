package gr.rk.tasks.repository;

import gr.rk.tasks.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findCommentByIdentifier(String identifier);

    Page<Comment> findCommentByTaskIdAndApplicationUser(Long id, String applicationUser, Pageable pageable);
}
