package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.inst.DefFuncB;
import org.smoothbuild.bytecode.type.inst.FuncTB;
import org.smoothbuild.bytecode.type.oper.ClosurizeCB;

import com.google.common.collect.ImmutableList;

/**
 * Closurize - create closure.
 * This class is thread-safe.
 */
public final class ClosurizeB extends ExprB {
  public ClosurizeB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof ClosurizeCB);
  }

  @Override
  public FuncTB evalT() {
    return category().evalT();
  }

  @Override
  public ClosurizeCB category() {
    return (ClosurizeCB) super.category();
  }

  public DefFuncB buildClosure(ImmutableList<ExprB> environment) {
    var bytecodeDb = bytecodeDb();
    var environmentB = bytecodeDb.combine(environment);
    return bytecodeDb.defFunc(evalT(), environmentB, body());
  }

  private ExprB body() {
    return readDataAsExpr();
  }

  @Override
  public String exprToString() {
    return category().name() + "(???)";
  }
}
