package com.gallery.layer.util;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class MultipartFileToFileConverter implements IMultipartFileToFileConverter<MultipartFile, File> {

    @Override
    public File convert(MultipartFile multipartFile) {
        File convertFile = new File(multipartFile.getName());
        try (OutputStream out = new FileOutputStream(convertFile)){
            out.write(multipartFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return convertFile;
    }
}
