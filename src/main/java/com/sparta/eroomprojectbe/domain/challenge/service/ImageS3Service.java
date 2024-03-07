package com.sparta.eroomprojectbe.domain.challenge.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
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

    /**
     * S3 버켓에 이미지를 업로드하는 메서드
     *
     * @param multipartFile 저장하려는 파일(jpeg,png만 가능하며 10mb 이하만 가능)
     * @return 업로드된 파일의 URL
     * @throws IOException
     */
    public String saveFile(MultipartFile multipartFile) throws IOException {
        // 파일 크기 체크
        if (multipartFile.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("파일의 크기는 10mb이하여야 합니다.");
        }
        //파일 형식 체크 (PNG 또는 JPEG 허용)
        String contentType = multipartFile.getContentType();
        if (!contentType.equals("image/png") && !contentType.equals("image/jpeg")) {
            throw new IllegalArgumentException("파일형식은 png파일과 jpeg파일만 가능합니다.");
        }
        // 파일의 원본 이름을 얻어오고 이후 S3버킷에 저장할 때 사용.
        String originalFileName = multipartFile.getOriginalFilename();
        // 파일명을 랜덤으로 작성
        String randomFileName = RandomStringUtils.randomAlphanumeric(10) + "_" + originalFileName;
        // ObjectMetadata 객체를 생성해서 S3 객체에 메타데이터를 추가한다. 파일 크기와 형식을 설정하고 이는 S3콘솔에서 확인할 때, 사용됨
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());
        // amazonS3.putObject 메서드를 사용하여 S3 버킷에 파일을 업로드합니다.
        // bucketName은 업로드될 버킷의 이름이며, originalFileName은 파일이 S3에 저장될 때 사용될 키입니다.
        // multipartFile.getInputStream()은 업로드할 파일의 InputStream을 제공합니다.
        // metadata 객체는 위에서 설정한 메타데이터 정보를 사용합니다.
        amazonS3.putObject(bucketName, randomFileName, multipartFile.getInputStream(), metadata);
        // 업로드가 완료되면 amazonS3.getUrl을 사용하여 업로드된 객체의 URL을 얻고, 이를 문자열로 반환합니다.
        // 이 URL은 업로드된 파일에 대한 고유한 식별자로 사용될 수 있습니다.
        return URLDecoder.decode(amazonS3.getUrl(bucketName, randomFileName).toString(), "utf-8");
    }

    /**
     * 업로드된 파일을 수정하는 메서드
     *
     * @param existingFileName 원본파일이름
     * @param newFile          새로 저장하려는 파일
     * @return 업로드된 파일의 URL
     * @throws IOException
     */
    public String updateFile(String existingFileName, MultipartFile newFile) throws IOException {
        try {
            // 기존 파일 삭제
            amazonS3.deleteObject(bucketName, existingFileName.split("/")[3]);
            // 새로운 파일 업로드
            return saveFile(newFile);
        } catch (SdkClientException e) {
            throw new IOException("S3에서 파일을 삭제하는데 실패했습니다.", e);
        }


    }

    /**
     * 업로드된 파일을 삭제하는 메서드
     *
     * @param existingFileName 원본 파일 이름
     */
    public void deleteFile(String existingFileName) {
        amazonS3.deleteObject(bucketName, existingFileName.split("/")[3]);
    }
}