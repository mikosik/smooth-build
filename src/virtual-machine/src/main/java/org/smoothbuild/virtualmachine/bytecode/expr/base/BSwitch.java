package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
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

  private static final int DATA_SEQ_SIZE = 2;
  private static final int CHOICE_INDEX = 0;
  private static final int HANDLERS_INDEX = 1;

  public BSwitch(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BSwitchKind);
  }

  @Override
  public BSwitchKind kind() {
    return (BSwitchKind) super.kind();
  }

  @Override
  public BSubExprs subExprs() throws BytecodeException {
    var hashes = readDataAsHashChain(DATA_SEQ_SIZE);
    var choice = readMemberFromHashChain(hashes, CHOICE_INDEX);
    if (!(choice.evaluationType() instanceof BChoiceType choiceType)) {
      throw new MemberHasWrongEvaluationTypeException(
          hash(), kind(), "choice", BChoiceType.class, choice.evaluationType());
    }
    var expectedHandlersType = choiceType
        .alternatives()
        .map(a -> kindDb().lambda(list(a), evaluationType()))
        .construct(l -> kindDb().tuple(l));
    var handlers =
        readAndCastMemberFromHashChain(hashes, HANDLERS_INDEX, "handlers", BCombine.class);
    if (!handlers.evaluationType().equals(expectedHandlersType)) {
      throw new MemberHasWrongEvaluationTypeException(
          hash(), kind(), "handlers", expectedHandlersType, handlers.evaluationType());
    }
    return new BSubExprs(choice, handlers);
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = subExprs();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("choice", subExprs.choice())
        .addField("handlers", subExprs.handlers())
        .toString();
  }

  public static record BSubExprs(BExpr choice, BCombine handlers) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(choice, handlers);
    }
  }
}
