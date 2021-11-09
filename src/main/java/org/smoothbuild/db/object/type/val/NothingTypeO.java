package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.ObjKind.NOTHING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class NothingTypeO extends TypeV {
  public NothingTypeO(Hash hash) {
    super(TypeNames.NOTHING, hash, NOTHING);
  }

  @Override
  public boolean isNothing() {
    return true;
  }

  @Override
  public Obj newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    throw new UnsupportedOperationException("Cannot create object for " + NOTHING + " type.");
  }
}
