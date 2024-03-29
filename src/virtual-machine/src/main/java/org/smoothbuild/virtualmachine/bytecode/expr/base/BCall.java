package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongMemberEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCallKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaType;

/**
 * This class is thread-safe.
 */
public final class BCall extends BOperation {
  public BCall(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BCallKind);
  }

  @Override
  public BCallKind kind() {
    return (BCallKind) super.kind();
  }

  @Override
  public SubExprsB subExprs() throws BytecodeException {
    var hashes = readDataAsHashChain(2);
    var lambda = readMemberFromHashChain(hashes, 0);
    var lambdaEvaluationType = lambda.evaluationType();
    if (!(lambdaEvaluationType instanceof BLambdaType lambdaType)) {
      throw new DecodeExprWrongMemberEvaluationTypeException(
          hash(), kind(), "lambda", BLambdaType.class.getSimpleName(), lambdaEvaluationType);
    }
    var args = readMemberFromHashChain(hashes, 1, "arguments", lambdaType.params());
    if (!evaluationType().equals(lambdaType.result())) {
      throw new DecodeExprWrongMemberEvaluationTypeException(
          hash(), kind(), "function.resultType", evaluationType(), lambdaType.result());
    }
    return new SubExprsB(lambda, args);
  }

  public static record SubExprsB(BExpr lambda, BExpr arguments) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(lambda, arguments);
    }
  }
}
