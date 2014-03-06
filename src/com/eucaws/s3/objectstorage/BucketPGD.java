package com.eucaws.s3.objectstorage;

import com.amazonaws.services.s3.model.Bucket;
import com.tester.EucaTester;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BucketPGD {

  public static void main(String [] args) {
    PropertyConfigurator.configure( "log4j.properties" );

    EucaTester tester = new EucaTester( "eucarc" );

    System.out.println( "\nStarting CREATE bucket test..." );
    List<Bucket> buckets = tester.s3.createBucketTest( 1 );

    System.out.println( "\nStarting LIST bucket test..." );
    tester.s3.listBuckets( );

    System.out.println( "\nStarting DELETE bucket test..." );
    tester.s3.deleteBuckets( buckets );

  }
}
