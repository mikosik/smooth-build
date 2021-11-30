package org.smoothbuild.db.object.obj.base;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.type.base.TypeH;

/**
 * This class is immutable.
 */
public abstract class ValueH extends ObjH {
  public ValueH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
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
