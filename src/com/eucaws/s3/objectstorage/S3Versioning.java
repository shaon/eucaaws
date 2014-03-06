package com.eucaws.s3.objectstorage;

import com.amazonaws.services.s3.model.ListBucketsRequest;
import com.tester.EucaTester;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;

public class S3Versioning {
  public static void main(String [] args) throws IOException {
    PropertyConfigurator.configure( "log4j.properties" );

    String bucketName = "onebucket";
    String destBucketName = "twobucket";
    String srcdirectoryName = "srcobjects";
    String destdirectoryName = "destobjects";

    String fileName = "id_rsa.pub";
    String filePathWithFileName = srcdirectoryName+"/"+fileName;

    EucaTester tester = new EucaTester( "eucarc-5-3" );

//    tester.s3.createBucket( "shaontest" );
//    tester.s3.enableBucketVersioningConfiguration( destBucketName );
//    tester.s3.putObjectIntoBucket( bucketName, srcdirectoryName+"/package-list" );
//    tester.s3.listVersion( bucketName );
//    tester.s3Client.copyObject( bucketName, "package-list", destBucketName, "package-list" );
//    tester.s3.listVersion( bucketName, fileName, "e14954540a444c7b984c636dd97b28da" );
//    tester.s3.listVersion( destBucketName );
    tester.s3Client.getBucketLocation( "shaontest" );
//    tester.s3.listBuckets();
  }
}

