package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.INT;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.val.IntB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class IntTB extends BaseTB {
  public IntTB(Hash hash) {
    super(hash, TypeNames.INT, INT);
  }

  @Override
  public IntB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    return (IntB) super.newObj(merkleRoot, objDb);
  }
}
