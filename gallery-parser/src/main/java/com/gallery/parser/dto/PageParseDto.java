package com.gallery.parser.dto;

import lombok.Data;

@Data
public class PageParseDto {
    private String url;
    private int endCountPage;
    private String folderName;
}
