package gr.rk.taskui.resource;

import gr.rk.taskui.dto.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class Auth {


    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    public Auth(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @GetMapping("/auth/csrf")
    public CsrfToken csrfToken(ServerWebExchange exchange) {
        Mono<CsrfToken> csrfToken = (Mono<CsrfToken>) exchange.getAttributes().get(CsrfToken.class.getName());
        return csrfToken.block();
    }

    @GetMapping("/auth/userDetails")
    public Mono<UserPrincipal> getUserDetails() {
        UserPrincipal userPrincipal = new UserPrincipal();
        return ReactiveSecurityContextHolder.getContext().map(ctx -> {
            OidcUser oidcUser = (OidcUser) ctx.getAuthentication().getPrincipal();
            userPrincipal.setName(oidcUser.getClaims().get("user_name").toString());

            if (Objects.nonNull(oidcUser.getClaims().get("email"))) {
                userPrincipal.setEmail(oidcUser.getClaims().get("email").toString());
            }

            if (Objects.nonNull(oidcUser.getClaims().get("realm_access"))) {
                final Map<String, List<String>> realmAccess = (Map<String, List<String>>) oidcUser.getClaims().get("realm_access");
                List<String> roles = realmAccess.get("roles");
                userPrincipal.setRoles(roles);
            }

            if (Objects.nonNull(oidcUser.getClaims().get("groups"))) {
                List<String> groupsDTO = (List<String>) oidcUser.getClaims().get("groups");
                userPrincipal.setGroups(groupsDTO);
            }
            return userPrincipal;
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
