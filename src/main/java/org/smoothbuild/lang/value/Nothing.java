package org.smoothbuild.lang.value;

import org.smoothbuild.lang.type.Type;

import com.google.common.hash.HashCode;

public final class Nothing extends Value {

  private Nothing() {
    super(null, null);
  }

  public HashCode hash() {
    throw new UnsupportedOperationException();
  }

  public Type type() {
    throw new UnsupportedOperationException();
  }
}
