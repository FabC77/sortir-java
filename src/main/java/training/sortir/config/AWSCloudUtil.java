package training.sortir.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3EncryptionClientV2Builder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;

public class AWSCloudUtil {

    @Value("${aws.access.key}")
    private String AWS_ACCESS_KEY;
    @Value("${aws.secret.key}")
    private String AWS_SECRET_KEY;
    @Value("${aws.s3.bucket}")
    private String AWS_BUCKET;
    @Value("${aws.s3.baseurl}")
    private String S3_URL;

    private AWSCredentials awsCredentials() {
        AWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY,AWS_SECRET_KEY);
        return credentials;
    }

    private AmazonS3 awsS3ClientBuilder() {
        AmazonS3 s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials()))
                .withRegion(Regions.EU_WEST_3)
                .build();
        return s3Client;
    }
    public void uploadFileToS3(String filename, byte[] fileBytes){
        AmazonS3 s3client = awsS3ClientBuilder();
        File file = new File(filename);

        try(OutputStream os = new FileOutputStream(file)){
            os.write(fileBytes);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        s3client.putObject(AWS_BUCKET,filename,file);
    }
    public S3ObjectInputStream downloadFileFromS3(String filename ){
        AmazonS3 s3client = awsS3ClientBuilder();
        S3Object s3object= s3client.getObject(AWS_BUCKET,filename);
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        return inputStream;
    }
    public void deleteFileFromS3(String filename){
        AmazonS3 s3client = awsS3ClientBuilder();
        try {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(AWS_BUCKET, filename);
            s3client.deleteObject(deleteObjectRequest);
            System.out.println("File deleted successfully: " + filename);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error deleting file: " + filename);
        }
    }
}
