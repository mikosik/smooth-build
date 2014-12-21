package org.smoothbuild.lang.base;

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
