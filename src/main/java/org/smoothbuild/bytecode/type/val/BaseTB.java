package org.smoothbuild.bytecode.type.val;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.CatKindB;

/**
 * This class is immutable.
 */
public class BaseTB extends TypeB {
  public BaseTB(Hash hash, String name, CatKindB kind) {
    super(hash, name, kind);
  }
}
