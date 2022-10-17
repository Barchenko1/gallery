package com.gallery.layer.util;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

@Service
public class MultipartFileToFileConverter implements IMultipartFileToFileConverter<MultipartFile, File> {

    @Override
    public File convert(MultipartFile multipartFile) {
        if (multipartFile.getOriginalFilename() == null) {
            throw new NullPointerException();
        }
        File convertFile = new File(multipartFile.getOriginalFilename());
        try (OutputStream out = new FileOutputStream(convertFile)){
            out.write(multipartFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return convertFile;
    }
}
