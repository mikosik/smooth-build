package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.INT;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.val.IntB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class IntTB extends TypeB {
  public IntTB(Hash hash) {
    super(TypeNames.INT, hash, INT);
  }

  @Override
  public IntB newObj(MerkleRoot merkleRoot, ObjDbImpl byteDb) {
    return (IntB) super.newObj(merkleRoot, byteDb);
  }
}
