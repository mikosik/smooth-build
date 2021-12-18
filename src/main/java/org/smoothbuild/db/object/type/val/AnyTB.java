package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindB.ANY;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeB;

/**
 * This class is immutable.
 */
public class AnyTB extends TypeB {
  public AnyTB(Hash hash) {
    super("Any", hash, ANY);
  }
}
