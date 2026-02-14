package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCallKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaType;

/**
 * This class is thread-safe.
 */
public final class BCall extends BOperation {
  private static final int LAMBDA_INDEX = 0;
  private static final int ARGUMENTS_INDEX = 1;

  private static final List<String> SUB_EXPR_NAMES = list("lambda", "arguments");

  private final Function0<SubExprs, BytecodeException> subExprs =
      Function0.memoizer(this::fetchAndValidateSubExprs);

  public BCall(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb, SUB_EXPR_NAMES.size());
    checkArgument(merkleRoot.kind() instanceof BCallKind);
  }

  @Override
  public BCallKind kind() {
    return (BCallKind) super.kind();
  }

  private SubExprs fetchAndValidateSubExprs() throws BytecodeException {
    var subExprs = createSubExprList(SUB_EXPR_NAMES);
    var lambda = subExprs.get(LAMBDA_INDEX).asExpr(BLambdaType.class);
    var lambdaType = (BLambdaType) lambda.evaluationType();
    checkSubExprEvaluationType("lambda.resultType", lambdaType.result(), evaluationType());
    var args = subExprs.get(ARGUMENTS_INDEX).asExpr(lambdaType.params());
    return new SubExprs(lambda, args);
  }

  public BExpr lambda() throws BytecodeException {
    return subExprs().lambda();
  }

  public BExpr arguments() throws BytecodeException {
    return subExprs().arguments();
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

  private SubExprs subExprs() throws BytecodeException {
    return subExprs.apply();
  }

  private record SubExprs(BExpr lambda, BExpr arguments) {}
}
