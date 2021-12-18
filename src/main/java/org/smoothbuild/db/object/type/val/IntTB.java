package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindB.INT;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.IntB;
import org.smoothbuild.db.object.type.base.TypeB;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class IntTB extends TypeB {
  public IntTB(Hash hash) {
    super(TypeNames.INT, hash, INT);
  }

  @Override
  public IntB newObj(MerkleRoot merkleRoot, ByteDb byteDb) {
    return (IntB) super.newObj(merkleRoot, byteDb);
  }
}
