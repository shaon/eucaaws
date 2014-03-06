package com.tester;

import com.amazonaws.services.ec2.AmazonEC2;

public class ElasticComputeCloud {
  AmazonEC2 ec2Client;

  public ElasticComputeCloud( AmazonEC2 ec2Client) {
    this.ec2Client = ec2Client;
  }

  public void describeImages() {

  }

}
