package koscom.mini3.domain.ncpstorage.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NcpS3Service {

    private final S3Client s3Client;

    @Value("${ncp.object-storage.endpoint}")
    private String endpoint;

    @Value("${ncp.object-storage.bucket-name}")
    private String bucketName;

    // ✅ 파일 업로드 후 URL 반환
    public String uploadFile(MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename(); // 고유 파일명 생성

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .acl(ObjectCannedACL.PUBLIC_READ) // 업로드된 파일을 퍼블릭으로 설정 (URL 접근 가능)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            return getFileUrl(fileName); // 업로드 후 URL 반환

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류 발생: " + fileName, e);
        }
    }

    // ✅ 업로드된 파일 URL 반환
    private String getFileUrl(String fileName) {
        return endpoint + "/" + bucketName + "/" + fileName;
    }
}