package io.github.franciscopaulinoq.qmanager.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String prefix,
        boolean active,
        OffsetDateTime createdAt
) { }
