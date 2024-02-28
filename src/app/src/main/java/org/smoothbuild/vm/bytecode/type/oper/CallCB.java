package org.smoothbuild.vm.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.CALL;

import org.smoothbuild.common.Hash;
import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.oper.CallB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

/**
 * This class is immutable.
 */
public class CallCB extends OperCB {
  public CallCB(Hash hash, TypeB evaluationT) {
    super(hash, CALL, evaluationT);
  }

  @Override
  public CallB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof CallCB);
    return new CallB(merkleRoot, exprDb);
  }
}
