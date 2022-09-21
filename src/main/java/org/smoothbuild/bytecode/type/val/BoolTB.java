package org.smoothbuild.bytecode.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CategoryKinds.BOOL;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.val.BoolB;
import org.smoothbuild.bytecode.hashed.Hash;

/**
 * This class is immutable.
 */
public class BoolTB extends BaseTB {
  public BoolTB(Hash hash) {
    super(hash, TNamesB.BOOL, BOOL);
  }

  @Override
  public BoolB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof BoolTB);
    return new BoolB(merkleRoot, bytecodeDb);
  }
}
