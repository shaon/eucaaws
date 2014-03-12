package com.tester;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.github.sjones4.youcan.youare.model.CreateAccountRequest;
import com.google.common.collect.Multimap;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.github.sjones4.youcan.youare.YouAre;
import com.github.sjones4.youcan.youare.YouAreClient;

public class SimpleStorageService {

  public static Logger LOG = Logger.getLogger( SimpleStorageService.class.getCanonicalName( ) );
  AmazonS3 s3Client;

  public SimpleStorageService( AmazonS3 s3Client ) {
    this.s3Client = s3Client;
  }


  /**
   *
   * @param bucketName
   * @return Bucket
   * @Note Create bucket requires the endpoint.xml,
   *       hence, we need to pass the following as JVM option,
   *       -Dcom.amazonaws.regions.RegionUtils.fileOverride=/Users/shaon/IdeaProjects/EucaAWS/endpoints.xml
   */
  public Bucket createBucket( String bucketName ) {
    LOG.info( "Creating bucket: " + bucketName );
    Bucket bucket = s3Client.createBucket( bucketName );
    assert s3Client.doesBucketExist( bucketName ): "Bucket '" + bucketName + "' was not found.";
    return bucket;
  }

  public List<Bucket> createBucketTest( ) {
    return createBucketTest( 5 );
  }

