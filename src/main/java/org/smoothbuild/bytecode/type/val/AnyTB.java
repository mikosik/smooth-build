package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.ANY;

import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.type.api.AnyT;

/**
 * This class is immutable.
 */
public class AnyTB extends BaseTB implements AnyT {
  public AnyTB(Hash hash) {
    super(hash, "Any", ANY);
  }
}
