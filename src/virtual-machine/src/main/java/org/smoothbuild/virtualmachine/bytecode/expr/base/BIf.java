package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
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

  public BIf(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BIfKind);
  }

  @Override
  public BSubExprs subExprs() throws BytecodeException {
    var members = members("condition", "then", "else");
    var condition = members.get(CONDITION_INDEX).asExpr(kindDb().bool());
    var then_ = members.get(THEN_INDEX).asExpr(evaluationType());
    var else_ = members.get(ELSE_INDEX).asExpr(evaluationType());
    return new BSubExprs(condition, then_, else_);
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = subExprs();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("condition", subExprs.condition())
        .addField("then", subExprs.then_())
        .addField("else", subExprs.else_())
        .toString();
  }

  public static record BSubExprs(BExpr condition, BExpr then_, BExpr else_) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(condition, then_, else_);
    }
  }
}
