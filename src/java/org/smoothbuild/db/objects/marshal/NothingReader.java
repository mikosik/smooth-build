package org.smoothbuild.db.objects.marshal;

import org.smoothbuild.lang.base.SNothing;

import com.google.common.hash.HashCode;

public class NothingReader implements ObjectReader<SNothing> {
  public NothingReader() {}

  @Override
  public SNothing read(HashCode hash) {
    throw new UnsupportedOperationException("ReadNothing should not be called.\n"
        + "It is part of EmptyArray implementation that have no elements.");
  }
}
