package io.github.franciscopaulinoq.qmanager.config;

import io.github.franciscopaulinoq.qmanager.model.Permission;
import io.github.franciscopaulinoq.qmanager.model.Role;
import io.github.franciscopaulinoq.qmanager.model.User;
import io.github.franciscopaulinoq.qmanager.repository.PermissionRepository;
import io.github.franciscopaulinoq.qmanager.repository.RoleRepository;
import io.github.franciscopaulinoq.qmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (roleRepository.count() == 0) {
            initData();
        }
    }

    private void initData() {
        Set<String> permissionNames = Set.of(
                "user:read", "user:create", "user:update", "user:delete", "user:reset-password",
                "ticket:read", "ticket:create", "ticket:update", "ticket:call",
                "category:read", "category:write",
                "priority:read", "priority:write",
                "role:read", "role:write"
        );

        List<Permission> savedPermissions = permissionNames.stream()
                .map(name -> permissionRepository.findByName(name)
                        .orElseGet(() -> permissionRepository.save(Permission.builder().name(name).build())))
                .toList();

        Role adminRole = createRole("ADMIN", "Super Administrator", new HashSet<>(savedPermissions));

        Set<Permission> managerPermissions = savedPermissions.stream()
                .filter(p -> !p.getName().equals("user:delete") &&
                        !p.getName().equals("user:reset-password") &&
                        !p.getName().startsWith("role:"))
                .collect(Collectors.toSet());
        createRole("MANAGER", "Manager", managerPermissions);

        Set<Permission> operatorPermissions = savedPermissions.stream()
                .filter(p -> p.getName().startsWith("ticket:"))
                .collect(Collectors.toSet());
        createRole("OPERATOR", "Ticket Operator", operatorPermissions);

        if (userRepository.findByEmail("admin@admin.com").isEmpty()) {
            User admin = User.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("admin"))
                    .active(true)
                    .roles(Set.of(adminRole))
                    .build();
            userRepository.save(admin);
        }
    }

    private Role createRole(String name, String description, Set<Permission> permissions) {
        return roleRepository.save(Role.builder()
                .name(name)
                .description(description)
                .permissions(permissions)
                .active(true)
                .build());
    }
}
