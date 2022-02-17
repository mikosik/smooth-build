package org.smoothbuild.lang.type.impl;

import com.google.common.collect.ImmutableSet;

/**
 * This class is immutable.
 */
public sealed abstract class BaseTS extends TypeS
    permits AnyTS, BlobTS, BoolTS, IntTS, NothingTS, StringTS {
  public BaseTS(String name) {
    super(name, ImmutableSet.of(), false);
  }
}