//2013-11-26 22:09:21,807 [main] DEBUG com.amazonaws.services.s3.internal.S3Signer -  Calculated string to sign:
//        "GET
//
//        application/x-www-form-urlencoded; charset=utf-8
//        Wed, 27 Nov 2013 06:09:21 GMT
//        /mybuckers/?location"
//        2013-11-26 22:09:21,885 [main] DEBUG com.amazonaws.request -  Sending Request: GET https://mybuckers.s3.amazonaws.com / Parameters: (location: null, ) Headers: (Authorization: AWS AKIAIPGVJKHNNGPSIYTQ:LIy1gB9haYtyuQ3j0LGSIxBzYJo=, Date: Wed, 27 Nov 2013 06:09:21 GMT, User-Agent: aws-sdk-java/1.6.4 Mac_OS_X/10.9 Java_HotSpot(TM)_64-Bit_Server_VM/24.0-b56, Content-Type: application/x-www-form-urlencoded; charset=utf-8, )
//        2013-11-26 22:09:21,921 [main] DEBUG org.apache.http.impl.conn.PoolingClientConnectionManager -  Connection request: [route: {s}->https://mybuckers.s3.amazonaws.com][total kept alive: 0; route allocated: 0 of 50; total allocated: 0 of 50]
//        2013-11-26 22:09:21,932 [main] DEBUG org.apache.http.impl.conn.PoolingClientConnectionManager -  Connection leased: [id: 0][route: {s}->https://mybuckers.s3.amazonaws.com][total kept alive: 0; route allocated: 1 of 50; total allocated: 1 of 50]
//        2013-11-26 22:09:22,150 [main] DEBUG org.apache.http.impl.conn.DefaultClientConnectionOperator -  Connecting to mybuckers.s3.amazonaws.com:443
//        2013-11-26 22:09:22,712 [main] DEBUG org.apache.http.client.protocol.RequestAddCookies -  CookieSpec selected: best-match
//        2013-11-26 22:09:22,730 [main] DEBUG org.apache.http.client.protocol.RequestAuthCache -  Auth cache not set in the context
//        2013-11-26 22:09:22,730 [main] DEBUG org.apache.http.client.protocol.RequestProxyAuthentication -  Proxy auth state: UNCHALLENGED
//        2013-11-26 22:09:22,730 [main] DEBUG org.apache.http.impl.client.DefaultHttpClient -  Attempt 1 to execute request
//        2013-11-26 22:09:22,731 [main] DEBUG org.apache.http.impl.conn.DefaultClientConnection -  Sending request: GET /?location HTTP/1.1
//        2013-11-26 22:09:22,731 [main] DEBUG org.apache.http.wire -  >> "GET /?location HTTP/1.1[\r][\n]"
//        2013-11-26 22:09:22,732 [main] DEBUG org.apache.http.wire -  >> "Host: mybuckers.s3.amazonaws.com[\r][\n]"
//        2013-11-26 22:09:22,732 [main] DEBUG org.apache.http.wire -  >> "Authorization: AWS AKIAIPGVJKHNNGPSIYTQ:LIy1gB9haYtyuQ3j0LGSIxBzYJo=[\r][\n]"
//        2013-11-26 22:09:22,732 [main] DEBUG org.apache.http.wire -  >> "Date: Wed, 27 Nov 2013 06:09:21 GMT[\r][\n]"
//        2013-11-26 22:09:22,732 [main] DEBUG org.apache.http.wire -  >> "User-Agent: aws-sdk-java/1.6.4 Mac_OS_X/10.9 Java_HotSpot(TM)_64-Bit_Server_VM/24.0-b56[\r][\n]"
//        2013-11-26 22:09:22,732 [main] DEBUG org.apache.http.wire -  >> "Content-Type: application/x-www-form-urlencoded; charset=utf-8[\r][\n]"
//        2013-11-26 22:09:22,732 [main] DEBUG org.apache.http.wire -  >> "Connection: Keep-Alive[\r][\n]"
//        2013-11-26 22:09:22,732 [main] DEBUG org.apache.http.wire -  >> "[\r][\n]"
//        2013-11-26 22:09:22,732 [main] DEBUG org.apache.http.headers -  >> GET /?location HTTP/1.1
//        2013-11-26 22:09:22,732 [main] DEBUG org.apache.http.headers -  >> Host: mybuckers.s3.amazonaws.com
//        2013-11-26 22:09:22,732 [main] DEBUG org.apache.http.headers -  >> Authorization: AWS AKIAIPGVJKHNNGPSIYTQ:LIy1gB9haYtyuQ3j0LGSIxBzYJo=
//        2013-11-26 22:09:22,733 [main] DEBUG org.apache.http.headers -  >> Date: Wed, 27 Nov 2013 06:09:21 GMT
//        2013-11-26 22:09:22,733 [main] DEBUG org.apache.http.headers -  >> User-Agent: aws-sdk-java/1.6.4 Mac_OS_X/10.9 Java_HotSpot(TM)_64-Bit_Server_VM/24.0-b56
//        2013-11-26 22:09:22,733 [main] DEBUG org.apache.http.headers -  >> Content-Type: application/x-www-form-urlencoded; charset=utf-8
//        2013-11-26 22:09:22,733 [main] DEBUG org.apache.http.headers -  >> Connection: Keep-Alive
//        2013-11-26 22:09:22,857 [main] DEBUG org.apache.http.wire -  << "HTTP/1.1 200 OK[\r][\n]"
//        2013-11-26 22:09:22,858 [main] DEBUG org.apache.http.wire -  << "x-amz-id-2: B7aJNxYbYP63uN31awWC1D2syvzhI4jYfcHSU9I4aiHuJ8r9TIlN0WR7nzG67IRz[\r][\n]"
//        2013-11-26 22:09:22,858 [main] DEBUG org.apache.http.wire -  << "x-amz-request-id: 7372F637AC73427A[\r][\n]"
//        2013-11-26 22:09:22,858 [main] DEBUG org.apache.http.wire -  << "Date: Wed, 27 Nov 2013 06:09:23 GMT[\r][\n]"
//        2013-11-26 22:09:22,858 [main] DEBUG org.apache.http.wire -  << "Content-Type: application/xml[\r][\n]"
//        2013-11-26 22:09:22,859 [main] DEBUG org.apache.http.wire -  << "Transfer-Encoding: chunked[\r][\n]"
//        2013-11-26 22:09:22,859 [main] DEBUG org.apache.http.wire -  << "Server: AmazonS3[\r][\n]"
//        2013-11-26 22:09:22,859 [main] DEBUG org.apache.http.wire -  << "[\r][\n]"
//        2013-11-26 22:09:22,859 [main] DEBUG org.apache.http.impl.conn.DefaultClientConnection -  Receiving response: HTTP/1.1 200 OK
//        2013-11-26 22:09:22,859 [main] DEBUG org.apache.http.headers -  << HTTP/1.1 200 OK
//        2013-11-26 22:09:22,859 [main] DEBUG org.apache.http.headers -  << x-amz-id-2: B7aJNxYbYP63uN31awWC1D2syvzhI4jYfcHSU9I4aiHuJ8r9TIlN0WR7nzG67IRz
//        2013-11-26 22:09:22,859 [main] DEBUG org.apache.http.headers -  << x-amz-request-id: 7372F637AC73427A
//        2013-11-26 22:09:22,859 [main] DEBUG org.apache.http.headers -  << Date: Wed, 27 Nov 2013 06:09:23 GMT
//        2013-11-26 22:09:22,859 [main] DEBUG org.apache.http.headers -  << Content-Type: application/xml
//        2013-11-26 22:09:22,859 [main] DEBUG org.apache.http.headers -  << Transfer-Encoding: chunked
//        2013-11-26 22:09:22,859 [main] DEBUG org.apache.http.headers -  << Server: AmazonS3
//        2013-11-26 22:09:22,863 [main] DEBUG org.apache.http.impl.client.DefaultHttpClient -  Connection can be kept alive indefinitely
//        2013-11-26 22:09:22,888 [main] DEBUG com.amazonaws.services.s3.model.transform.XmlResponsesSaxParser -  Parsing XML response document with handler: class com.amazonaws.services.s3.model.transform.XmlResponsesSaxParser$BucketLocationHandler
//        2013-11-26 22:09:22,889 [main] DEBUG org.apache.http.wire -  << "6c[\r][\n]"
//        2013-11-26 22:09:22,889 [main] DEBUG org.apache.http.wire -  << "<?xml version="1.0" encoding="UTF-8"?>[\n]"
//        2013-11-26 22:09:22,889 [main] DEBUG org.apache.http.wire -  << "<LocationConstraint xmlns="http://s3.amazonaws.com/doc/2006-03-01/"/>"
//        2013-11-26 22:09:22,892 [main] DEBUG org.apache.http.wire -  << "[\r][\n]"
//        2013-11-26 22:09:22,892 [main] DEBUG org.apache.http.wire -  << "0[\r][\n]"
//        2013-11-26 22:09:22,892 [main] DEBUG org.apache.http.wire -  << "[\r][\n]"
//        2013-11-26 22:09:22,892 [main] DEBUG org.apache.http.impl.conn.PoolingClientConnectionManager -  Connection [id: 0][route: {s}->https://mybuckers.s3.amazonaws.com] can be kept alive indefinitely
//        2013-11-26 22:09:22,892 [main] DEBUG org.apache.http.impl.conn.PoolingClientConnectionManager -  Connection released: [id: 0][route: {s}->https://mybuckers.s3.amazonaws.com][total kept alive: 1; route allocated: 1 of 50; total allocated: 1 of 50]
//        2013-11-26 22:09:22,892 [main] DEBUG com.amazonaws.request -  Received successful response: 200, AWS Request ID: 7372F637AC73427A