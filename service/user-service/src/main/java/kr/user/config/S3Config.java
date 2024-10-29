package kr.user.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    private final String endpoint = "https://kr.object.ncloudstorage.com";
    private final String regionName = "kr-standard";

    @Value("${naver.storage.accessKey}")
    private String accessKey;

    @Value("${naver.storage.secretKey}")
    private String secretKey;

    @Value("${naver.storage.bucket}")
    private String bucketName;

    @Bean
    public AmazonS3 amazonS3Client() {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .withPathStyleAccessEnabled(true)
                .build();
    }

    @Bean
    public String bucketName() {
        return bucketName;
    }
}
