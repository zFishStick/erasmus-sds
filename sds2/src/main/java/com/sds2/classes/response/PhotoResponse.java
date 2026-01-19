package com.sds2.classes.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class PhotoResponse {
    private String name;
    private String photoUri;
}