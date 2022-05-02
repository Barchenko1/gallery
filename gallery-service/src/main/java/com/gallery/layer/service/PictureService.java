package com.gallery.layer.service;

import com.gallery.core.service.IPictureService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PictureService<T> implements IPictureService<T> {
    @Override
    public Optional<T> getPictureByName(String name) {
        return Optional.empty();
    }

    @Override
    public List<T> getAllPictures() {
        return null;
    }

    @Override
    public List<T> getPicturePull(int start, int end) {
        return null;
    }

    @Override
    public void addPictures(List<T> pictures) {

    }

    @Override
    public void deletePicture(T picture) {

    }
}
