package org.smoothbuild.vm.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.LAMBDA;

import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.value.LambdaB;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public final class LambdaCB extends FuncCB {
  public LambdaCB(Hash hash, FuncTB funcTB) {
    super(hash, LAMBDA, funcTB);
  }

  @Override
  public LambdaB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof LambdaCB);
    return new LambdaB(merkleRoot, exprDb);
  }
}
