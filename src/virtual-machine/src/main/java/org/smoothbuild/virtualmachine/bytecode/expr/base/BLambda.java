package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaKind;

/**
 * Lambda function (aka anonymous function).
 * This class is thread-safe.
 */
public final class BLambda extends BFunc {
  public BLambda(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BLambdaKind);
  }

  public BExpr body() throws BytecodeException {
    return readData("body", type().result());
  }

  @Override
  public String exprToString() {
    return "Lambda(" + type().name() + ")";
  }
}
