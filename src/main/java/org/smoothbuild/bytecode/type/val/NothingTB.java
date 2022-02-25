package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.NOTHING;

import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class NothingTB extends BaseTB {
  public NothingTB(Hash hash) {
    super(hash, TypeNames.NOTHING, NOTHING);
  }

  @Override
  public boolean isNothing() {
    return true;
  }
}
