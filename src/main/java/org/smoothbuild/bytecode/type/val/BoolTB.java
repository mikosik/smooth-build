package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.CatKindB.BOOL;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.val.BoolB;
import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class BoolTB extends BaseTB {
  public BoolTB(Hash hash) {
    super(hash, TNamesB.BOOL, BOOL);
  }

  @Override
  public BoolB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    return (BoolB) super.newObj(merkleRoot, bytecodeDb);
  }
}
