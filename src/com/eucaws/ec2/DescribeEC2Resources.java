package com.eucaws.ec2;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class DescribeEC2Resources {
  static Logger logger = Logger.getLogger( DescribeEC2Resources.class );

  public static void main( String[] args ) {
    BasicConfigurator.configure( );

  }
}


