package org.smoothbuild.lang.base.type.impl;

import static org.smoothbuild.util.collect.Sets.set;

/**
 * This class is immutable.
 */
public sealed abstract class BaseTS extends TypeS
    permits AnyTS, BlobTS, BoolTS, IntTS, NothingTS, StringTS {
  public BaseTS(String name) {
    super(name, set());
  }
}

