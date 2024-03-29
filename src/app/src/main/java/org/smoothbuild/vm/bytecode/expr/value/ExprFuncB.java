package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.vm.bytecode.type.value.ExprFuncCB;

/**
 * Named Expression Function.
 * This class is thread-safe.
 */
public final class ExprFuncB extends FuncB {
  public ExprFuncB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof ExprFuncCB);
  }

  public ExprB body() {
    var body = readData();
    var resultT = type().result();
    var bodyT = body.evaluationT();
    if (!resultT.equals(bodyT)) {
      throw new DecodeExprWrongNodeTypeExc(hash(), category(), DATA_PATH, resultT, bodyT);
    }
    return body;
  }

  @Override
  public String exprToString() {
    return "ExprFunc(" + type().name() + ")";
  }
}
