package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IfFuncB;

public final class IfFuncCB extends FuncCB {
  public IfFuncCB(Hash hash, FuncTB funcTB) {
    super(hash, "IF", funcTB, IfFuncB.class);
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
