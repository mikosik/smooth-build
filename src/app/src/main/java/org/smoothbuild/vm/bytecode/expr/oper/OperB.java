package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.type.oper.OperCB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

/**
 * Operation.
 * This class is thread-safe.
 */
public abstract class OperB extends ExprB {
  public OperB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof OperCB);
  }

  @Override
  public OperCB category() {
    return (OperCB) super.category();
  }

  @Override
  public TypeB evaluationT() {
    return category().evaluationT();
  }

  public abstract ExprsB subExprs() throws BytecodeException;

  @Override
  public String exprToString() throws BytecodeException {
    return category().name() + "(???)";
  }
}
