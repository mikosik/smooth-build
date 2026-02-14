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

  private static final List<String> SUB_EXPR_NAMES = list("method", "isPure", "arguments");

  private final Function0<SubExprs, BytecodeException> subExprs =
      Function0.memoizer(this::fetchAndValidateSubExprs);

  public BInvoke(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb, SUB_EXPR_NAMES.size());
    checkArgument(merkleRoot.kind() instanceof BInvokeKind);
  }

  private SubExprs fetchAndValidateSubExprs() throws BytecodeException {
    var subExprs = createSubExprList(SUB_EXPR_NAMES);
    var method = subExprs.get(METHOD_INDEX).asExpr(kindDb().method());
    var isPure = subExprs.get(IS_PURE_INDEX).asExpr(kindDb().bool());
    var arguments = subExprs.get(ARGUMENTS_INDEX).asExpr(BTupleType.class);
    return new SubExprs(method, isPure, arguments);
  }

  public BExpr method() throws BytecodeException {
    return subExprs().method();
  }

  public BExpr isPure() throws BytecodeException {
    return subExprs().isPure();
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
        .addField("method", subExprs.method())
        .addField("isPure", subExprs.isPure())
        .addField("arguments", subExprs.arguments())
        .toString();
  }

  private SubExprs subExprs() throws BytecodeException {
    return subExprs.apply();
  }

  private record SubExprs(BExpr method, BExpr isPure, BExpr arguments) {}
}
