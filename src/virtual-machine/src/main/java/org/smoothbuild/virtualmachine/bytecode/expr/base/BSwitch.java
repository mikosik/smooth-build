package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.SubExprHasWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BSwitchKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BVariantType;

/**
 * This class is thread-safe.
 */
public final class BSwitch extends BOperation {
  private static final int VARIANT_INDEX = 0;
  private static final int HANDLERS_INDEX = 1;

  private static final List<String> SUB_EXPR_NAMES = list("variant", "handlers");

  private final Function0<SubExprs, BytecodeException> subExprs =
      Function0.memoizer(this::fetchAndValidateSubExprs);

  public BSwitch(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb, SUB_EXPR_NAMES.size());
    checkArgument(merkleRoot.kind() instanceof BSwitchKind);
  }

  @Override
  public BSwitchKind kind() {
    return (BSwitchKind) super.kind();
  }

  private SubExprs fetchAndValidateSubExprs() throws BytecodeException {
    var subExprs = createSubExprList(SUB_EXPR_NAMES);
    var variant = subExprs.get(VARIANT_INDEX).asExpr(BVariantType.class);
    var variantType = ((BVariantType) variant.evaluationType());
    var expectedHandlersType = variantType
        .alternatives()
        .map(a -> kindDb().lambda(list(a), evaluationType()))
        .construct(l -> kindDb().tuple(l));
    var handlers = subExprs.get(HANDLERS_INDEX).asInstanceOf(BConstructTuple.class);
    if (!handlers.evaluationType().equals(expectedHandlersType)) {
      throw new SubExprHasWrongEvaluationTypeException(
          this, "handlers", expectedHandlersType, handlers.evaluationType());
    }
    return new SubExprs(variant, handlers);
  }

  public BExpr variant() throws BytecodeException {
    return subExprs().variant();
  }

  public BConstructTuple handlers() throws BytecodeException {
    return subExprs().handlers();
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = subExprs();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("variant", subExprs.variant())
        .addField("handlers", subExprs.handlers())
        .toString();
  }

  private SubExprs subExprs() throws BytecodeException {
    return subExprs.apply();
  }

  private record SubExprs(BExpr variant, BConstructTuple handlers) {}
}
