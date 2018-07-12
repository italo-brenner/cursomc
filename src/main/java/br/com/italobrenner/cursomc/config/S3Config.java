package br.com.italobrenner.cursomc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3Config {

	@Value("${file.strategy}")
	private String fileStrategy;
	
	@Value("${aws.access_key_id}")
	private String awsId;
	
	@Value("${aws.secret_access_id}")
	private String awsKey;
	
	@Value("${s3.region}")
	private String region;
	
	@Bean
	public AmazonS3 s3client() {
		if ("s3".equals(fileStrategy)) {
			BasicAWSCredentials awsCred = new BasicAWSCredentials(awsId, awsKey);
			AmazonS3 s3client = AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(region))
								.withCredentials(new AWSStaticCredentialsProvider(awsCred)).build();
			return s3client;
		} else {
			return null;
		}
	}
	
}
