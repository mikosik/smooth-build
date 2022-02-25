package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.ANY;

import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class AnyTB extends BaseTB {
  public AnyTB(Hash hash) {
    super(hash, "Any", ANY);
  }
}
