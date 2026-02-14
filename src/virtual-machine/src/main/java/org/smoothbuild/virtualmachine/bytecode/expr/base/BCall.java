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

  private static final List<String> MEMBER_NAMES = list("lambda", "arguments");

  private final Function0<BSubExprs, BytecodeException> subExprs =
      Function0.memoizer(this::fetchAndValidateSubExprs);

  public BCall(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb, MEMBER_NAMES.size());
    checkArgument(merkleRoot.kind() instanceof BCallKind);
  }

  @Override
  public BCallKind kind() {
    return (BCallKind) super.kind();
  }

  private BSubExprs fetchAndValidateSubExprs() throws BytecodeException {
    var members = createMemberList(MEMBER_NAMES);
    var lambda = members.get(LAMBDA_INDEX).asExpr(BLambdaType.class);
    var lambdaType = (BLambdaType) lambda.evaluationType();
    checkMemberEvaluationType("lambda.resultType", lambdaType.result(), evaluationType());
    var args = members.get(ARGUMENTS_INDEX).asExpr(lambdaType.params());
    return new BSubExprs(lambda, args);
  }

  public BExpr lambda() throws BytecodeException {
    return subExprs.apply().lambda();
  }

  public BExpr arguments() throws BytecodeException {
    return subExprs.apply().arguments();
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = this.subExprs.apply();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("lambda", subExprs.lambda())
        .addField("arguments", subExprs.arguments())
        .toString();
  }

  private record BSubExprs(BExpr lambda, BExpr arguments) {}
}
