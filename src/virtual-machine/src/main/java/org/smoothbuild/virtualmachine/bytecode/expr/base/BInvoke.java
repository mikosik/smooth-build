package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
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

  private static final List<String> MEMBER_NAMES = list("method", "isPure", "arguments");

  private final Function0<BSubExprs, BytecodeException> subExprs =
      Function0.memoizer(this::fetchAndValidateSubExprs);

  public BInvoke(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb, MEMBER_NAMES.size());
    checkArgument(merkleRoot.kind() instanceof BInvokeKind);
  }

  private BSubExprs fetchAndValidateSubExprs() throws BytecodeException {
    var members = createMemberList(MEMBER_NAMES);
    var method = members.get(METHOD_INDEX).asExpr(kindDb().method());
    var isPure = members.get(IS_PURE_INDEX).asExpr(kindDb().bool());
    var arguments = members.get(ARGUMENTS_INDEX).asExpr(BTupleType.class);
    return new BSubExprs(method, isPure, arguments);
  }

  public BExpr method() throws BytecodeException {
    return subExprs.apply().method();
  }

  public BExpr isPure() throws BytecodeException {
    return subExprs.apply().isPure();
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
        .addField("method", subExprs.method())
        .addField("isPure", subExprs.isPure())
        .addField("arguments", subExprs.arguments())
        .toString();
  }

  private record BSubExprs(BExpr method, BExpr isPure, BExpr arguments) {}
}
