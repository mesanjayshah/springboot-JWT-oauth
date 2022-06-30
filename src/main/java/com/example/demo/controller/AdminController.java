package com.example.demo.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.entity.Administrator;
import com.example.demo.entity.Role;
import com.example.demo.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.demo.constants.SecurityConstants.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminController {

    private final AdminService adminService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "newTopic";

    @GetMapping("/publish/{message}")
    public String publishMessage(@PathVariable("message") String message) {
        kafkaTemplate.send(TOPIC, message);
        return "message published";
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<Administrator>> getAdmins() {
        return ResponseEntity.ok(adminService.getAdmins());
    }

    @PostMapping("/addmin/save")
    public ResponseEntity<Administrator> saveAdmin(@RequestBody Administrator admin) {
        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/admin/save").buildAndExpand().toUriString());
        return ResponseEntity.created(uri).body(adminService.saveAdmin(admin));
    }

    @PostMapping("/role/save")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/role/save").buildAndExpand().toUriString());
        return ResponseEntity.created(uri).body(adminService.saveRole(role));
    }

    @PostMapping("/role/addtoadmin")
    public ResponseEntity<?> addRoleToAdmin(@RequestBody RoleToAdminFrom form) {
        adminService.addRoleToAdmin(form.username(), form.roleName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/get-by-username")
    public ResponseEntity<Administrator> getAdminByUsername(@RequestParam String username) {
        return ResponseEntity.ok(adminService.getAdminByUsername(username));
    }

    @PostMapping("/token/refresh")
    public void getAdminByUsername(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null || authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256(SECRET);
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                Administrator admin = adminService.getAdminByUsername(username);

                List<String> roles = admin.getRoles().stream().map(Role::getName).collect(Collectors.toList());

                String access_token = JWT.create()
                        .withSubject(admin.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                        .withIssuer(request.getRequestURI())
                        .withClaim("roles", roles)
                        .withClaim("device_id", !roles.contains("ROLE_ADMIN") ? admin.getUsername() : null)
                        .sign(algorithm);

                response.addHeader("access_token", access_token);
                response.addHeader("refresh_token", refresh_token);
                Map<String, String> map = new HashMap<>();
                map.put("access_token", access_token);
                map.put("refresh_token", refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), map);

            } catch (Exception e) {
                response.setHeader("error", e.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> map = new HashMap<>();
                map.put("error_message", e.getMessage());

                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), map);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

}

record RoleToAdminFrom(
        String username,
        String roleName
) {
}

