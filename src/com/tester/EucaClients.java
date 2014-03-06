package com.tester;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.io.File;

public class EucaClients {
  public AmazonEC2 ec2Client;
  public AmazonS3 s3Client;
  String AWS_ACCESS_KEY;
  String AWS_SECRET_KEY;
  String S3_ENDPOINT;
  String EC2_ENDPOINT;

  /**
   *
   * @param file
   */
  private void getCredentials( File file ) {
    try {
    this.AWS_ACCESS_KEY = parseEucarc( file.getAbsolutePath( ), "AWS_ACCESS_KEY" ).replace( "'", "" );
    this.AWS_SECRET_KEY = parseEucarc( file.getAbsolutePath( ), "AWS_SECRET_KEY" ).replace( "'", "" );
    this.S3_ENDPOINT = parseEucarc( file.getAbsolutePath( ), "S3_URL" );
    this.EC2_ENDPOINT = parseEucarc( file.getAbsolutePath( ), "EC2_URL" );
    updateEndPoints( );
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  protected EucaClients( ) {
    new EucaClients( "eucarc" );
  }

  protected EucaClients( String AWS_ACCESS_KEY, String AWS_SECRET_KEY ) {
    this.s3Client = new AmazonS3Client( new BasicAWSCredentials( AWS_ACCESS_KEY, AWS_SECRET_KEY ) );
    this.ec2Client = new AmazonEC2Client( new BasicAWSCredentials( AWS_ACCESS_KEY, AWS_SECRET_KEY ) );
  }

  protected EucaClients( String filePath ) {
      getCredentials( new File ( filePath ) );
      this.ec2Client = getEC2Client( this.AWS_ACCESS_KEY, this.AWS_SECRET_KEY, this.EC2_ENDPOINT );
      this.s3Client = getS3Client( this.AWS_ACCESS_KEY, this.AWS_SECRET_KEY, this.S3_ENDPOINT );
  }

  /**
   *
   * @param AWS_ACCESS_KEY
   * @param AWS_SECRET_KEY
   * @param ENDPOINT
   */
  protected EucaClients( String AWS_ACCESS_KEY, String AWS_SECRET_KEY, String ENDPOINT ) {
    this.ec2Client = getEC2Client( AWS_ACCESS_KEY, AWS_SECRET_KEY, constructEndpoint( ENDPOINT, "EC2" ) );
    this.s3Client = getS3Client( AWS_ACCESS_KEY, AWS_SECRET_KEY, constructEndpoint( ENDPOINT, "WALRUS" ) );
  }

  /**
   *
   * @param AWS_ACCESS_KEY
   * @param AWS_SECRET_KEY
   * @param ENDPOINT
   * @param S3BACKEND - Available option: WALRUS, RIAKCS
   */
  protected EucaClients( String AWS_ACCESS_KEY, String AWS_SECRET_KEY, String ENDPOINT, String S3BACKEND ) {
    this.ec2Client = getEC2Client( AWS_ACCESS_KEY, AWS_SECRET_KEY, constructEndpoint( ENDPOINT, "EC2" ) );
    this.s3Client = getS3Client( AWS_ACCESS_KEY, AWS_SECRET_KEY, constructEndpoint( ENDPOINT, S3BACKEND ) );
  }

  /**
   *
   * @param ENDPOINT
   * @param service
   * @return
   */
  private String constructEndpoint( String ENDPOINT, String service ) {
    service = service.toUpperCase();
    if ( service.equals( "EC2" ) )
      return "http://" + ENDPOINT + ":8773/services/Eucalyptus";
    else if ( service.equals( "WALRUS" ) )
      return "http://" + ENDPOINT + ":8773/services/Walrus";
    else if ( service.equals( "RIAKCS" ) )
      return "http://" + ENDPOINT + ":8773/services/objectstorage";
    else return null;
  }

  /**
   *
   * @param AWS_ACCESS_KEY
   * @param AWS_SECRET_KEY
   * @param S3_ENDPOINT
   * @return
   */
  private AmazonS3 getS3Client( String AWS_ACCESS_KEY, String AWS_SECRET_KEY, String S3_ENDPOINT ) {
    AWS_ACCESS_KEY = AWS_ACCESS_KEY.replace( "'", "" );
    AWS_SECRET_KEY = AWS_SECRET_KEY.replace( "'", "" );
    AmazonS3 s3Client = new AmazonS3Client(new BasicAWSCredentials( AWS_ACCESS_KEY, AWS_SECRET_KEY ) );
    s3Client.setEndpoint( S3_ENDPOINT + "/");
    s3Client.setS3ClientOptions(new S3ClientOptions().withPathStyleAccess(true));
    return s3Client;
  }

  /**
   *
   * @param AWS_ACCESS_KEY
   * @param AWS_SECRET_KEY
   * @param EC2_ENDPOINT
   * @return
   */
  private AmazonEC2 getEC2Client( String AWS_ACCESS_KEY, String AWS_SECRET_KEY, String EC2_ENDPOINT ) {
    AWS_ACCESS_KEY = AWS_ACCESS_KEY.replace( "'", "" );
    AWS_SECRET_KEY = AWS_SECRET_KEY.replace( "'", "" );
    AmazonEC2 ec2Client = new AmazonEC2Client(new BasicAWSCredentials( AWS_ACCESS_KEY, AWS_SECRET_KEY ) );
    ec2Client.setEndpoint( EC2_ENDPOINT );
    return ec2Client;
  }

  /**
   *
   * @param credpath
   * @param field
   * @return
   * @throws IOException
   */
  private static String parseEucarc(String credpath, String field)
          throws IOException {
    Charset charset = Charset.forName("UTF-8");
    String creds = credpath;
    String result = null;
    try {
      List<String> lines = Files.readAllLines( Paths.get( creds ), charset );
      CharSequence find = field;
      for (String line : lines) {
        if (line.contains(find)) {
          result = line.substring(line.lastIndexOf('=') + 1);
          break;
        }
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
    return result;
  }

  private void setXMLTextContent(NodeList endpointChildNodes, String ENDPOINT) {
    for ( int k = 0; k < endpointChildNodes.getLength(); k++ ) {
      if ( endpointChildNodes.item( k ).getNodeName( ).compareTo( "Hostname" ) == 0 ) {
        endpointChildNodes.item( k ).setTextContent( ENDPOINT );
      }
    }
  }

  private void updateEndPoints( ) {
    System.out.println( "updating endpoints.." );
    try {
      String endpointPath = "endpoints.xml";
      File xmlFile = new File( endpointPath );
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance( );
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder( );
      Document document = documentBuilder.parse( xmlFile );

      NodeList regions = document.getElementsByTagName( "Region" );

      for ( int i = 0; i < regions.getLength( ); i++ ) {
        if ( regions.item( i ).getTextContent( ).contains( "eucalyptus" ) ) {
          Element eucalyptusElements = ( Element ) regions.item( i );
          NodeList eucalyptusEndpoints = eucalyptusElements.getElementsByTagName( "Endpoint" );
          for ( int j = 0; j < eucalyptusEndpoints.getLength( ); j++ ) {
            NodeList endpointChildNodes = eucalyptusEndpoints.item( j ).getChildNodes( );
            if ( eucalyptusEndpoints.item( j ).getTextContent( ).contains( "ec2" ) ) {
              setXMLTextContent( endpointChildNodes, EC2_ENDPOINT );
            } else if ( eucalyptusEndpoints.item( j ).getTextContent( ).contains( "s3" ) ) {
              setXMLTextContent( endpointChildNodes, S3_ENDPOINT );
            }
          }
        }
      }

      TransformerFactory transformerFactory = TransformerFactory.newInstance( );
      Transformer transformer = transformerFactory.newTransformer( );
      DOMSource source = new DOMSource( document );
      StreamResult result = new StreamResult( xmlFile );
      transformer.transform( source, result );

    } catch ( Exception e ) {
      System.out.println( e );
    }
    System.out.println( "done updating endpoints.." );

  }

}
