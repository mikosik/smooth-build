package org.smoothbuild.lang.type.impl;

import org.smoothbuild.lang.type.api.BaseT;

import com.google.common.collect.ImmutableSet;

/**
 * This class is immutable.
 */
public sealed abstract class BaseTS extends TypeS implements BaseT
    permits AnyTS, BlobTS, BoolTS, IntTS, NothingTS, StringTS {
  public BaseTS(String name) {
    super(name, ImmutableSet.of(), false);
  }
}

