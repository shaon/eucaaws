package com.eucatester;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.jcraft.jsch.JSch;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ElasticComputeCloud {

  public static Logger LOG = Logger.getLogger( ElasticComputeCloud.class.getCanonicalName( ) );
  AmazonEC2 ec2Client;

  public ElasticComputeCloud( AmazonEC2 ec2Client ) {
    PropertyConfigurator.configure( "log4j.properties" );
    this.ec2Client = ec2Client;
  }

  public void describeImages( ) {

  }

  public void runInstances( String imageId, String keyName, InstanceType instanceType,
                            String securityGroups, int maxCount, int minCount ) {
    LOG.info( "Running instance with image: " + imageId );
    RunInstancesRequest runInstancesRequest = new RunInstancesRequest(  );
    runInstancesRequest
            .withImageId( imageId )
            .withKeyName( keyName )
            .withInstanceType( instanceType )
            .withMaxCount( maxCount )
            .withMinCount( minCount )
            .withSecurityGroups( securityGroups );
    ec2Client.runInstances( runInstancesRequest );

    JSch jsch=new JSch( );

  }

}
