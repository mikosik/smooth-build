package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.TypeKindH.ANY;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeHV;

/**
 * This class is immutable.
 */
public class AnyTypeH extends TypeHV {
  public AnyTypeH(Hash hash) {
    super("Any", hash, ANY);
  }
}
