package org.smoothbuild.db.bytecode.type.val;

import static org.smoothbuild.db.bytecode.type.base.CatKindB.NOTHING;

import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class NothingTB extends TypeB {
  public NothingTB(Hash hash) {
    super(TypeNames.NOTHING, hash, NOTHING);
  }

  @Override
  public boolean isNothing() {
    return true;
  }
}
