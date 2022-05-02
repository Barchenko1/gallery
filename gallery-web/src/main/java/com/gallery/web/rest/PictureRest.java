package com.gallery.web.rest;

import com.gallery.core.service.IPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

@RestController
public class PictureRest {
    private final IPictureService pictureService;

    @Autowired
    public PictureRest(IPictureService pictureService) {
        this.pictureService = pictureService;
    }

    @RequestMapping(value = "/pictures", method = RequestMethod.GET, produces = "application/json")
    public List<File> getPictures() {
        return pictureService.getAllPictures();
    }
}
