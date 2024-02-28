package org.smoothbuild.vm.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.COMBINE;

import org.smoothbuild.common.Hash;
import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.oper.CombineB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

/**
 * This class is immutable.
 */
public class CombineCB extends OperCB {
  public CombineCB(Hash hash, TypeB evaluationT) {
    super(hash, COMBINE, evaluationT);
    checkArgument(evaluationT instanceof TupleTB);
  }

  @Override
  public TupleTB evaluationT() {
    return (TupleTB) super.evaluationT();
  }

  @Override
  public CombineB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof CombineCB);
    return new CombineB(merkleRoot, exprDb);
  }
}
