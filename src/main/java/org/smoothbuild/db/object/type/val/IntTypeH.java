package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.SpecKindH.INT;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class IntTypeH extends TypeH {
  public IntTypeH(Hash hash) {
    super(TypeNames.INT, hash, INT);
  }

  @Override
  public IntH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (IntH) super.newObj(merkleRoot, objectHDb);
  }
}
