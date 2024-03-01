package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.bytecode.type.value.LambdaCB;

/**
 * Lambda function (aka anonymous function).
 * This class is thread-safe.
 */
public final class LambdaB extends FuncB {
  public LambdaB(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.category() instanceof LambdaCB);
  }

  public ExprB body() throws BytecodeException {
    var body = readData();
    var resultT = type().result();
    var bodyT = body.evaluationType();
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
