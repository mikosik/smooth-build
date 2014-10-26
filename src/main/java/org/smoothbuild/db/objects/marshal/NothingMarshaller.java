package org.smoothbuild.db.objects.marshal;

import org.smoothbuild.lang.base.Nothing;

import com.google.common.hash.HashCode;

public class NothingMarshaller implements ObjectMarshaller<Nothing> {
  public NothingMarshaller() {}

  @Override
  public Nothing read(HashCode hash) {
    throw new UnsupportedOperationException("ReadNothing should not be called.\n"
        + "It is part of EmptyArray implementation that have no elements.");
  }
}
