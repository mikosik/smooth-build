package org.smoothbuild.db.object.obj.base;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.type.base.ValType;

/**
 * This class is immutable.
 */
public abstract class Val extends Obj {
  public Val(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  @Override
  public ValType type() {
    return (ValType) super.type();
  }
}
