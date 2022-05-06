package org.smoothbuild.lang.type.impl;

import static org.smoothbuild.lang.type.impl.VarSetS.varSetS;

/**
 * This class is immutable.
 */
public sealed abstract class BaseTS extends TypeS
    permits AnyTS, BlobTS, BoolTS, IntTS, NothingTS, StringTS {
  public BaseTS(String name) {
    super(name, varSetS());
  }
}

