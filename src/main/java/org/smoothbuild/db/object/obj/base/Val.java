package org.smoothbuild.db.object.obj.base;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.type.base.ValType;

/**
 * This class is immutable.
 */
public abstract class Val extends Obj {
  public Val(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  @Override
  public ValType type() {
    return (ValType) super.type();
  }
}
