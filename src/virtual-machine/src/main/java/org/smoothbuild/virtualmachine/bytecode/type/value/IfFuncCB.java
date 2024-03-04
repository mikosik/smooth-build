package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.IF_FUNC;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IfFuncB;

public final class IfFuncCB extends FuncCB {
  public IfFuncCB(Hash hash, FuncTB funcTB) {
    super(hash, IF_FUNC, funcTB);
  }

  @Override
  public IfFuncB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof IfFuncCB);
    return new IfFuncB(merkleRoot, exprDb);
  }

  @Override
  public boolean containsData() {
    return false;
  }
}
