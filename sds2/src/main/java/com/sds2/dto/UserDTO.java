package com.sds2.dto;

import java.io.Serializable;

public record UserDTO(
    Long id,
    String username,
    String email
) implements Serializable {
    
}
