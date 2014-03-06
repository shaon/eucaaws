package com.eucaws.s3.BucketVersion;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class BucketVersioning {
    static Logger logger = Logger.getLogger(BucketVersioning.class);

    public static void main(String [] args) {
        BasicConfigurator.configure();

        AmazonS3 walrus = new AmazonS3Client(new BasicAWSCredentials(
                "XXXXXXXXXXXXXXXXXXXXXX",
                "XXXXXXXXXXXXXXXXXXXXXX"
        ));
        walrus.setEndpoint("http://example.org:8773/services/Walrus/");
        walrus.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(true));

        SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest =
                new SetBucketVersioningConfigurationRequest("hulk1admin1",
                        new BucketVersioningConfiguration(BucketVersioningConfiguration.OFF));

        walrus.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);

        BucketVersioningConfiguration bucketVersioningConfiguration = walrus.getBucketVersioningConfiguration("hulk1admin1");

        System.out.println();
        logger.debug("Bucket versioning is: " + bucketVersioningConfiguration.getStatus());

    }
}
