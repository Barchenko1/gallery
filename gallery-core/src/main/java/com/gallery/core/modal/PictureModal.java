package com.gallery.core.modal;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PictureModal {
    private String objectKey;
    private String url;
    private Date lastModified;
}
