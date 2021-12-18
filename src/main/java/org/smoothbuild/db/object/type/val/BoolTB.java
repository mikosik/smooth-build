package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindB.BOOL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.BoolB;
import org.smoothbuild.db.object.type.base.TypeB;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class BoolTB extends TypeB {
  public BoolTB(Hash hash) {
    super(TypeNames.BOOL, hash, BOOL);
  }

  @Override
  public BoolB newObj(MerkleRoot merkleRoot, ByteDb byteDb) {
    return (BoolB) super.newObj(merkleRoot, byteDb);
  }
}
