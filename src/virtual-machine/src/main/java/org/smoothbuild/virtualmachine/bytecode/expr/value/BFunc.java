package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncKind;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;

public abstract sealed class BFunc extends BValue permits BLambda, BMap, BNativeFunc {
  public BFunc(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BFuncKind);
  }

  @Override
  public BFuncType evaluationType() {
    return type();
  }

  @Override
  public BFuncType type() {
    return ((BFuncKind) kind()).type();
  }
}
