package org.smoothbuild.testing.lang.type;

import org.smoothbuild.io.cache.hash.Hash;
import org.smoothbuild.lang.base.Hashed;

import com.google.common.hash.HashCode;

public class FakeHashed implements Hashed {
  private final HashCode hash;

  public FakeHashed(String string) {
    this.hash = Hash.string(string);
  }

  public FakeHashed(byte[] bytes) {
    this.hash = HashCode.fromBytes(bytes);
  }

  @Override
  public HashCode hash() {
    return hash;
  }
}
