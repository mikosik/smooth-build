package org.smoothbuild.db.bytecode.type.val;

import static org.smoothbuild.db.bytecode.type.base.CatKindB.BOOL;

import org.smoothbuild.db.bytecode.obj.ByteDb;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.val.BoolB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;
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
