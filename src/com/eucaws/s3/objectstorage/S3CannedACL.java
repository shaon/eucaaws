package com.eucaws.s3.objectstorage;

import com.tester.EucaTester;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;

public class S3CannedACL {
  public static void main(String [] args) throws IOException {
    PropertyConfigurator.configure( "log4j.properties" );

    EucaTester tester = new EucaTester( "eucarc" );

    tester.s3.bucketACLTest( );
//    tester.s3.cleanUp( );
  }
}
