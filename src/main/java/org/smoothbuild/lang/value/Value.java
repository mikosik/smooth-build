package org.smoothbuild.lang.value;

import org.smoothbuild.lang.type.ConcreteType;

import com.google.common.hash.HashCode;

public interface Value {
  public HashCode hash();

  public HashCode dataHash();

  public ConcreteType type();
}
