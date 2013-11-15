package org.smoothbuild.testing.lang.function.value;

import org.smoothbuild.io.cache.hash.Hash;
import org.smoothbuild.lang.function.value.Hashed;

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
