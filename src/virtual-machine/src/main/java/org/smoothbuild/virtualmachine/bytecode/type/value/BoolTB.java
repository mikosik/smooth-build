package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BoolB;

/**
 * This class is immutable.
 */
public class BoolTB extends TypeB {
  public BoolTB(Hash hash) {
    super(hash, TypeNamesB.BOOL, BoolB.class);
  }

  @Override
  public BoolB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof BoolTB);
    return new BoolB(merkleRoot, exprDb);
  }
}
