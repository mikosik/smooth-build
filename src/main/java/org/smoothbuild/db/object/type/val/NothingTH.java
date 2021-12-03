package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindH.NOTHING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class NothingTH extends TypeH {
  public NothingTH(Hash hash) {
    super(TypeNames.NOTHING, hash, NOTHING);
  }

  @Override
  public boolean isNothing() {
    return true;
  }
}
