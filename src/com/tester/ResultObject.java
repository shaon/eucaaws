package com.tester;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.List;

/**
 * Created by shaon on 1/14/14.
 */
public class ResultObject {

  public Multimap<String, String> createResultObject(String bucket, List<String> keys) {
    Multimap<String, String> multimap = ArrayListMultimap.create();

    for ( String k : keys ) {
      multimap.put( bucket, k );
    }

    return multimap;
  }

}
