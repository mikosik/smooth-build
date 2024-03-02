package org.smoothbuild.virtualmachine.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.CALL;

import org.smoothbuild.common.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CallB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

/**
 * This class is immutable.
 */
public class CallCB extends OperCB {
  public CallCB(Hash hash, TypeB evaluationType) {
    super(hash, CALL, evaluationType);
  }

  @Override
  public CallB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof CallCB);
    return new CallB(merkleRoot, exprDb);
  }
}
