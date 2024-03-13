package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.NativeFuncB;

public final class NativeFuncCB extends FuncCB {
  public NativeFuncCB(Hash hash, FuncTB funcTB) {
    super(hash, "NATIVE_FUNC", funcTB, NativeFuncB.class);
  }

  @Override
  public NativeFuncB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof NativeFuncCB);
    return new NativeFuncB(merkleRoot, exprDb);
  }
}
