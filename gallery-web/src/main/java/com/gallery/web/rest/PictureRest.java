package com.gallery.web.rest;

import com.gallery.core.modal.PictureModal;
import com.gallery.core.service.IPictureService;
import com.gallery.web.dto.CreatePictureFolderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
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

    @RequestMapping(value = "/pictures", method = RequestMethod.GET)
    public List<PictureModal> getPicturesByFolder(@RequestBody CreatePictureFolderDto createPictureFolderDto) {
        return pictureService.getPicturesByFolderName(createPictureFolderDto.getFolderName());
    }

    @RequestMapping(value = "/addPictures", method = RequestMethod.GET)
    public void addPictures(List<File> pictures) {
        pictureService.addPictures(pictures);
    }

    @RequestMapping(value = "/deletePictures", method = RequestMethod.GET)
    public void deletePicture(PictureModal picture) {
        pictureService.deletePicture(picture);
    }

}