  public List<Bucket> createBucketTest( int number ) {
    List<Bucket> resources = new ArrayList<Bucket>( );
    for ( int i = 0; i < number; i++) {
      resources.add( createBucket( "eucaaws-bucket-test" + i + "-" + System.currentTimeMillis( ) ) );
    }
    return resources;
  }

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
   * @param bucketName
   * @param doRecursive
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
    try {
      LOG.info( "Deleting bucket: " + bucketName );
      s3Client.deleteBucket( bucketName );
    } catch ( Exception e ) {
      if ( e.toString( ).contains( "NoSuchBucket" ) )
        LOG.info( "Bucket '" + bucketName + "' does not exist.");
      if ( e.toString( ).contains( "BucketNotEmpty" ) ) {
        LOG.error( e.toString() );
        LOG.info( "Bucket '" + bucketName + "' is not empty. To delete everything use 'doRecursive=true'" );
      }
    }
  }


  /**
   *
   * @param bucketList
   * @param doRecursive
   */
  public void deleteBucketsWithNames( List<String> bucketList, boolean doRecursive) {
    for ( String bucket : bucketList ) {
      deleteBucket( bucket, doRecursive );
    }
  }

  public void deleteBuckets( List<Bucket> buckets ) {
    for ( Bucket bucket : buckets ) {
      deleteBucket( bucket.getName( ), true );
    }
  }


  /**
   *
   * @param bucketName
   * @param versioned
   *
   * @Note For version enabled bucket
   */
  public void deleteAllObjectsFromBucket( String bucketName, String versioned ) {
    ObjectListing objectListing = s3Client.listObjects( bucketName );
    List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
    for ( S3ObjectSummary objectSummary : objectSummaries ) {
      LOG.info( "Deleting object: '" + objectSummary.getKey( ) + "' from bucket '" + bucketName + "'");
      s3Client.deleteObject( bucketName, objectSummary.getKey( ) );
    }
    List<S3VersionSummary> versionSummaries = listVersion( bucketName ).getVersionSummaries();
    for ( S3VersionSummary versionSummary : versionSummaries ) {
      s3Client.deleteVersion( bucketName, versionSummary.getKey( ), versionSummary.getVersionId( ) );
    }
  }

  /**
   *
   * @param bucketName
   */
  public void deleteAllObjectsFromBucket( String bucketName ) {
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
   * @return
   */
  public VersionListing listVersion( String bucketName ) {
    ListVersionsRequest listVersionsRequest = new ListVersionsRequest(  );
    listVersionsRequest.setBucketName( bucketName );
    VersionListing versionListing = s3Client.listVersions( listVersionsRequest );
    for ( S3VersionSummary versionSummary : versionListing.getVersionSummaries( ) ) {
      LOG.info( "s3://" + versionSummary.getBucketName( ) + "/" + versionSummary.getKey( ) +
              "\t" + versionSummary.getVersionId( ) +
              "\t" + versionSummary.getLastModified( ) );
    }
    return versionListing;
  }

  public VersionListing listVersion( String bucketName, String keyMarker, String versionId ) {
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
   * @param bucketName
   * @param objectKey
   * @param versionId
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
        S3Object s3Object = s3Client.getObject( bucketName, file.getName() );
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
    return putObjectResult;
  }

  public Multimap<String, String> putObjectsTest( ) {
    return putObjectsTest( 5 );
  }

  public Multimap<String, String> putObjectsTest( int number ) {
    Bucket bucket = createBucket( "eucaaws-test-" + System.currentTimeMillis( ) );
    List<String> textObjects = createTextObjects( number );
    for ( String textObj : textObjects ) {
      LOG.info( textObj );
      String scrDirectoryName = "srcobjects/";
      putObjectIntoBucket( bucket.getName( ), scrDirectoryName + textObj );
    }
    ResultObject resultObject = new ResultObject( );
    return resultObject.createResultObject( bucket.getName(), textObjects );
  }

  public List<String> createTextObjects( ) {
    return createTextObjects( 5 );
  }

  public List<String> createTextObjects( int number ) {
    List<String> resources = new ArrayList<String>(  );
    Writer writer = null;
    for ( int i = 0; i < number; i++ ) {
      String scrDirectoryName = "srcobjects/";
      String filename =  "eucaaws-test" + i + "-" + System.currentTimeMillis() + ".txt";
      try {
        writer = new BufferedWriter(
                new OutputStreamWriter( new FileOutputStream( scrDirectoryName + filename ), "utf-8" ) );
        writer.write( "This is an EucaAWS object test. My name is '" + filename + "'");
      } catch ( IOException ex ) {
        ex.printStackTrace( );
      } finally {
        try {
          writer.close( );
        } catch ( Exception ex ) { }
      }
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
  public void listBucketObjects( String bucketName ) {
    LOG.info( "Listing bucket: " + bucketName );
    ObjectListing objectListing = s3Client.listObjects( bucketName );
    List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries( );
    if ( !objectSummaries.isEmpty( ) ) {
      for ( S3ObjectSummary objectSummary : objectSummaries ) {
        LOG.info( "s3://" + bucketName + "/" + objectSummary.getKey( ) + "\tSize: " + objectSummary.getSize( ) );
      }
    } else
      LOG.info( "Empty bucket. Nothing to list." );
  }

  public void getObject( String bucketName, List<String> keyNames ) {
    String destDirectory = "destobjects/";
    for ( String keyName : keyNames ) {
      LOG.info( "Downloading key '" + keyName + "' " + "from " + "'" + bucketName + "' " + "to " + destDirectory.replace( '/', ' ' ) );
      s3Client.getObject( new GetObjectRequest( bucketName, keyName ), new File( destDirectory + keyName ) );
    }
  }


  /**
   *
   * @param bucketName
   */
  public void enableBucketVersioningConfiguration( String bucketName ) {
    LOG.info( "Enabling bucket versioning configuration for bucket: " + bucketName );
    SetBucketVersioningConfigurationRequest versioningConfigurationRequest =
            new SetBucketVersioningConfigurationRequest(
                    bucketName, new BucketVersioningConfiguration( BucketVersioningConfiguration.ENABLED ) );
    s3Client.setBucketVersioningConfiguration( versioningConfigurationRequest );
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
  }


  /**
   *
   * @param bucketName
   */
  public void getBucketVersioningConfiguration( String bucketName ) {
    BucketVersioningConfiguration versioningConfiguration = s3Client.getBucketVersioningConfiguration( bucketName );
    LOG.info( "Bucket versioning status for bucket '" + bucketName + "' is: " + versioningConfiguration.getStatus( ) );
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
      String bucketName = "eucaaws-test" + i + "-" + System.currentTimeMillis( );
      System.out.println();
      LOG.info( "Setting '" + cannedAccessControlLists[i].toString() + "'" + " on bucket '" + bucketName + "'");
      createBucket( bucketName );
      setCannedACL( bucketName, cannedAccessControlLists[ i ] );
      getBucketACL( bucketName );
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
    StringBuffer sb = new StringBuffer();
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      FileInputStream fis = new FileInputStream( file );
      byte[] dataBytes = new byte[1024];

      int nread = 0;
      while ((nread = fis.read(dataBytes)) != -1) {
        md.update(dataBytes, 0, nread);
      };
      byte[] mdbytes = md.digest();

      for (int i = 0; i < mdbytes.length; i++) {
        sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
      }
    } catch ( Exception e ) {
    }
    return sb.toString();
  }
    
  public void cleanUp() {
    List<Bucket> buckets = listBucketsWithPrefix( "eucaaws" );
    deleteBuckets( buckets );
  }

}
