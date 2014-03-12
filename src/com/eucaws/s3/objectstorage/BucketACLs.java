package com.eucaws.s3.objectstorage;


import com.tester.EucaTester;
import org.apache.log4j.PropertyConfigurator;

public class BucketACLs {

  public static void main( String[] args ) {

    PropertyConfigurator.configure( "log4j.properties" );

    EucaTester tester = new EucaTester( "eucarc" );

    String bucketName = "yetanotherbucketfromhulk1";
//    tester.s3.listBuckets();
    tester.s3Client.getBucketAcl( bucketName );
  }
}
