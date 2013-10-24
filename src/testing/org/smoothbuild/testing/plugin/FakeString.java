package org.smoothbuild.testing.plugin;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.hash.Hash;
import org.smoothbuild.plugin.StringValue;

import com.google.common.hash.HashCode;

public class FakeString implements StringValue {
  private final String value;
  private final HashCode hash;

  public FakeString(String value) {
    this.value = checkNotNull(value);
    this.hash = Hash.string(value);
  }

  @Override
  public HashCode hash() {
    return hash;
  }

  @Override
  public String value() {
    return value;
  }
}
