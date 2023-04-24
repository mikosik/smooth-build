package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.vm.bytecode.expr.value.ClosureB;
import org.smoothbuild.vm.bytecode.expr.value.ExprFuncB;
import org.smoothbuild.vm.bytecode.type.oper.ClosurizeCB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;

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
  public ClosurizeSubExprsB subExprs() {
    return new ClosurizeSubExprsB(func());
  }

  @Override
  public FuncTB evaluationT() {
    return category().evaluationT();
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

  public ExprFuncB func() {
    var exprFuncB = readData(ExprFuncB.class);
    var evaluationT = evaluationT();
    var funcT = exprFuncB.type();
    if (!evaluationT.equals(funcT)) {
      throw new DecodeExprWrongNodeTypeExc(hash(), category(), DATA_PATH, evaluationT, funcT);
    }
    return exprFuncB;
  }

  @Override
  public String exprToString() {
    return category().name() + "(???)";
  }
}
