package org.smoothbuild.io.cache.value.read;

import org.smoothbuild.lang.type.SNothing;

import com.google.common.hash.HashCode;

public class ReadNothing implements ReadValue<SNothing> {
  public ReadNothing() {}

  @Override
  public SNothing read(HashCode hash) {
    throw new UnsupportedOperationException("ReadNothing should not be called.\n"
        + "It is part of EmptyArray implementation that have no elements.");
  }
}
