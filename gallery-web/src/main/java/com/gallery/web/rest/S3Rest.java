package com.gallery.web.rest;

import com.gallery.layer.service.IS3BucketService;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.gallery.web.util.Constant.CONTENT_DISPOSITION;
import static com.gallery.web.util.Constant.CONTENT_TYPE;

@RestController
public class S3Rest {

    private final IS3BucketService bucketService;

    @Autowired
    public S3Rest(IS3BucketService bucketService) {
        this.bucketService = bucketService;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<String> uploadPicture(@RequestParam(value = "file") MultipartFile file) {
        return new ResponseEntity<>(bucketService.uploadPicture(file), HttpStatus.OK);
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> downloadPicture(@RequestParam String fileName) {
        byte[] data = bucketService.downloadPicture(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header(CONTENT_TYPE, ContentType.APPLICATION_OCTET_STREAM.getMimeType())
                .header(CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<String> deletePicture(@RequestParam String fileName) {
        return new ResponseEntity<>(bucketService.deletePicture(fileName), HttpStatus.OK);
    }
}
