package com.gallery.web.rest;

import com.gallery.core.modal.PictureModal;
import com.gallery.web.service.IPictureService;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.gallery.web.util.Constant.CONTENT_DISPOSITION;
import static com.gallery.web.util.Constant.CONTENT_TYPE;

@RestController
public class S3Rest {

    private final IPictureService pictureService;

    @Autowired
    public S3Rest(IPictureService pictureService) {
        this.pictureService = pictureService;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<String> uploadPicture(@RequestParam(value = "file") MultipartFile file) {
        return new ResponseEntity<>(pictureService.uploadPicture(file), HttpStatus.OK);
    }

    @RequestMapping(value = "/download/{fileName}", method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> downloadPicture(@PathVariable String fileName) {
        byte[] data = pictureService.downloadPicture(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header(CONTENT_TYPE, ContentType.APPLICATION_OCTET_STREAM.getMimeType())
                .header(CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @RequestMapping(value = "/view/{objectKey}", method = RequestMethod.GET)
    public ResponseEntity<PictureModal> getPicture(@PathVariable String objectKey) {
        return new ResponseEntity<>(pictureService.getPicture(objectKey), HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{fileName}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deletePicture(@PathVariable String fileName) {
        return new ResponseEntity<>(pictureService.deletePicture(fileName), HttpStatus.OK);
    }
}
