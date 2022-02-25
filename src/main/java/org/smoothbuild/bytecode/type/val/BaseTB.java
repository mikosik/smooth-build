package org.smoothbuild.bytecode.type.val;

import org.smoothbuild.bytecode.type.base.CatKindB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class BaseTB extends TypeB {
  public BaseTB(Hash hash, String name, CatKindB kind) {
    super(hash, name, kind);
  }
}
