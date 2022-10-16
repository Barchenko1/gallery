package com.gallery.layer.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.gallery.core.modal.PictureModal;
import com.gallery.layer.util.IInStreamToByteConverter;
import com.gallery.layer.util.IMultipartFileToFileConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
@PropertySource("classpath:application.properties")
public class S3BucketService implements IS3BucketService {

    @Value("aws.s3.bucket.name")
    private String bucketName;

    private AmazonS3 s3Client;

    private IMultipartFileToFileConverter<MultipartFile, File> multipartFileToFileConverter;
    private IInStreamToByteConverter<S3ObjectInputStream, byte[]> inStreamToByteConverter;

    @Override
    public String uploadPicture(MultipartFile multipartFile) {
        File file = multipartFileToFileConverter.convert(multipartFile);
        s3Client.putObject(new PutObjectRequest(bucketName, file.getName(), file));
        file.delete();
        return String.format("file %s success upload", file.getName());
    }

    @Override
    public byte[] downloadPicture(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
        return inStreamToByteConverter.convert(s3ObjectInputStream);
    }

    @Override
    public String deletePicture(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
        return String.format("file %s success deleted", fileName);
    }

    @Override
    public void addPictureToFolder(PictureModal pictureModal, String folderName) {

    }

    @Override
    public void addFolder(String name) {

    }

    @Override
    public List<PictureModal> getFolderPictureList(String folderName) {
        return null;
    }

    @Override
    public List<PictureModal> getAllPictures() {
        return null;
    }

    @Override
    public List<PictureModal> getPicturesChunk(int chunk) {
        return null;
    }

    @Override
    public Optional<PictureModal> getPictureByName(String name) {
        return Optional.empty();
    }

    @Override
    public void deletePictureByName(String name) {

    }

    @Override
    public void deleteFolderByName(String folderName) {

    }
}
