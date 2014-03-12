package com.eucaws.s3.S3TestCasesSuite;

import com.google.common.collect.Multimap;
import com.tester.EucaTester;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestObjectPutGetDelete {

  EucaTester tester = new EucaTester( "eucarc" );

  @Test
  public void objectPutGetDeleteTest( ) {
    PropertyConfigurator.configure( "log4j.properties" );

    System.out.println( "\nStarting PUT object test..." );
    Multimap<String, String> bucketObjectList = tester.s3.putObjectsTest();

    System.out.println( "\nStarting GET object test..." );
    Set<String> keys = bucketObjectList.keySet();

    String bucketName = null;
    List<String> keyNames = null;

    for ( String key : keys) {
      bucketName = key;
      keyNames = new ArrayList<String>( bucketObjectList.get( key ) );
      tester.s3.getObject( bucketName, keyNames );
    }

    System.out.println( "\nStarting DELETE object test..." );
    tester.s3.deleteAllObjectsFromBucket( bucketName );
    tester.s3.deleteBucket( bucketName );
  }
}
