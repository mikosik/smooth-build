package org.smoothbuild.virtualmachine.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

/**
 * This class is immutable.
 */
public class BCallKind extends BOperKind {
  public BCallKind(Hash hash, BType evaluationType) {
    super(hash, "CALL", BCall.class, evaluationType);
  }

  @Override
  public BCall newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BCallKind);
    return new BCall(merkleRoot, exprDb);
  }
}
