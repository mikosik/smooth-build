package org.smoothbuild.bytecode.type.cnst;

import org.smoothbuild.bytecode.type.CatKindB;
import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class BaseTB extends TypeB {
  public BaseTB(Hash hash, String name, CatKindB kind) {
    super(hash, name, kind);
  }
}
