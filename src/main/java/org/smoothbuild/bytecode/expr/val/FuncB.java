package org.smoothbuild.bytecode.expr.val;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.val.FuncTB;

/**
 * Function.
 * This class is thread-safe.
 */
public final class FuncB extends ValB {
  public FuncB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
  }

  @Override
  public FuncTB type() {
    return (FuncTB) super.type();
  }

  @Override
  public FuncTB cat() {
    return (FuncTB) super.cat();
  }

  public ExprB body() {
    var body = readExpr(DATA_PATH, dataHash(), ExprB.class);
    var resT = cat().res();
    var bodyT = body.type();
    if (!resT.equals(bodyT)) {
      throw new DecodeExprWrongNodeTypeExc(hash(), cat(), DATA_PATH, resT, bodyT);
    }
    return body;
  }

  @Override
  public String exprToString() {
    return "Func(" + cat().name() + ")";
  }
}
