package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.ObjKind.NOTHING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.type.base.ValType;
import org.smoothbuild.lang.base.type.api.NothingType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class NothingOType extends ValType implements NothingType {
  public NothingOType(Hash hash) {
    super(TypeNames.NOTHING, hash, NOTHING);
  }

  @Override
  public boolean isNothing() {
    return true;
  }

  @Override
  public Obj newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    throw new UnsupportedOperationException("Cannot create object for " + NOTHING + " type.");
  }
}
