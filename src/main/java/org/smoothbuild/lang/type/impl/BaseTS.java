package org.smoothbuild.lang.type.impl;

import static org.smoothbuild.lang.type.impl.VarSetS.varSetS;

import org.smoothbuild.lang.type.api.BaseT;

/**
 * This class is immutable.
 */
public sealed abstract class BaseTS extends TypeS implements BaseT
    permits AnyTS, BlobTS, BoolTS, IntTS, NothingTS, StringTS {
  public BaseTS(String name) {
    super(name, varSetS());
  }
}

