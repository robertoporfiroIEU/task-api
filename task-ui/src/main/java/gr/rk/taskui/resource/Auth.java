package gr.rk.taskui.resource;

import gr.rk.tasks.V1.dto.GroupDTO;
import gr.rk.tasks.V1.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class Auth {


    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    public Auth(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @GetMapping("/auth/userDetails")
    public Mono<UserDTO> getUserDetails() {
        UserDTO userDTO = new UserDTO();
        return ReactiveSecurityContextHolder.getContext().map(ctx -> {
            OidcUser oidcUser = (OidcUser) ctx.getAuthentication().getPrincipal();
            userDTO.setName(oidcUser.getClaims().get("user_name").toString());

            if (Objects.nonNull(oidcUser.getClaims().get("email"))) {
                userDTO.setEmail(oidcUser.getClaims().get("email").toString());
            }

            if (Objects.nonNull(oidcUser.getClaims().get("realm_access"))) {
                final Map<String, List<String>> realmAccess = (Map<String, List<String>>) oidcUser.getClaims().get("realm_access");
                List<GroupDTO> groupsDTO = realmAccess.get("roles")
                        .stream()
                        .map(roleName -> new GroupDTO().name(roleName)) // prefix required by Spring Security for roles.
                        .collect(Collectors.toList());

                userDTO.setGroups(groupsDTO);
            }
            return userDTO;
        });
    }

    @GetMapping("/auth/logout")
    public Mono<Void> logout(
            ServerHttpResponse response,
            WebSession session,
            @AuthenticationPrincipal(expression = "idToken") OidcIdToken idToken,
            @RequestParam(name = "post_logout_redirect_uri") String postLogoutRedirectURI

    ) {
        String logoutURL = "";
        if (Objects.nonNull(idToken)) {
            logoutURL = this.clientRegistrationRepository
                    .findByRegistrationId("keycloak-spring-gateway-client")
                    .block().getProviderDetails()
                    .getConfigurationMetadata()
                    .get("end_session_endpoint")
                    .toString() + "?post_logout_redirect_uri=" + URLEncoder.encode(postLogoutRedirectURI, StandardCharsets.UTF_8) +
                    "&id_token_hint=" + idToken.getTokenValue();
        }

        session.invalidate();
        response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
        response.getHeaders().setLocation(URI.create(logoutURL));
        return response.setComplete();
    }
}
