package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.bytecode.type.value.BLambdaCategory;

/**
 * Lambda function (aka anonymous function).
 * This class is thread-safe.
 */
public final class BLambda extends BFunc {
  public BLambda(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.category() instanceof BLambdaCategory);
  }

  public BExpr body() throws BytecodeException {
    var body = readData();
    var resultType = type().result();
    var bodyType = body.evaluationType();
    if (!resultType.equals(bodyType)) {
      throw new DecodeExprWrongNodeTypeException(
          hash(), category(), DATA_PATH, resultType, bodyType);
    }
    return body;
  }

  @Override
  public String exprToString() {
    return "Lambda(" + type().name() + ")";
  }
}
