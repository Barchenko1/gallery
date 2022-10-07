package com.gallery.web.rest;

import com.gallery.core.modal.PictureModal;
import com.gallery.core.service.IS3BucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class S3Rest {

    private final IS3BucketService bucketService;

    @Autowired
    public S3Rest(IS3BucketService bucketService) {
        this.bucketService = bucketService;
    }

    @RequestMapping(value = "/addPicture", method = RequestMethod.POST)
    public void getPictures(@RequestBody PictureModal pictureModal) {
        bucketService.addPictureToFolder(pictureModal, null);
    }
}
