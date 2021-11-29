package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.SpecKindH.BOOL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class BoolTypeH extends TypeH {
  public BoolTypeH(Hash hash) {
    super(TypeNames.BOOL, hash, BOOL);
  }

  @Override
  public BoolH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (BoolH) super.newObj(merkleRoot, objectHDb);
  }
}
