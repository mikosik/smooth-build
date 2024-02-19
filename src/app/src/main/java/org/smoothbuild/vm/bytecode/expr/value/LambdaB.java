package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.vm.bytecode.type.value.LambdaCB;

/**
 * Lambda function (aka anonymous function).
 * This class is thread-safe.
 */
public final class LambdaB extends FuncB {
  public LambdaB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof LambdaCB);
  }

  public ExprB body() throws BytecodeException {
    var body = readData();
    var resultT = type().result();
    var bodyT = body.evaluationT();
    if (!resultT.equals(bodyT)) {
      throw new DecodeExprWrongNodeTypeException(hash(), category(), DATA_PATH, resultT, bodyT);
    }
    return body;
  }

  @Override
  public String exprToString() {
    return "Lambda(" + type().name() + ")";
  }
}
