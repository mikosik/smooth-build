package org.smoothbuild.db.bytecode.type.val;

import static org.smoothbuild.db.bytecode.type.base.CatKindB.INT;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.val.IntB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class IntTB extends TypeB {
  public IntTB(Hash hash) {
    super(TypeNames.INT, hash, INT);
  }

  @Override
  public IntB newObj(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    return (IntB) super.newObj(merkleRoot, byteDb);
  }
}
