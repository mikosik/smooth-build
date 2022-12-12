package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.value.ClosureB;
import org.smoothbuild.bytecode.expr.value.DefinedFuncB;
import org.smoothbuild.bytecode.type.oper.ClosurizeCB;
import org.smoothbuild.bytecode.type.value.FuncTB;

import com.google.common.collect.ImmutableList;

/**
 * Closurize - create closure.
 * This class is thread-safe.
 */
public final class ClosurizeB extends OperB {
  public ClosurizeB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof ClosurizeCB);
  }

  @Override
  public ImmutableList<ExprB> dataSeq() {
    return list(func());
  }

  @Override
  public FuncTB evalT() {
    return category().evalT();
  }

  @Override
  public ClosurizeCB category() {
    return (ClosurizeCB) super.category();
  }

  public ClosureB buildClosure(ImmutableList<ExprB> environment) {
    var bytecodeDb = bytecodeDb();
    var environmentB = bytecodeDb.combine(environment);
    return bytecodeDb.closure(environmentB, func());
  }

  private DefinedFuncB func() {
    return readDataAsExpr(DefinedFuncB.class);
  }

  @Override
  public String exprToString() {
    return category().name() + "(???)";
  }
}
