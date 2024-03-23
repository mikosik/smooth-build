package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;

public abstract sealed class BFunc extends BValue permits BLambda, BIf, BMap, BNativeFunc {
  public BFunc(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.category() instanceof BFuncCategory);
  }

  @Override
  public BFuncType evaluationType() {
    return type();
  }

  @Override
  public BFuncType type() {
    return ((BFuncCategory) category()).type();
  }
}
