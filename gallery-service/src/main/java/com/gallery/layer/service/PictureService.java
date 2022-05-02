package com.gallery.layer.service;

import com.gallery.core.service.IPictureService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Service
public class PictureService implements IPictureService {

    @Override
    public List<File> getPicturesByFolderName(String name) {

        return Collections.emptyList();
    }

    @Override
    public List<File> getAllPictures() {
        return null;
    }

    @Override
    public List<File> getPicturePull(int start, int end) {
        return null;
    }

    @Override
    public void addPictures(List<File> pictures) {

    }

    @Override
    public void deletePicture(File picture) {

    }
}
