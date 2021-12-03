package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindH.BOOL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class BoolTH extends TypeH {
  public BoolTH(Hash hash) {
    super(TypeNames.BOOL, hash, BOOL);
  }

  @Override
  public BoolH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (BoolH) super.newObj(merkleRoot, objDb);
  }
}
