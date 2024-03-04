package org.smoothbuild.virtualmachine.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.COMBINE;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CombineB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TupleTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

/**
 * This class is immutable.
 */
public class CombineCB extends OperCB {
  public CombineCB(Hash hash, TypeB evaluationType) {
    super(hash, COMBINE, evaluationType);
    checkArgument(evaluationType instanceof TupleTB);
  }

  @Override
  public TupleTB evaluationType() {
    return (TupleTB) super.evaluationType();
  }

  @Override
  public CombineB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof CombineCB);
    return new CombineB(merkleRoot, exprDb);
  }
}
