package com.eucaws.iam;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.github.sjones4.youcan.youare.YouAre;
import com.github.sjones4.youcan.youare.YouAreClient;
import com.github.sjones4.youcan.youare.model.CreateAccountRequest;

/**
 * Created by shaon on 1/16/14.
 */
public class IdentityAccessManagement {

  public static void createAccount( String accountName, String endpoint, String accessKey, String secretKey ) {
    AWSCredentialsProvider awsCredentialsProvider =
            new StaticCredentialsProvider( new BasicAWSCredentials(accessKey, secretKey));
    final YouAre youAre = new YouAreClient(awsCredentialsProvider);
    youAre.setEndpoint(endpoint);
    int numAccountsBefore = youAre.listAccounts().getAccounts().size();
    CreateAccountRequest createAccountRequest = new CreateAccountRequest().withAccountName(accountName);
    youAre.createAccount(createAccountRequest);
//    assertThat((numAccountsBefore < youAre.listAccounts().getAccounts().size()),"Failed to create account " + accountName);
//    print("Created account: " + accountName);
  }

}
