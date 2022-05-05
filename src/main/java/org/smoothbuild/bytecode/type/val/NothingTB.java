package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.NOTHING;

import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.type.api.NothingT;

/**
 * This class is immutable.
 */
public class NothingTB extends BaseTB implements NothingT {
  public NothingTB(Hash hash) {
    super(hash, TypeNamesB.NOTHING, NOTHING);
  }
}
