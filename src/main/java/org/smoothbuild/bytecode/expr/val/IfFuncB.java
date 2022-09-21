package org.smoothbuild.bytecode.expr.val;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.type.val.IfFuncCB;

/**
 * If function.
 * This class is thread-safe.
 */
public final class IfFuncB extends FuncB {
  public IfFuncB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof IfFuncCB);
  }

  @Override
  public String exprToString() {
    return "IfFunc(" + type().name() + ")";
  }
}
