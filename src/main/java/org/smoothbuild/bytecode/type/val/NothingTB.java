package org.smoothbuild.bytecode.type.val;

import org.smoothbuild.bytecode.type.base.CatKindB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class NothingTB extends TypeB {
  public NothingTB(Hash hash) {
    super(TypeNames.NOTHING, hash, CatKindB.NOTHING);
  }

  @Override
  public boolean isNothing() {
    return true;
  }
}
