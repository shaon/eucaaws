package com.tester;

import org.apache.log4j.Logger;

import java.io.IOException;

public class EucaTester extends EucaClients {
  public static Logger LOG = Logger.getLogger( EucaTester.class.getCanonicalName( ) );
  public SimpleStorageService s3;
  public ElasticComputeCloud ec2;

  /**
   *
   * @throws IOException
   */
  public EucaTester( ) {
    super( );
    this.clientsCallback( );
  }

  /**
   *
   * @param filePath
   * @throws IOException
   */
  public EucaTester( String filePath ) {
    super( filePath );
    this.clientsCallback( );
  }

  public EucaTester(String AWS_ACCESS_KEY, String AWS_SECRET_KEY) {
    super( AWS_ACCESS_KEY, AWS_SECRET_KEY );
    this.clientsCallback( );
  }

  /**
   *
   * @param ACCESS_KEY
   * @param SECRET_KEY
   * @param ENDPOINT
   */
  public EucaTester(String ACCESS_KEY, String SECRET_KEY, String ENDPOINT ) {
    super( ACCESS_KEY, SECRET_KEY, ENDPOINT );
    this.clientsCallback( );
  }

  public EucaTester(String ACCESS_KEY, String SECRET_KEY, String ENDPOINT, String S3BACKEND ) {
    super( ACCESS_KEY, SECRET_KEY, ENDPOINT, S3BACKEND );
    this.clientsCallback( );
  }

  private void clientsCallback() {
    s3 = new SimpleStorageService(s3Client);
    ec2 = new ElasticComputeCloud( ec2Client );
  }

}
