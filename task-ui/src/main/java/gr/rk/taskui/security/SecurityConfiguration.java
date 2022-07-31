package gr.rk.taskui.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.*;
import java.util.stream.Collectors;

import static gr.rk.taskui.security.Constants.*;

@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean()
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf().disable().authorizeExchange()
                .pathMatchers("/actuators/**")
                .permitAll()
             .and()
                .authorizeExchange()
                .pathMatchers("/auth/logout")
                .permitAll()
             .and()
                .authorizeExchange()
                .pathMatchers(
                        HttpMethod.GET, "**"
                )
                .hasAnyRole(CONSULTATION_ROLE, DEVELOPER_ROLE, LEADER_ROLE, PROJECT_MANAGER_ROLE, ADMIN_ROLE)
             .and()
                .authorizeExchange()
                .pathMatchers(
                        HttpMethod.POST, USERS_ENDPOINT_PATTERN
                )
                .hasAnyRole(CONSULTATION_ROLE, DEVELOPER_ROLE, LEADER_ROLE, PROJECT_MANAGER_ROLE, ADMIN_ROLE)
             .and()
                .authorizeExchange()
                .pathMatchers(HttpMethod.POST, COMMENTS_ENDPOINT_PATTERN, FILE_ENDPOINT_PATTERN)
                .hasAnyRole(DEVELOPER_ROLE, LEADER_ROLE, PROJECT_MANAGER_ROLE, ADMIN_ROLE)
                .pathMatchers(HttpMethod.PUT, COMMENTS_ENDPOINT_PATTERN)
                .hasAnyRole(DEVELOPER_ROLE, LEADER_ROLE, PROJECT_MANAGER_ROLE, ADMIN_ROLE)
             .and()
                .authorizeExchange()
                .pathMatchers(HttpMethod.POST, TASKS_ENDPOINT_PATTERN)
                .hasAnyRole(LEADER_ROLE, PROJECT_MANAGER_ROLE, ADMIN_ROLE)
                .pathMatchers(HttpMethod.PUT, TASKS_ENDPOINT_PATTERN)
                .hasAnyRole(DEVELOPER_ROLE, LEADER_ROLE, PROJECT_MANAGER_ROLE, ADMIN_ROLE)
                .pathMatchers(HttpMethod.DELETE, TASKS_ENDPOINT_PATTERN)
                .hasAnyRole(LEADER_ROLE, PROJECT_MANAGER_ROLE, ADMIN_ROLE)
             .and()
                .authorizeExchange()
                .pathMatchers(HttpMethod.POST, PROJECTS_ENDPOINT_PATTERN)
                .hasAnyRole(PROJECT_MANAGER_ROLE, ADMIN_ROLE)
                .pathMatchers(HttpMethod.PUT, PROJECTS_ENDPOINT_PATTERN)
                .hasAnyRole(PROJECT_MANAGER_ROLE, ADMIN_ROLE)
                .pathMatchers(HttpMethod.DELETE, PROJECTS_ENDPOINT_PATTERN)
                .hasAnyRole(PROJECT_MANAGER_ROLE, ADMIN_ROLE)
             .and()
                .authorizeExchange()
                .pathMatchers("**")
                .hasAnyRole(ADMIN_ROLE)
             .and()
                .authorizeExchange()
                .anyExchange()
                .authenticated()
             .and()
                .oauth2Login()
                .and().logout().disable();
        return http.build();
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapperForKeycloak() {
        return authorities -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            GrantedAuthority authority = authorities.iterator().next();
            boolean isOidc = authority instanceof OidcUserAuthority;

            if (isOidc) {
                var oidcUserAuthority = (OidcUserAuthority) authority;
                var userInfo = oidcUserAuthority.getUserInfo();

                if (userInfo.hasClaim("realm_access")) {
                    var realmAccess = userInfo.getClaimAsMap("realm_access");
                    var roles = (Collection<String>) realmAccess.get("roles");
                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
                }
            } else {
                var oauth2UserAuthority = (OAuth2UserAuthority) authority;
                Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();

                if (userAttributes.containsKey("realm_access")) {
                    var realmAccess =  (Map<String,Object>) userAttributes.get("realm_access");
                    var roles =  (Collection<String>) realmAccess.get("roles");
                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
                }
            }

            return mappedAuthorities;
        };
    }

    Collection<GrantedAuthority> generateAuthoritiesFromClaim(Collection<String> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}
