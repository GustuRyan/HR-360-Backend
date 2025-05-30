package org.acme.model;

public record ProfileResponse(
        Long userId,
        Long employeeId,
        String message
) {
}
