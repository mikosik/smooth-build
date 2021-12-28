package org.smoothbuild.db.bytecode.type.val;

import static org.smoothbuild.db.bytecode.type.base.CatKindB.ANY;

import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;

/**
 * This class is immutable.
 */
public class AnyTB extends TypeB {
  public AnyTB(Hash hash) {
    super("Any", hash, ANY);
  }
}
