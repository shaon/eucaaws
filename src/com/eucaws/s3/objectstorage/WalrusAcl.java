package com.eucaws.s3.objectstorage;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.Bucket;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

public class WalrusAcl {

    static Logger logger = Logger.getLogger(WalrusAcl.class);

    public static void main(String [] args) {

        // get log4j in action
        BasicConfigurator.configure();

        AmazonS3 walrus = new AmazonS3Client(new BasicAWSCredentials(
           "XXXXXXXXXXXXXXXXXXXXXX",
           "XXXXXXXXXXXXXXXXXXXXXX"
        ));
        walrus.setEndpoint("http://example.org:8773/services/Walrus/");
        walrus.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(true));

        walrus.createBucket("yetanotherbucketfromhulk1", "eucalyptus");

        logger.debug("Listing buckets:");
        System.out.println();

        for (Bucket bucket : walrus.listBuckets()) {
            System.out.println(bucket.getName());
        }

        walrus.getBucketAcl("yetanotherbucketfromhulk1");

    }
}
