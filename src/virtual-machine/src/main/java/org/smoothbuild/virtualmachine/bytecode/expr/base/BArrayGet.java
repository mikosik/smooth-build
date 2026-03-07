package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayGetKind;

/**
 * This class is thread-safe.
 */
public final class BArrayGet extends BOperation {
  public static final int ARRAY_INDEX = 0;
  public static final int INDEX_INDEX = 1;

  private static final List<String> SUB_EXPR_NAMES = list("array", "index");

  private final Function0<SubExprs, BytecodeException> subExprs =
      Function0.memoizer(this::fetchAndValidateSubExprs);

  public BArrayGet(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb, SUB_EXPR_NAMES.size());
    checkArgument(merkleRoot.kind() instanceof BArrayGetKind);
  }

  @Override
  public BArrayGetKind kind() {
    return (BArrayGetKind) super.kind();
  }

  private SubExprs fetchAndValidateSubExprs() throws BytecodeException {
    var subExprs = createSubExprList(SUB_EXPR_NAMES);
    var array = subExprs.get(ARRAY_INDEX).asExpr(kindDb().array(evaluationType()));
    var index = subExprs.get(INDEX_INDEX).asExpr(kindDb().int_());
    return new SubExprs(array, index);
  }

  public BExpr array() throws BytecodeException {
    return subExprs().array();
  }

  public BExpr index() throws BytecodeException {
    return subExprs().index();
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = subExprs();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("array", subExprs.array())
        .addField("index", subExprs.index())
        .toString();
  }

  private SubExprs subExprs() throws BytecodeException {
    return subExprs.apply();
  }

  private record SubExprs(BExpr array, BExpr index) {}
}
