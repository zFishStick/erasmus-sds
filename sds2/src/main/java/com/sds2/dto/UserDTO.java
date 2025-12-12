package com.sds2.dto;

import java.io.Serializable;

public record UserDTO(
    Long id,
    String email,
    String username
) implements Serializable {
    
}
