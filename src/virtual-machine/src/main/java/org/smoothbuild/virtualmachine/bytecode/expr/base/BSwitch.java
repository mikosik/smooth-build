package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BChoiceType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BSwitchKind;

/**
 * This class is thread-safe.
 */
public final class BSwitch extends BOperation {
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
    var hashes = readDataAsHashChain(2);
    var choice = readMemberFromHashChain(hashes, 0);
    if (!(choice.evaluationType() instanceof BChoiceType choiceType)) {
      throw new MemberHasWrongTypeException(
          hash(), kind(), "choice", BChoiceType.class, choice.evaluationType().getClass());
    }
    var expectedHandlersType = choiceType
        .alternatives()
        .map(a -> kindDb().lambda(list(a), evaluationType()))
        .construct(l -> kindDb().tuple(l));
    var handlers = readAndCastMemberFromHashChain(hashes, 1, "handlers", BCombine.class);
    if (!(handlers.evaluationType().equals(expectedHandlersType))) {
      throw new MemberHasWrongEvaluationTypeException(
          hash(), kind(), "handlers", expectedHandlersType, handlers.evaluationType());
    }
    return new BSubExprs(choice, handlers);
  }

  public static record BSubExprs(BExpr choice, BCombine handlers) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(choice, handlers);
    }
  }
}
