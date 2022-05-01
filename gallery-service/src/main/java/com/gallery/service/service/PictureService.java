package com.gallery.service.service;

import com.gallery.core.service.IPictureService;
import com.gallery.service.dao.PictureDao;
import com.gallery.service.modal.Picture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PictureService implements IPictureService<Picture> {

    private final PictureDao pictureDao;

    @Autowired
    public PictureService(PictureDao pictureDao) {
        this.pictureDao = pictureDao;
    }

    @Override
    public Optional<Picture> getPictureByName(String name) {
        return pictureDao.getPictureByName(name);
    }

    @Override
    public List<Picture> getAllPictures() {
        return pictureDao.getAllPictures();
    }

    @Override
    @Transactional
    public void addPictures(List<Picture> pictures) {
        pictures.forEach(pictureDao::addPicture);
    }

    @Override
    @Transactional
    public void updatePicture(Picture picture) {
        pictureDao.updatePicture(picture);
    }

    @Override
    @Transactional
    public void deletePicture(Picture picture) {
        pictureDao.deletePicture(picture);
    }

}
