package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BIfKind;

/**
 * 'If' operation.
 * This class is thread-safe.
 */
public final class BIf extends BOperation {
  private static final int CONDITION_INDEX = 0;
  private static final int THEN_INDEX = 1;
  private static final int ELSE_INDEX = 2;

  private static final List<String> SUB_EXPR_NAMES = list("condition", "then", "else");

  private final Function0<BSubExprs, BytecodeException> subExprs =
      Function0.memoizer(this::fetchAndValidateSubExprs);

  public BIf(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb, SUB_EXPR_NAMES.size());
    checkArgument(merkleRoot.kind() instanceof BIfKind);
  }

  private BSubExprs fetchAndValidateSubExprs() throws BytecodeException {
    var subExprs = createSubExprList(SUB_EXPR_NAMES);
    var condition = subExprs.get(CONDITION_INDEX).asExpr(kindDb().bool());
    var then_ = subExprs.get(THEN_INDEX).asExpr(evaluationType());
    var else_ = subExprs.get(ELSE_INDEX).asExpr(evaluationType());
    return new BSubExprs(condition, then_, else_);
  }

  public BExpr condition() throws BytecodeException {
    return subExprs.apply().condition();
  }

  public BExpr then_() throws BytecodeException {
    return subExprs.apply().then_();
  }

  public BExpr else_() throws BytecodeException {
    return subExprs.apply().else_();
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = this.subExprs.apply();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("condition", subExprs.condition())
        .addField("then", subExprs.then_())
        .addField("else", subExprs.else_())
        .toString();
  }

  private record BSubExprs(BExpr condition, BExpr then_, BExpr else_) {}
}
