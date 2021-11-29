package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.SpecKindH.ANY;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeH;

/**
 * This class is immutable.
 */
public class AnyTypeH extends TypeH {
  public AnyTypeH(Hash hash) {
    super("Any", hash, ANY);
  }
}
