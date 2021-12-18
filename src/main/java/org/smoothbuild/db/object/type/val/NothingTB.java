package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindB.NOTHING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeB;
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
