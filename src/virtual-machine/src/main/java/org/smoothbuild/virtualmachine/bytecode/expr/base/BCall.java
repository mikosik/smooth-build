package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCallKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaType;

/**
 * This class is thread-safe.
 */
public final class BCall extends BOperation {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int LAMBDA_INDEX = 0;
  private static final int ARGUMENTS_INDEX = 1;

  public BCall(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BCallKind);
  }

  @Override
  public BCallKind kind() {
    return (BCallKind) super.kind();
  }

  @Override
  public BSubExprs subExprs() throws BytecodeException {
    var hashes = readDataAsHashChain(DATA_SEQ_SIZE);
    var lambda = readMemberFromHashChain(hashes, LAMBDA_INDEX);
    var lambdaEvaluationType = lambda.evaluationType();
    if (!(lambdaEvaluationType instanceof BLambdaType lambdaType)) {
      throw new MemberHasWrongEvaluationTypeException(
          hash(), kind(), "lambda", BLambdaType.class.getSimpleName(), lambdaEvaluationType);
    }
    var args = readMemberFromHashChain(hashes, ARGUMENTS_INDEX, "arguments", lambdaType.params());
    if (!evaluationType().equals(lambdaType.result())) {
      throw new MemberHasWrongEvaluationTypeException(
          hash(), kind(), "lambda.resultType", evaluationType(), lambdaType.result());
    }
    return new BSubExprs(lambda, args);
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = subExprs();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("lambda", subExprs.lambda())
        .addField("arguments", subExprs.arguments())
        .toString();
  }

  public static record BSubExprs(BExpr lambda, BExpr arguments) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(lambda, arguments);
    }
  }
}
