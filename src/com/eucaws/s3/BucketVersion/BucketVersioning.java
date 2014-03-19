package com.eucaws.s3.BucketVersion;

import com.tester.EucaTester;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import static org.testng.Assert.assertTrue;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.util.List;

public class BucketVersioning {

  public static void main( String[] args ) {

    PropertyConfigurator.configure( "log4j.properties" );

    EucaTester tester = new EucaTester( "eucarc" );

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
    }

//    CleanUp
    tester.s3.deleteAllObjectsFromVersionedBucket( bucket.getName( ) );
    tester.s3.deleteBucket( bucket.getName( ) );

  }
}
