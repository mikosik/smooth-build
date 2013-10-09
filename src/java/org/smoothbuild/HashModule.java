package org.smoothbuild;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class HashModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  public HashFunction provideHashFunction() {
    return Hashing.sha1();
  }
}
