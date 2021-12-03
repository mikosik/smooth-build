package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindH.INT;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class IntTH extends TypeH {
  public IntTH(Hash hash) {
    super(TypeNames.INT, hash, INT);
  }

  @Override
  public IntH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (IntH) super.newObj(merkleRoot, objDb);
  }
}
