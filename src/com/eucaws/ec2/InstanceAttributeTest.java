package com.eucaws.ec2;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Arrays;
import java.util.List;

public class InstanceAttributeTest {
  static Logger logger = Logger.getLogger( InstanceAttributeTest.class );

  public static void main( String[] args ) {
    PropertyConfigurator.configure( "log4j.properties" );

    AmazonEC2 ec2Client = new AmazonEC2Client( new BasicAWSCredentials(
            "XXXXXXXXXXXXXXXXXXXXXX",
            "XXXXXXXXXXXXXXXXXXXXXX"
    ) );

    ec2Client.setEndpoint( "http://example.org:8773/services/Eucalyptus" );

    String runningInstanceId = "i-317A40D4";

    String stoppedInstanceId = "i-DF9D4572";

    String volumeId = "vol-70113ECF";
    String deviceName = "/dev/sdb";

    String ramdiskId = "eri-C6CC3B6A";
    String kernelId = "eki-7DB73766";
    String instanceType = "c1.medium";


/**
 * DescribeInstanceAttribute:
 *  - Supported
 *  a) blockDeviceMapping
 *  b) ramdisk
 *  c) rootDeviceName
 *  d) userData
 *  e) instanceType
 *  f) kernel
 *
 *  - Not Supported at this moment
 *  g) ebsOptimized
 *  h) productCodes
 *  i) disableApiTermination
 *  j) groupSet
 *  k) sourceDestCheck
 *  l) instanceInitiatedShutdownBehavior
 *
 */

    List<String> instanceAttributes =
            Arrays.asList("instanceType", "kernel", "rootDeviceName", "ramdisk", "blockDeviceMapping", "userData" );
/**
 * Action=DescribeInstanceAttribute&
 * SignatureMethod=HmacSHA256&
 * InstanceId=i-7B113F3C&
 * Attribute=blockDeviceMapping&
 */

    DescribeInstanceAttributeRequest describeInstanceAttributeRequest;

//    for ( String instanceAttribute : instanceAttributes ) {
//      describeInstanceAttributeRequest = new DescribeInstanceAttributeRequest( );
//      ec2Client.describeInstanceAttribute( describeInstanceAttributeRequest
//              .withInstanceId( stoppedInstanceId )
//              .withAttribute( instanceAttributes.get( 0 ) ) );
//    }

/**
 * ModifyInstanceAttribute:
 * - Supported
 * a) instanceType
 * b) kernel
 * c) ramdisk
 * d) userData
 * e) blockDeviceMapping
 *
 * - Not Supported at this moment
 *  g) ebsOptimized
 *  h) disableApiTermination
 *  i) groupSet
 *  j) sourceDestCheck
 *  k) instanceInitiatedShutdownBehavior
 *
 */
    ModifyInstanceAttributeRequest modifyInstanceAttributeRequest;

//    modifyInstanceAttributeRequest = new ModifyInstanceAttributeRequest(  );
//    ec2Client.modifyInstanceAttribute( modifyInstanceAttributeRequest
//            .withInstanceId( stoppedInstanceId )
//            .withKernel( kernelId ) );

//    modifyInstanceAttributeRequest = new ModifyInstanceAttributeRequest(  );
//    ec2Client.modifyInstanceAttribute( modifyInstanceAttributeRequest
//            .withInstanceId( runningInstanceId )
//            .withRamdisk( ramdiskId ) );

//    modifyInstanceAttributeRequest = new ModifyInstanceAttributeRequest(  );
//    ec2Client.modifyInstanceAttribute( modifyInstanceAttributeRequest
//            .withInstanceId( stoppedInstanceId )
//            .withInstanceType( instanceType ) );

//    modifyInstanceAttributeRequest = new ModifyInstanceAttributeRequest(  );
//    ec2Client.modifyInstanceAttribute( modifyInstanceAttributeRequest
//            .withInstanceId( stoppedInstanceId )
//            .withUserData( "mkdir shao9n11" ) );


/**
 * ModifyInstanceAttribute:
 *  Request:
 *  Action=ModifyInstanceAttribute&
 *  SignatureMethod=HmacSHA256&
 *  BlockDeviceMapping.1.DeviceName=%2Fdev%2Fsdb&
 *  InstanceId=i-2C6540B1&
 *  BlockDeviceMapping.1.Ebs.VolumeId=vol-2F0338B7&
 *  Attribute=blockDeviceMapping&
 *  SignatureVersion=2&
 *  Version=2013-08-15&
 *  BlockDeviceMapping.1.Ebs.DeleteOnTermination=false&
 */
    EbsInstanceBlockDeviceSpecification ebsInstanceBlockDeviceSpecification = new EbsInstanceBlockDeviceSpecification();
    InstanceBlockDeviceMappingSpecification instanceBlockDeviceMappingSpecification = new InstanceBlockDeviceMappingSpecification();

    modifyInstanceAttributeRequest = new ModifyInstanceAttributeRequest(  );
    ec2Client.modifyInstanceAttribute( modifyInstanceAttributeRequest
            .withInstanceId( runningInstanceId )
            .withAttribute( "blockDeviceMapping" )
            .withBlockDeviceMappings( instanceBlockDeviceMappingSpecification
                    .withDeviceName( deviceName )
                    .withEbs( ebsInstanceBlockDeviceSpecification
                            .withVolumeId( "vol-79FA3B45" )
                            .withDeleteOnTermination( true ) ) ) ); // deleteOnTermination: true/false

/**
 * ResetInstanceAttribute:
 * - Supported
 * a) kernel
 * b) ramdisk
 *
 * - Not Supported at this moment
 *  c) sourceDestCheck
 */
    instanceAttributes =
            Arrays.asList( "kernel", "ramdisk" );
    ResetInstanceAttributeRequest resetInstanceAttributeRequest;
//    for ( String instanceAttribute : instanceAttributes ) {
//      resetInstanceAttributeRequest = new ResetInstanceAttributeRequest();
//      resetInstanceAttributeRequest.withInstanceId( stoppedInstanceId ).withAttribute( instanceAttributes.get(1) );
//      ec2Client.resetInstanceAttribute( resetInstanceAttributeRequest );
//    }
  }
}
