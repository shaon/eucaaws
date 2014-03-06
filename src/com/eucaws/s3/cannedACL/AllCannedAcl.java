package com.eucaws.s3.cannedACL;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class AllCannedAcl {

    static Logger logger = Logger.getLogger(AllCannedAcl.class);

    public static void main(String [] args) {

        // get log4j in action
        BasicConfigurator.configure();

        // shaon
        AmazonS3 walrus = new AmazonS3Client(new BasicAWSCredentials(
                "XXXXXXXXXXXXXXXXXXXXXX",
                "XXXXXXXXXXXXXXXXXXXXXX"
        ));

        walrus.setEndpoint("http://example.org:8773/services/Walrus");
        walrus.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(true));

        // Create Bucket
//        walrus.createBucket("owncloud", "eucalyptus");
//        walrus.createBucket( "blahblah" );
        // Create object file and put into the bucket
        File file = new File("anyfile");
        walrus.putObject(new PutObjectRequest("owncloud", "anyfile", file));

        // Delete object
//        walrus.deleteObject("euca", "cloud_hosts");

        // List object in the bucket
        ObjectListing objectListing = walrus.listObjects("owncloud");


//        walrus.setObjectAcl("euca", "anyfile", CannedAccessControlList.AuthenticatedRead);
//        logger.debug("Listing buckets:");
//        System.out.println();
//
//        for (Bucket bucket : walrus.listBuckets()) {
//            System.out.println(bucket.getName());
//        }

//        AccessControlList bucketAcl = walrus.getBucketAcl("euca");

        // Grants an user (heh)
//        Collection<Grant> grantCollection = new ArrayList<Grant>();
//        Grant grant0 = new Grant(new CanonicalGrantee("227020432263"), Permission.Write);
//        grantCollection.add(grant0);
//        bucketAcl.getGrants().addAll(grantCollection);
//        walrus.setBucketAcl("euca", bucketAcl);

//        walrus.getObjectAcl("euca", "anyfile");

//        walrus.getObjectMetadata("euca", "anyfile");
    }
}
