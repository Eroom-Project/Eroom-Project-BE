package com.sparta.eroomprojectbe.domain.challenge.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;

@Service
@RequiredArgsConstructor
public class ImageS3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    //String 타입으로 MultipartFile을 바꿔주는 작업 + putObject메소드를 통해 S3에 넣어주는 작업
    public String saveFile(MultipartFile multipartFile) throws IOException {
        // 파일의 원본 이름을 얻어오고 이후 S3버킷에 저장할 때 사용.
        String originalFileName = multipartFile.getOriginalFilename();
        // ObjectMetadata 객체를 생성해서 S3 객체에 메타데이터를 추가한다. 파일 크기와 형식을 설정하고 이는 S3콘솔에서 확인할 때, 사용됨
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());
        // amazonS3.putObject 메서드를 사용하여 S3 버킷에 파일을 업로드합니다.
        // bucketName은 업로드될 버킷의 이름이며, originalFileName은 파일이 S3에 저장될 때 사용될 키입니다.
        // multipartFile.getInputStream()은 업로드할 파일의 InputStream을 제공합니다.
        // metadata 객체는 위에서 설정한 메타데이터 정보를 사용합니다.
        amazonS3.putObject(bucketName, originalFileName, multipartFile.getInputStream(), metadata);
        // 업로드가 완료되면 amazonS3.getUrl을 사용하여 업로드된 객체의 URL을 얻고, 이를 문자열로 반환합니다.
        // 이 URL은 업로드된 파일에 대한 고유한 식별자로 사용될 수 있습니다.
        return URLDecoder.decode(amazonS3.getUrl(bucketName, originalFileName).toString(), "utf-8");
    }
    // 전체적으로 이 메서드는 주어진 MultipartFile을 AWS S3에 업로드하고, 업로드된 파일의 URL을 반환합니다.

    public String updateFile(String existingFileName, MultipartFile newFile) throws IOException {
        try {
            // 기존 파일 삭제
            amazonS3.deleteObject(bucketName, existingFileName.split("/")[3]);
            // 새로운 파일 업로드
            return saveFile(newFile);
        }catch (SdkClientException e){
            throw new IOException("S3에서 파일을 삭제하는데 실패했습니다.", e);
        }


    }

    public void deleteFile(String existingFileName) {
        amazonS3.deleteObject(bucketName,existingFileName.split("/")[3]);
    }
}