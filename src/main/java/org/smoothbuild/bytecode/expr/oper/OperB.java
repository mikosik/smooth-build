package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.type.oper.OperCB;
import org.smoothbuild.bytecode.type.value.TypeB;

import com.google.common.collect.ImmutableList;

/**
 * Operation.
 * This class is thread-safe.
 */
public abstract class OperB extends ExprB {
  public OperB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof OperCB);
  }

  public abstract ImmutableList<ExprB> dataSeq();

  @Override
  public OperCB category() {
    return (OperCB) super.category();
  }

  @Override
  public TypeB evalT() {
    return category().evalT();
  }

  @Override
  public String exprToString() {
    return category().name() + "(???)";
  }
}
