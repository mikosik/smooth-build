package org.smoothbuild.bytecode.expr.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.value.DefinedFuncCB;

/**
 * Defined Function.
 * This class is thread-safe.
 */
public final class DefinedFuncB extends FuncB {
  public DefinedFuncB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof DefinedFuncCB);
  }

  public ExprB body() {
    var body = readDataAsExpr();
    var resT = type().result();
    var bodyT = body.evalT();
    if (!resT.equals(bodyT)) {
      throw new DecodeExprWrongNodeTypeExc(hash(), category(), DATA_PATH, resT, bodyT);
    }
    return body;
  }

  @Override
  public String exprToString() {
    return "DefinedFunc(" + type().name() + ")";
  }
}
