package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindH.ANY;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeH;

/**
 * This class is immutable.
 */
public class AnyTH extends TypeH {
  public AnyTH(Hash hash) {
    super("Any", hash, ANY);
  }
}
