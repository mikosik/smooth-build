package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.CatKindB.NOTHING;

import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class NothingTB extends BaseTB {
  public NothingTB(Hash hash) {
    super(hash, TNamesB.NOTHING, NOTHING);
  }
}
