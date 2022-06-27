package com.gallery.core.modal;

import lombok.Data;

import java.util.Date;

@Data
public class PictureModal {
    private String name;
    private String url;
    private Date dateOfCreate;
}
