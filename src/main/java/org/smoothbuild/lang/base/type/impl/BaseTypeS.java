package org.smoothbuild.lang.base.type.impl;

import static org.smoothbuild.util.collect.Sets.set;

/**
 * This class is immutable.
 */
public sealed abstract class BaseTypeS extends TypeS
    permits AnyTypeS, BlobTypeS, BoolTypeS, IntTypeS, NothingTypeS, StringTypeS {
  public BaseTypeS(String name) {
    super(name, set());
  }
}

