package training.sortir.tools;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;


import java.io.*;

@Component
@PropertySource("classpath:application.properties")
public class AWSCloudUtil {

    private static String AWS_ACCESS_KEY;
    private static String AWS_SECRET_KEY;
    private static String AWS_BUCKET;
    private static String S3_URL;

    @Value("${aws.access.key}")
    private String accessKey;
    @Value("${aws.secret.key}")
    private String secretKey;
    @Value("${aws.s3.bucket}")
    private String bucket;
    @Value("${aws.s3.baseurl}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        AWS_ACCESS_KEY = accessKey;
        AWS_SECRET_KEY = secretKey;
        AWS_BUCKET = bucket;
        S3_URL = baseUrl;
    }

    private AwsCredentials awsCredentialsProvider() {

        return AwsBasicCredentials.create(AWS_ACCESS_KEY,AWS_SECRET_KEY);
    }

    private S3Client awsS3ClientBuilder() {

        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentialsProvider()))
                .region(Region.EU_WEST_3)
                .build();
    }


    public void uploadFileToS3(String name, byte[] fileBytes){
                System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");

        S3Client s3Client = awsS3ClientBuilder();

        String filename = "temp-files/"+name;
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(AWS_BUCKET)
                            .key(filename)
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(fileBytes)
            );
            System.out.println("File uploaded successfully: " + filename);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }
    public void confirmFile(String filename, String path) {
        S3Client s3Client = awsS3ClientBuilder();
        String oldPath = "temp-files/" + filename;
        String newPath = path + filename;

        try {
            s3Client.copyObject(CopyObjectRequest.builder()
                    .sourceBucket(AWS_BUCKET)
                    .sourceKey(oldPath)
                    .destinationBucket(AWS_BUCKET)
                    .destinationKey(newPath)
                    .build()
            );
            System.out.println("File moved successfully: " + newPath);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }
    public void confirmSmallFile(String filename, String path) {
        S3Client s3Client = awsS3ClientBuilder();
        String oldPath = "temp-files/small/" + filename;
        String newPath = path + filename;

        try {
            s3Client.copyObject(CopyObjectRequest.builder()
                    .sourceBucket(AWS_BUCKET)
                    .sourceKey(oldPath)
                    .destinationBucket(AWS_BUCKET)
                    .destinationKey(newPath)
                    .build()
            );
            System.out.println("File moved successfully: " + newPath);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    public InputStream downloadFileFromS3(String filename) {
        S3Client s3Client = awsS3ClientBuilder();
        try {
            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(AWS_BUCKET)
                    .key(filename)
                    .build());

            return s3Object;
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

    public void deleteFileFromS3(String filename){
        S3Client s3Client = awsS3ClientBuilder();
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(AWS_BUCKET)
                    .key(filename)
                    .build()
            );
            System.out.println("File deleted successfully: " + filename);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }

}
