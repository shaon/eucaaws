package com.eucatester;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.google.common.collect.Multimap;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class SimpleStorageService {

  public static Logger LOG = Logger.getLogger( SimpleStorageService.class.getCanonicalName( ) );
  AmazonS3 s3Client;

  public SimpleStorageService( AmazonS3 s3Client ) {
    PropertyConfigurator.configure( "log4j.properties" );
    this.s3Client = s3Client;
  }


  /**
   *
   * @param bucketName
   * @return Bucket
   * @Note Create bucket requires the endpoint.xml,
   *       hence, we need to pass the following as JVM option,
   *       -Dcom.amazonaws.regions.RegionUtils.fileOverride=/Users/shaon/IdeaProjects/eucatester/endpoints.xml
   */
  public Bucket createBucket( String bucketName ) {
    LOG.info( "Creating bucket: '" + bucketName + "'" );
    Bucket bucket = s3Client.createBucket( bucketName );
    assertTrue( s3Client.doesBucketExist( bucketName ), "Bucket '" + bucketName + "' was not found." );
    return bucket;
  }

  /**
   *
   * @param resourceType type of the resource, e.g bucket, object
   * @return EucaTester generated unique resource name
   */
  public String getResourceName( String resourceType ) {
    return "eucatester-" + resourceType + "-test-" + System.currentTimeMillis( );
  }

  /**
   *
   * @return list of buckets
   */
  public List<Bucket> listBuckets( ) {
    LOG.info( "Listing buckets:" );
    List<Bucket> bucketList = s3Client.listBuckets( );
    if ( !bucketList.isEmpty( ) ) {
      for ( Bucket bucket : bucketList ) {
        LOG.info( "s3://" + bucket.getName( ) );
      }
    } else
      LOG.info( "User doesn't own any bucket. Nothing to list." );
    return bucketList;
  }

  /**
   *
   * @param bucketPrefix prefix of the bucket name.
   * @return returns the list of buckets that starts with bucketPrefix
   */
  public List<Bucket> listBucketsWithPrefix( String bucketPrefix ) {
    List<Bucket> bucketList = s3Client.listBuckets( );
    List<Bucket> resultedBucketList = new ArrayList<Bucket>( );
    LOG.info( "Fetching bucket list with prefix '" + bucketPrefix + "'" );
    if ( !bucketList.isEmpty( ) ) {
      for ( Bucket bucket : bucketList ) {
        if ( bucket.getName( ).contains( bucketPrefix ) ) {
          resultedBucketList.add( bucket );
          LOG.info( "s3://" + bucket.getName( ) );
        }
      }
    } else
      LOG.info( "User doesn't own any bucket. Nothing to list." );
    return resultedBucketList;
  }


  /**
   *
   * @param bucketName name of the bucket to delete
   * @param doRecursive if the bucket has objects or versioned object in it.
   *                    If 'true' delete everything then delete the bucket
   */
  public void deleteBucket( String bucketName, boolean doRecursive ) {
    try {
      if ( doRecursive ) {
        deleteAllObjectsFromBucket( bucketName );
      }
    } catch ( Exception e ) {
      if ( e.toString( ).contains( "NoSuchBucket" ) )
        LOG.info( "Bucket '" + bucketName + "' does not exist.");
      if ( e.toString( ).contains( "BucketNotEmpty" ) ) {
        LOG.info( "Bucket '" + bucketName + "' is not empty. To delete everything use 'doRecursive=true'" );
      }
    }
    deleteBucket( bucketName );
  }

  /**
   *
   * @param bucketName
   */
  public void deleteBucket( String bucketName ) {
    LOG.info( "Deleting bucket: " + bucketName );
    s3Client.deleteBucket( bucketName );

    assertFalse( s3Client.doesBucketExist( bucketName ), "CRITICAL: Deleted bucket found." );
  }


  /**
   *
   * @param bucketNames list of bucket names to delete
   * @param doRecursive if the bucket has objects or versioned object in it.
   *                    If 'true' delete everything then delete the bucket
   */
  public void deleteBucketsWithNames( List<String> bucketNames, boolean doRecursive) {
    for ( String bucket : bucketNames ) {
      deleteBucket( bucket, doRecursive );
    }
  }

  /**
   *
   * @param bucketNames list of bucket names to delete
   */
  public void deleteBuckets( List<Bucket> bucketNames ) {
    for ( Bucket bucket : bucketNames ) {
      deleteBucket( bucket.getName( ), true );
    }
  }


  /**
   *
   * @param bucketName name of the versioned bucket to delete all objects from
   * @Note For version enabled bucket
   */
  public void deleteAllObjectsFromVersionedBucket( String bucketName ) {
//    ObjectListing objectListing = s3Client.listObjects( bucketName );
//    List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
//    for ( S3ObjectSummary objectSummary : objectSummaries ) {
//      LOG.info( "Deleting object: '" + objectSummary.getKey( ) + "' from bucket '" + bucketName + "'");
//      s3Client.deleteObject( bucketName, objectSummary.getKey( ) );
//    }
    List<S3VersionSummary> versionSummaries = listVersionedObjects( bucketName ).getVersionSummaries();
    for ( S3VersionSummary versionSummary : versionSummaries ) {
      LOG.info( "Deleting versioned object: " + versionSummary.getKey( ) );
      s3Client.deleteVersion( bucketName, versionSummary.getKey( ), versionSummary.getVersionId( ) );
    }
  }

  /**
   *
   * @param bucketName name of the bucket to delete all objects from
   */
  public void deleteAllObjectsFromBucket( String bucketName ) {
    // TODO check if the versioned objects are required to delete or not, currently it deletes everything
    ObjectListing objectListing = s3Client.listObjects( bucketName );
    List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
    for ( S3ObjectSummary objectSummary : objectSummaries ) {
      LOG.info( "Deleting object: '" + objectSummary.getKey( ) + "' from bucket '" + bucketName + "'");
      s3Client.deleteObject( bucketName, objectSummary.getKey( ) );
    }
  }


  /**
   *
   * @param bucketName
   * @return list of versioned objects in the bucket. Versions with data and versions with delete markers are included in the results.
   * @see http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/model/ListVersionsRequest.html
   *
   */
  public VersionListing listVersionedObjects( String bucketName ) {
    LOG.info( "Listing versioned objects.." );
    ListVersionsRequest listVersionsRequest = new ListVersionsRequest(  );
    listVersionsRequest.setBucketName( bucketName );
    VersionListing versionListing = s3Client.listVersions( listVersionsRequest );
    for ( S3VersionSummary versionSummary : versionListing.getVersionSummaries( ) ) {
      // to remove delete markers for viewing pleasure
//      if ( !versionSummary.isDeleteMarker( ) ) {
      LOG.info( "s3://" + versionSummary.getBucketName( ) + "/" + versionSummary.getKey( ) +
              "\t" + versionSummary.getVersionId( ) +
              "\t" + versionSummary.getLastModified( ) );
//      }
    }
    return versionListing;
  }

  /**
   *
   * @param bucketName bucket name where the object resides
   * @param keyMarker http://docs.aws.amazon.com/AmazonS3/latest/API/RESTBucketGET.html
   * @param versionId version Id of the object to list
   * @return
   */
  public VersionListing listVersionedObjects( String bucketName, String keyMarker, String versionId ) {
    ListVersionsRequest listVersionsRequest = new ListVersionsRequest(  );
    listVersionsRequest.withKeyMarker( keyMarker ).withVersionIdMarker( versionId ).setBucketName( bucketName );
    VersionListing versionListing = s3Client.listVersions( listVersionsRequest );
    for ( S3VersionSummary versionSummary : versionListing.getVersionSummaries( ) ) {
      LOG.info( "s3://" + versionSummary.getBucketName( ) + "/" + versionSummary.getKey( ) +
              "\t" + versionSummary.getVersionId( ) +
              "\t" + versionSummary.getLastModified( ) );
    }
    return versionListing;
  }


  /**
   *
   * @param bucketName bucket name to delete the object from
   * @param objectKey object key name to delete
   * @param versionId version Id of the object to delete
   */
  public void deleteObjectVersionKey( String bucketName, String objectKey, String versionId ) {
    LOG.info( "Deleting from bucket: " + bucketName + " object: " + objectKey + "\t" + " versionId: " + versionId );
    s3Client.deleteVersion( bucketName, objectKey, versionId );
  }


  /**
   *
   * @param bucketName
   * @param objectPrefix
   * @return
   */
  public VersionListing getBucketVersioningConfiguration( String bucketName, String objectPrefix ) {
    ListVersionsRequest listVersionsRequest = new ListVersionsRequest(  );
    listVersionsRequest.setBucketName( bucketName );
    listVersionsRequest.setPrefix( objectPrefix );
    VersionListing versionListing = s3Client.listVersions( listVersionsRequest );
    for ( S3VersionSummary versionSummary : versionListing.getVersionSummaries( ) ) {
      LOG.info( "s3://" + versionSummary.getBucketName( ) + "/" + versionSummary.getKey( ) +
              "\t" + versionSummary.getVersionId( ) +
              "\t" + versionSummary.getLastModified() );
    }
    return versionListing;
  }


  /**
   *
   * @param directoryName
   * @param bucketName
   * @param ignoreFiles
   * @return a List of PutObjectResult
   */
  public void syncDirectoryWithS3( String directoryName, String bucketName, List<String> ignoreFiles ) {
    LOG.info( "Syncing directory.." );
    File srcDirectory = new File( directoryName );
    File [] listOfFiles = srcDirectory.listFiles( );
    Set<String> srcFiles = new HashSet<String>( Arrays.asList( srcDirectory.list() ) );

    ObjectListing objectListing = s3Client.listObjects( bucketName );
    List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries( );
    for ( S3ObjectSummary objectSummary : objectSummaries ) {
      if ( !srcFiles.contains( objectSummary.getKey() ) ) {
        LOG.info( "D\t" + objectSummary.getKey() );
        deleteObject( bucketName, objectSummary.getKey() );
      }
    }
    boolean isNewFile;
    for ( File file : listOfFiles ) {
      if ( ignoreFiles.contains( file.getName( ) ) ) continue;
      isNewFile = false;
      try {
        S3Object s3Object = s3Client.getObject( bucketName, file.getName( ) );
        if ( !s3Object.getObjectMetadata( ).getETag( ).equals( getCheckSum( file ) ) ) {
          LOG.info( "M\t" + file.getAbsolutePath( ) );
          deleteObject( bucketName, s3Object.getKey( ) );
          putObjectIntoBucket( bucketName, file.getAbsolutePath() );
        }
      } catch ( Exception e ) {
        isNewFile = true;
      }
      if (isNewFile) {
        LOG.info( "A\t" + file.getAbsolutePath( ) );
        putObjectIntoBucket( bucketName, file.getAbsolutePath() );
      }
    }
  }


  /**
   *
   * @param bucketName
   * @param filePath
   * @return putObjectResult
   */
  public PutObjectResult putObjectIntoBucket( String bucketName, String filePath ) {
    File file = new File( filePath );
    LOG.info( "Putting object: " + file.getAbsolutePath( ) + " into bucket: " + bucketName );
    PutObjectResult putObjectResult = s3Client.putObject( new PutObjectRequest( bucketName, file.getName(), file ) );
    S3Object s3Object = s3Client.getObject( bucketName, file.getName( ) );
    LOG.info( "CSum: " + getCheckSum( file ) );
    LOG.info( "Etag: " + s3Object.getObjectMetadata( ).getETag( ) );
    assertTrue( s3Object.getObjectMetadata().getETag().equals( getCheckSum( file ) ),
            "Object metadata Etag and local file checksum did not match." );

    return putObjectResult;
  }

  public File updateTextObjectFile( File file ) {
    Writer writer = null;
    String content = "This is an updated EucaTester object text file.";
    try {
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(content + "My name is '" + file.getName( ) + "'");
      bw.close();
    } catch ( IOException ex ) {
      ex.printStackTrace( );
    } finally {
      try {
        writer.close( );
      } catch ( Exception ex ) { }
    }
    LOG.info( "Update text file: " + file.getName( ) );
    return file;
  }

  public File getTextObjectFile( ) {
    Writer writer = null;
    String scrDirectoryName = "srcobjects/";
    String filename =  "eucatester-test" + "-" + System.currentTimeMillis() + ".txt";

    File file = new File( scrDirectoryName + filename );
    try {
      writer = new BufferedWriter(
              new OutputStreamWriter( new FileOutputStream( scrDirectoryName + filename ), "utf-8" ) );
      writer.write( "This is an EucaTester object test. My name is '" + filename + "'");
    } catch ( IOException ex ) {
      ex.printStackTrace( );
    } finally {
      try {
        writer.close( );
      } catch ( Exception ex ) { }
    }
    LOG.info( "Created text file: " + filename );
    return file;
  }

  public List<String> createTextObjects( ) {
    return createTextObjects( 5 );
  }

  public List<String> createTextObjects( int number ) {
    List<String> resources = new ArrayList<String>(  );
    Writer writer = null;
    for ( int i = 0; i < number; i++ ) {
      String scrDirectoryName = "srcobjects/";
      String filename =  "eucatester-test" + i + "-" + System.currentTimeMillis() + ".txt";
      try {
        writer = new BufferedWriter(
                new OutputStreamWriter( new FileOutputStream( scrDirectoryName + filename ), "utf-8" ) );
        writer.write( "This is an eucatester object test. My name is '" + filename + "'");
      } catch ( IOException ex ) {
        ex.printStackTrace( );
      } finally {
        try {
          writer.close( );
        } catch ( Exception ex ) { }
      }
      LOG.info( "Created text file: " + filename );
      resources.add( filename );
    }
    return resources;
  }


  /**
   *
   * @param bucketName
   * @param objectName
   */
  public void deleteObject( String bucketName, String objectName ) {
    LOG.info( "Deleting object: " + objectName + " from bucket: " + bucketName );
    s3Client.deleteObject( bucketName, objectName );
  }


  /**
   *
   * @param bucketName
   */
  public ObjectListing listBucketObjects( String bucketName ) {
    LOG.info( "Listing bucket: " + bucketName );
    ObjectListing objectListing = s3Client.listObjects( bucketName );

    List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries( );
    if ( !objectSummaries.isEmpty( ) ) {
      for ( S3ObjectSummary objectSummary : objectSummaries ) {
        LOG.info( "s3://" + bucketName + "/" + objectSummary.getKey( ) + "\tSize: " + objectSummary.getSize( ) );
      }
    } else
      LOG.info( "Empty bucket. Nothing to list." );

    return objectListing;
  }

  public void getObject( String bucketName, List<String> keyNames ) {
    String destDirectory = "destobjects/";
    for ( String keyName : keyNames ) {
      LOG.info( "Downloading key '" + keyName + "' " + "from " + "'" + bucketName + "' " + "to " + destDirectory.replace( '/', ' ' ) );
      File file = new File( destDirectory + keyName );
      ObjectMetadata s3ObjectMetadata = s3Client.getObject( new GetObjectRequest( bucketName, keyName ), file );
      String localFileCheckSum = getCheckSum( file );
      LOG.info( "Etag: " + s3ObjectMetadata.getETag( ) );
      LOG.info( "CSum: " + localFileCheckSum );
      assertTrue( localFileCheckSum.equals( s3ObjectMetadata.getETag( ) ), "Downloaded file's checksum doesn't match with Etag." );
    }
  }


  /**
   *
   * @param bucketName
   */
  public void enableBucketVersioningConfiguration( String bucketName ) {
    LOG.info( "Enable bucket versioning for bucket: " + bucketName );
    SetBucketVersioningConfigurationRequest versioningConfigurationRequest =
            new SetBucketVersioningConfigurationRequest(
                    bucketName, new BucketVersioningConfiguration( BucketVersioningConfiguration.ENABLED ) );
    s3Client.setBucketVersioningConfiguration( versioningConfigurationRequest );

    BucketVersioningConfiguration bucketVersioningConfiguration = getBucketVersioningConfiguration( bucketName );

    assertTrue( bucketVersioningConfiguration.getStatus( ).equals( BucketVersioningConfiguration.ENABLED ),
            "Expected bucket version configuration setting is 'Enabled', found: '" +
                    bucketVersioningConfiguration.getStatus( ) + "'");
  }


  /**
   *
   * @param bucketName
   */
  public void suspendBucketVersioningConfiguration( String bucketName ) {
    LOG.info( "Suspending bucket versioning configuration for bucket: " + bucketName );
    SetBucketVersioningConfigurationRequest versioningConfigurationRequest =
            new SetBucketVersioningConfigurationRequest(
                    bucketName, new BucketVersioningConfiguration( BucketVersioningConfiguration.SUSPENDED ) );
    s3Client.setBucketVersioningConfiguration( versioningConfigurationRequest );

    BucketVersioningConfiguration bucketVersioningConfiguration = getBucketVersioningConfiguration( bucketName );

    assertTrue( bucketVersioningConfiguration.getStatus( ).equals( BucketVersioningConfiguration.SUSPENDED ),
            "Expected bucket version configuration setting is 'Suspended', found: '" +
                    bucketVersioningConfiguration.getStatus( ) + "'");
  }

  public void disableBucketVersioningConfiguration( String bucketName ) {
    LOG.info( "Disabling bucket versioning configuration for bucket: " + bucketName );
    SetBucketVersioningConfigurationRequest versioningConfigurationRequest =
            new SetBucketVersioningConfigurationRequest(
                    bucketName, new BucketVersioningConfiguration( BucketVersioningConfiguration.OFF ) );
    s3Client.setBucketVersioningConfiguration( versioningConfigurationRequest );

    BucketVersioningConfiguration bucketVersioningConfiguration = getBucketVersioningConfiguration( bucketName );

    assertTrue( bucketVersioningConfiguration.getStatus( ).equals( BucketVersioningConfiguration.OFF ),
            "Expected bucket version configuration setting is 'Off', found: '" +
                    bucketVersioningConfiguration.getStatus( ) + "'");
  }


  /**
   *
   * @param bucketName
   */
  public BucketVersioningConfiguration getBucketVersioningConfiguration( String bucketName ) {
    BucketVersioningConfiguration versioningConfiguration = s3Client.getBucketVersioningConfiguration( bucketName );
    LOG.info( "Bucket versioning status for bucket '" + bucketName + "' is: " + versioningConfiguration.getStatus( ) );
    return versioningConfiguration;
  }


  /**
   *
   * @param bucketName
   */
  public void getBucketACL( String bucketName ) {
    AccessControlList acl = s3Client.getBucketAcl( bucketName );
    LOG.info( "Owner Id: " + acl.getOwner( ).getId( ) );
    LOG.info( "Owner Display Name: " + acl.getOwner( ).getDisplayName( ) );

    Iterator<Grant> grantIterator = acl.getGrants( ).iterator( );
    while ( grantIterator.hasNext( ) ) {
      Grant grant = grantIterator.next( );
      LOG.info( "Grantee Identifier: " + grant.getGrantee( ).getIdentifier( ) + ": " + grant.getPermission( ) );
    }
  }

  public void getObjectACL( String bucketName, String objectKey ) {
    s3Client.getObjectAcl( bucketName, objectKey );
  }

  public void setObjectACL( String bucketName, String objectKey, CannedAccessControlList cAcl ) {
    AccessControlList acl = s3Client.getObjectAcl( bucketName, objectKey );
    s3Client.setObjectAcl( bucketName, objectKey, cAcl );
  }

  /**
   *
   * @param bucketName
   * @param granteeEmail
   */
  public void setBucketACL( String bucketName, String granteeEmail, Permission permission ) {
    Grant grant = new Grant( new EmailAddressGrantee( granteeEmail ), permission );
    AccessControlList acl = s3Client.getBucketAcl( bucketName );
    acl.getGrants( ).add( grant );
    s3Client.setBucketAcl( bucketName, acl );
  }

  /**
   *
   * @param bucketName
   * @param cAcl
   */
  public void setCannedACL( String bucketName, CannedAccessControlList cAcl ) {
    s3Client.setBucketAcl( bucketName, cAcl );
  }

  /**
   *
   * @param file
   * @return
   */
  public String getCheckSum( File file ) {
    StringBuffer sb = new StringBuffer( );
    try {
      MessageDigest md = MessageDigest.getInstance( "MD5" );
      FileInputStream fis = new FileInputStream( file );
      byte[] dataBytes = new byte[1024];

      int nread = 0;
      while ( ( nread = fis.read( dataBytes ) ) != -1 ) {
        md.update( dataBytes, 0, nread );
      };
      byte[] mdbytes = md.digest( );

      for ( int i = 0; i < mdbytes.length; i++ ) {
        sb.append( Integer.toString( ( mdbytes[i] & 0xff ) + 0x100, 16).substring( 1 ) );
      }
    } catch ( Exception e ) {
      LOG.info( e.toString( ) );
    }
    return sb.toString( );
  }

  public File getRandomFileInMb( int size ) {
    File file = new File( "srcobjects/eucatester-random-" + size + "mb-file" );
    try {
      file.createNewFile( );
      System.out.println( file.getAbsolutePath( ) );
      RandomAccessFile randomAccessFile = new RandomAccessFile(file.getAbsolutePath(), "rw");
      randomAccessFile.setLength( 1024 * 1024 * size );
    } catch (Exception e) {
      System.err.println(e);
    }
    return file;
  }
    
  public void bucketCleanUp( ) {
    List<Bucket> buckets = listBucketsWithPrefix( "eucatester" );
    deleteBuckets( buckets );
  }


  /********************************************************
   * Pre Built Sample Tests For Quick Functional Testing  *
   ********************************************************/

  public List<Bucket> createBucketTest( ) {
    return createBucketTest( 5 );
  }

  public List<Bucket> createBucketTest( int number ) {
    List<Bucket> resources = new ArrayList<Bucket>( );
    for ( int i = 0; i < number; i++) {
      resources.add( createBucket( "eucatester-bucket" + i + "-" + System.currentTimeMillis( ) ) );
    }
    return resources;
  }

  public Multimap<String, String> putObjectsTest( ) {
    return putObjectsTest( 5 );
  }

  public Multimap<String, String> putObjectsTest( int number ) {
    Bucket bucket = createBucket( "eucatester-test-" + System.currentTimeMillis( ) );
    List<String> textObjects = createTextObjects( number );
    for ( String textObj : textObjects ) {
      String scrDirectoryName = "srcobjects/";
      putObjectIntoBucket( bucket.getName( ), scrDirectoryName + textObj );
    }
    ResultObject resultObject = new ResultObject( );
    return resultObject.createResultObject( bucket.getName(), textObjects );
  }

  public void bucketACLTest( ) {
    CannedAccessControlList[] cannedAccessControlLists = {
            CannedAccessControlList.BucketOwnerRead,
            CannedAccessControlList.AuthenticatedRead,
            CannedAccessControlList.BucketOwnerFullControl,
            CannedAccessControlList.LogDeliveryWrite,
            CannedAccessControlList.Private,
            CannedAccessControlList.PublicRead,
            CannedAccessControlList.PublicReadWrite
    };
    for ( int i = 0; i < cannedAccessControlLists.length; i++ ) {
      String bucketName = "eucatester-test" + i + "-" + System.currentTimeMillis( );
      System.out.println();
      LOG.info( "Setting '" + cannedAccessControlLists[i].toString() + "'" + " on bucket '" + bucketName + "'");
      createBucket( bucketName );
      setCannedACL( bucketName, cannedAccessControlLists[ i ] );
      getBucketACL( bucketName );
    }
  }

}
