package org.smoothbuild.lang.value;

import org.smoothbuild.lang.type.Type;

import com.google.common.hash.HashCode;

public final class Nothing implements Value {

  private Nothing() {}

  @Override
  public HashCode hash() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Type type() {
    throw new UnsupportedOperationException();
  }
}
