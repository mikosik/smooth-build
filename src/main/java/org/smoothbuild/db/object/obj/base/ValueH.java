package org.smoothbuild.db.object.obj.base;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.type.base.TypeHV;

/**
 * This class is immutable.
 */
public abstract class ValueH extends ObjectH {
  public ValueH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
  }

  @Override
  public TypeHV type() {
    return (TypeHV) super.type();
  }
}
