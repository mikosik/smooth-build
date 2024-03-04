package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.INT;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IntB;

/**
 * This class is immutable.
 */
public class IntTB extends TypeB {
  public IntTB(Hash hash) {
    super(hash, TypeNamesB.INT, INT);
  }

  @Override
  public IntB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof IntTB);
    return new IntB(merkleRoot, exprDb);
  }
}
