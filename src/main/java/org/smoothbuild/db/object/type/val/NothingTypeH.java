package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.TypeKindH.NOTHING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class NothingTypeH extends TypeHV {
  public NothingTypeH(Hash hash) {
    super(TypeNames.NOTHING, hash, NOTHING);
  }

  @Override
  public boolean isNothing() {
    return true;
  }
}
