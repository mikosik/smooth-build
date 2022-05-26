package org.smoothbuild.bytecode.type.cnst;

import static org.smoothbuild.bytecode.type.CatKindB.ANY;

import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class AnyTB extends BaseTB {
  public AnyTB(Hash hash) {
    super(hash, "Any", ANY);
  }
}
