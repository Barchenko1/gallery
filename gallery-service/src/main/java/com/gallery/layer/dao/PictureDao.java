package com.gallery.layer.dao;

import com.gallery.core.dao.IPictureDao;
import com.gallery.layer.modal.Picture;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PictureDao implements IPictureDao<Picture> {

    private static final String getPicture = "";
    private static final String getPictures = "";
    private static final String getPullPictures = "";
    private final SessionFactory sessionFactory;

    @Autowired
    public PictureDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<Picture> getPictureByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        Picture picture = session.get(Picture.class, name);
        return Optional.of(picture);
    }

    @Override
    public List<Picture> getAllPictures() {
        Session session = sessionFactory.getCurrentSession();
        List<Picture> pictures = session.createNativeQuery(getPictures, Picture.class).list();
        return pictures;
    }

    @Override
    public void addPicture(Picture picture) {
        Session session = sessionFactory.getCurrentSession();
        session.save(picture);
    }

    @Override
    public void updatePicture(Picture picture) {
        Session session = sessionFactory.getCurrentSession();
        session.update(picture);
    }

    @Override
    public void deletePicture(Picture picture) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(picture);
    }
}
