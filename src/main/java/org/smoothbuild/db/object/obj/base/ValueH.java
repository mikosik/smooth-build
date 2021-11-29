package org.smoothbuild.db.object.obj.base;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.type.base.TypeH;

/**
 * This class is immutable.
 */
public abstract class ValueH extends ObjectH {
  public ValueH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
  }

  @Override
  public TypeH spec() {
    return (TypeH) super.spec();
  }

  @Override
  public TypeH type() {
    return spec();
  }
}
