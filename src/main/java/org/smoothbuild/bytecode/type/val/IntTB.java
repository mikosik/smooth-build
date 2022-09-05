package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.CatKindB.INT;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class IntTB extends BaseTB {
  public IntTB(Hash hash) {
    super(hash, TNamesB.INT, INT);
  }

  @Override
  public IntB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    return (IntB) super.newObj(merkleRoot, bytecodeDb);
  }
}
