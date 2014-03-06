package com.eucaws.ec2;

import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.tester.EucaTester;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.tester.EucaTester.LOG;

public class EC2TestTemplate {


  @Test
  public void describeImagesTest( ) {
    PropertyConfigurator.configure( "log4j.properties" );
    EucaTester tester = new EucaTester( "eucarc" );
    DescribeImagesResult imagesResult = tester.ec2Client.describeImages( );
    List<Image> images = imagesResult.getImages( );
//    assertTrue();
  }
}
