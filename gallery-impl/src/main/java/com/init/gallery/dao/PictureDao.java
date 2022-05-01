package com.init.gallery.dao;

import com.init.core.dao.IPictureDao;
import com.init.gallery.modal.Picture;

import java.util.List;
import java.util.Optional;

public class PictureDao implements IPictureDao<Picture> {

    @Override
    public Optional<Picture> getPictureByName(String name) {
        return Optional.empty();
    }

    @Override
    public List<Picture> getAllPictures() {
        return null;
    }
}
