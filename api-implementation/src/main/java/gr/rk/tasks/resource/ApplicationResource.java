package gr.rk.tasks.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class ApplicationResource {

    @GetMapping("/application")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Principal> isAlive() {
        Principal principal = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(principal);
    }
}
