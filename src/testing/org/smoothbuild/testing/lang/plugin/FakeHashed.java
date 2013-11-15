package org.smoothbuild.testing.lang.plugin;

import org.smoothbuild.io.db.hash.Hash;
import org.smoothbuild.lang.plugin.Hashed;

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
