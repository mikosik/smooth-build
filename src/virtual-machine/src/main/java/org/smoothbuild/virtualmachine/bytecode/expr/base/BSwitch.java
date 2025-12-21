package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BChoiceType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BSwitchKind;

/**
 * This class is thread-safe.
 */
public final class BSwitch extends BOperation {
  private static final int CHOICE_INDEX = 0;
  private static final int HANDLERS_INDEX = 1;

  private final Function0<BSubExprs, BytecodeException> subExprs =
      Function0.memoizer(this::fetchAndValidateSubExprs);

  public BSwitch(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb, 2);
    checkArgument(merkleRoot.kind() instanceof BSwitchKind);
  }

  @Override
  public BSwitchKind kind() {
    return (BSwitchKind) super.kind();
  }

  private BSubExprs fetchAndValidateSubExprs() throws BytecodeException {
    var members = members("choice", "handlers");
    var choice = members.get(CHOICE_INDEX).asExpr(BChoiceType.class);
    var choiceType = ((BChoiceType) choice.evaluationType());
    var expectedHandlersType = choiceType
        .alternatives()
        .map(a -> kindDb().lambda(list(a), evaluationType()))
        .construct(l -> kindDb().tuple(l));
    var handlers = members.get(HANDLERS_INDEX).asInstanceOf(BCombine.class);
    if (!handlers.evaluationType().equals(expectedHandlersType)) {
      throw new MemberHasWrongEvaluationTypeException(
          this, "handlers", expectedHandlersType, handlers.evaluationType());
    }
    return new BSubExprs(choice, handlers);
  }

  public BExpr choice() throws BytecodeException {
    return subExprs.apply().choice();
  }

  public BCombine handlers() throws BytecodeException {
    return subExprs.apply().handlers();
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = this.subExprs.apply();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("choice", subExprs.choice())
        .addField("handlers", subExprs.handlers())
        .toString();
  }

  private record BSubExprs(BExpr choice, BCombine handlers) {}
}
