package com.eucaws.s3.S3TestCasesSuite;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.tester.EucaTester;
import org.apache.commons.logging.Log;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class TestBucketVersioning {
  EucaTester tester = new EucaTester( "eucarc" );

  @Test
  public void bucketVersioningEnabledTest( ) {
    PropertyConfigurator.configure( "log4j.properties" );

    Bucket bucket = tester.s3.createBucket( tester.s3.getResourceName( "bucket" ) );

    tester.s3.enableBucketVersioningConfiguration( bucket.getName( ) );

    File file = tester.s3.getTextObjectFile( );
    PutObjectResult putObjectResult1 = tester.s3.putObjectIntoBucket( bucket.getName( ), file.getAbsolutePath( ) );

    tester.s3.updateTextObjectFile( file );
    PutObjectResult putObjectResult2 = tester.s3.putObjectIntoBucket( bucket.getName( ), file.getAbsolutePath( ) );

    ObjectListing objectListing = tester.s3.listBucketObjects( bucket.getName( ) );
    List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries( );
    for ( S3ObjectSummary objectSummary : objectSummaries ) {
      assertTrue( objectSummary.getETag( ).equals( putObjectResult2.getETag( ) ) );
      // currently failing because of the following bug
      // https://eucalyptus.atlassian.net/browse/EUCA-8894
    }

    // CleanUp
    tester.s3.deleteAllObjectsFromVersionedBucket( bucket.getName( ) );
    tester.s3.deleteBucket( bucket.getName( ) );
  }

  @Test
  public void bucketVersioningSuspendTest( ) {
    PropertyConfigurator.configure( "log4j.properties" );

    Bucket bucket = tester.s3.createBucket( tester.s3.getResourceName( "bucket" ) );
    tester.s3.enableBucketVersioningConfiguration( bucket.getName( ) );
    tester.s3.suspendBucketVersioningConfiguration( bucket.getName( ) );

    tester.s3.deleteBucket( bucket.getName( ) );

  }

  @Test
  public void bucketVersioningOffTest( ) {
    PropertyConfigurator.configure( "log4j.properties" );

    Bucket bucket = tester.s3.createBucket( tester.s3.getResourceName( "bucket" ) );
    BucketVersioningConfiguration versioningConfiguration =
            tester.s3.getBucketVersioningConfiguration( bucket.getName( ) );
    System.out.println( "Default bucket versioning status is: " + versioningConfiguration.getStatus( ) );

    assertTrue( versioningConfiguration.getStatus().equals( BucketVersioningConfiguration.OFF ),
            "Expected bucket version configuration setting is 'Enabled', found: '" +
                    versioningConfiguration.getStatus() + "'" );
  }
}
