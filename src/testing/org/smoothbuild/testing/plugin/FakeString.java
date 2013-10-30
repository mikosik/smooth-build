package org.smoothbuild.testing.plugin;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.db.Hash;
import org.smoothbuild.plugin.StringValue;

import com.google.common.hash.HashCode;

public class FakeString implements StringValue {
  private final String value;

  public FakeString(String value) {
    this.value = checkNotNull(value);
  }

  @Override
  public HashCode hash() {
    return Hash.string(value);
  }

  @Override
  public String value() {
    return value;
  }
}
