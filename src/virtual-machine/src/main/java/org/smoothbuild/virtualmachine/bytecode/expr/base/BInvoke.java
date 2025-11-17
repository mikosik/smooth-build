package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BInvokeKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;

/**
 * Invocation of native function.
 * This class is thread-safe.
 */
public final class BInvoke extends BOperation {
  public static final int METHOD_INDEX = 0;
  public static final int IS_PURE_INDEX = 1;
  public static final int ARGUMENTS_INDEX = 2;

  public BInvoke(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BInvokeKind);
  }

  @Override
  public BSubExprs subExprs() throws BytecodeException {
    var members = fetchMembers();
    var method = members.get(METHOD_INDEX).asExpr(kindDb().method());
    var isPure = members.get(IS_PURE_INDEX).asExpr(kindDb().bool());
    var arguments = members.get(ARGUMENTS_INDEX).asExpr(BTupleType.class);
    return new BSubExprs(method, isPure, arguments);
  }

  public BBool isPure() throws BytecodeException {
    return fetchMembers().get(IS_PURE_INDEX).asInstanceOf(BBool.class);
  }

  private List<Member> fetchMembers() throws BytecodeException {
    return members("method", "isPure", "arguments");
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = subExprs();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("method", subExprs.method())
        .addField("isPure", subExprs.isPure())
        .addField("arguments", subExprs.arguments())
        .toString();
  }

  public static record BSubExprs(BExpr method, BExpr isPure, BExpr arguments) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(method, isPure, arguments);
    }
  }
}
