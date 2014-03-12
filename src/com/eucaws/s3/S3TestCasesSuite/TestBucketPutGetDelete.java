package com.eucaws.s3.S3TestCasesSuite;

import com.amazonaws.services.s3.model.Bucket;
import com.tester.EucaTester;
import org.apache.log4j.PropertyConfigurator;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;

import java.util.List;

public class TestBucketPutGetDelete {

  EucaTester tester = new EucaTester( "eucarc" );

  @Test
  public void bucketPutGetDeleteTest( ) {
    PropertyConfigurator.configure( "log4j.properties" );

    System.out.println( "\nStarting PUT bucket test..." );
    List<Bucket> bucketList = tester.s3.createBucketTest(  );
    tester.s3.listBuckets( );
    List<Bucket> buckets = tester.s3.listBucketsWithPrefix( "eucaaws-bucket-" );
    tester.s3.deleteBuckets( buckets );
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void bucketNamingConventionTest( ) {
    PropertyConfigurator.configure( "log4j.properties" );
    String [] bucketNames =
            { "bucket&123", "bucket*123", "bucket123/", "bucket..123", "bucket.", "/bucket123", ".bucket" };

    Throwable e = null;
    for (int i = 0; i < bucketNames.length; i++) {
      try {
        Bucket bucket = tester.s3.createBucket( bucketNames[i] );
      } catch ( Exception ex ) {
        System.out.println( "\nFailed to create bucket: '" + bucketNames[i] + "' Caught exception: " + ex.toString() );
        e = ex;
      }
      assertTrue( e instanceof IllegalArgumentException );
    }
  }

}
