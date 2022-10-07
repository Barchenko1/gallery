package com.gallery.layer.service;

import com.gallery.core.modal.PictureModal;
import com.gallery.core.service.IS3BucketService;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@PropertySource("classpath:application.properties")
public class S3BucketService implements IS3BucketService {
    @Override
    public void addPictureToFolder(PictureModal pictureModal, String folderName) {

    }

    @Override
    public void addFolder(String name) {

    }

    @Override
    public List<PictureModal> getFolderPictureList(String folderName) {
        return null;
    }

    @Override
    public List<PictureModal> getAllPictures() {
        return null;
    }

    @Override
    public List<PictureModal> getPicturesChunk(int chunk) {
        return null;
    }

    @Override
    public Optional<PictureModal> getPictureByName(String name) {
        return Optional.empty();
    }

    @Override
    public void deletePictureByName(String name) {

    }

    @Override
    public void deleteFolderByName(String folderName) {

    }
}
