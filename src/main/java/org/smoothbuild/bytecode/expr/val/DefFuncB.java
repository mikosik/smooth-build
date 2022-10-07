package org.smoothbuild.bytecode.expr.val;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.val.DefFuncCB;

/**
 * Function.
 * This class is thread-safe.
 */
public final class DefFuncB extends FuncB {
  public DefFuncB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof DefFuncCB);
  }

  public ExprB body() {
    var body = readDataAsExpr(dataHash());
    var resT = type().res();
    var bodyT = body.evalT();
    if (!resT.equals(bodyT)) {
      throw new DecodeExprWrongNodeTypeExc(hash(), category(), DATA_PATH, resT, bodyT);
    }
    return body;
  }

  @Override
  public String exprToString() {
    return "DefFunc(" + type().name() + ")";
  }
}
