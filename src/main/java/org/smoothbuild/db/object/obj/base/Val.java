package org.smoothbuild.db.object.obj.base;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.spec.base.ValSpec;

/**
 * This class is immutable.
 */
public abstract class Val extends Obj {
  public Val(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  @Override
  public ValSpec spec() {
    return (ValSpec) super.spec();
  }
}
