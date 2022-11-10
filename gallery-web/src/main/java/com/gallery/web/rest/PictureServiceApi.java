package com.gallery.web.rest;

import com.gallery.core.modal.PictureModal;
import com.gallery.web.service.IPictureService;
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
import reactor.core.publisher.Mono;

import java.util.List;

import static com.gallery.web.util.Constants.CONTENT_DISPOSITION;
import static com.gallery.web.util.Constants.CONTENT_TYPE;

@RestController
public class PictureServiceApi {

    private final IPictureService pictureService;

    @Autowired
    public PictureServiceApi(IPictureService pictureService) {
        this.pictureService = pictureService;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<String> uploadPictures(@RequestParam(value = "folderPath") String folderPath,
                                                 @RequestParam(value = "files") List<MultipartFile> files) {
        return new ResponseEntity<>(pictureService.uploadPicturesAsync(folderPath, files), HttpStatus.OK);
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> downloadPicture(@RequestParam(value = "objectKey") String objectKey) {
        byte[] data = pictureService.downloadPicture(objectKey);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header(CONTENT_TYPE, ContentType.APPLICATION_OCTET_STREAM.getMimeType())
                .header(CONTENT_DISPOSITION, "attachment; filename=\""
                        + objectKey.substring(objectKey.lastIndexOf("/")) + "\"")
                .body(resource);
    }

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public ResponseEntity<PictureModal> getPicture(@RequestParam(value = "objectKey") String objectKey) {
        return new ResponseEntity<>(pictureService.getPicture(objectKey), HttpStatus.OK);
    }

    @RequestMapping(value = "/reactive/view", method = RequestMethod.GET)
    public ResponseEntity<Mono<PictureModal>> getReactivePicture(@RequestParam(value = "objectKey") String objectKey) {
        return new ResponseEntity<>(pictureService.getMonoPicture(objectKey), HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<String> deletePicture(@RequestParam(value = "objectKey") String objectKey) {
        return new ResponseEntity<>(pictureService.deletePicture(objectKey), HttpStatus.OK);
    }
}
