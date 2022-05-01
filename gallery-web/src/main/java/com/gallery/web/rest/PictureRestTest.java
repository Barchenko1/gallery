package com.gallery.web.rest;

import com.gallery.layer.modal.Picture;
import com.gallery.layer.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PictureRestTest {
    private final PictureService pictureService;

    @Autowired
    public PictureRestTest(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    @RequestMapping(value = "/pictures", method = RequestMethod.GET, produces = "application/json")
    public List<Picture> getPictures() {
        return pictureService.getAllPictures();
    }
}
