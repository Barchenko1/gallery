package com.gallery.layer.util;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class InStreamToByteConverter implements IInStreamToByteConverter<S3ObjectInputStream, byte[]> {

    @Override
    public byte[] convert(S3ObjectInputStream s3ObjectInputStream) {
        try {
            return IOUtils.toByteArray(s3ObjectInputStream);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
}
