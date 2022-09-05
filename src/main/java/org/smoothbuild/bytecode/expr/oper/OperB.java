package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.type.oper.OperCatB;
import org.smoothbuild.bytecode.type.val.TypeB;

/**
 * Operation.
 * This class is thread-safe.
 */
public abstract class OperB extends ExprB {
  public OperB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.cat() instanceof OperCatB);
  }

  @Override
  public OperCatB cat() {
    return (OperCatB) super.cat();
  }

  @Override
  public TypeB type() {
    return cat().evalT();
  }

  @Override
  public String exprToString() {
    return cat().name() + "(???)";
  }
}
