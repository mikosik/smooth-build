package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaType;

/**
 * Lambda (aka anonymous function).
 * This class is thread-safe.
 */
public final class BLambda extends BValue {
  public BLambda(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BLambdaType);
  }

  @Override
  public BLambdaType evaluationType() {
    return type();
  }

  @Override
  public BLambdaType type() {
    return ((BLambdaType) kind());
  }

  public BExpr body() throws BytecodeException {
    return readData("body", type().result());
  }

  @Override
  public String exprToString() {
    return "Lambda(" + type().name() + ")";
  }
}
