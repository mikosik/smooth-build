package org.smoothbuild.bytecode.expr.inst;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.type.inst.ClosureCB;

/**
 * Defined Function.
 * This class is thread-safe.
 */
public final class ClosureB extends FuncB {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int ENVIRONMENT_IDX = 0;
  private static final int BODY_IDX = 1;

  public ClosureB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof ClosureCB);
  }

  public CombineB environment() {
    return readDataSeqElem(ENVIRONMENT_IDX, DATA_SEQ_SIZE, CombineB.class);
  }

  public ExprB body() {
    var body = readDataSeqElem(BODY_IDX, DATA_SEQ_SIZE, ExprB.class);
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
