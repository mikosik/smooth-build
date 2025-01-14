package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BChooseKind;

/**
 * This class is thread-safe.
 */
public final class BChoose extends BOperation {
  public BChoose(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BChooseKind);
  }

  @Override
  public BChooseKind kind() {
    return (BChooseKind) super.kind();
  }

  @Override
  public BSubExprs subExprs() throws BytecodeException {
    var hashes = readDataAsHashChain(2);
    var choice = readAndCastMemberFromHashChain(hashes, 0, "choice", BChoice.class);
    var handlersType = choice
        .evaluationType()
        .alternatives()
        .map(a -> kindDb().lambda(list(a), evaluationType()))
        .construct(l -> kindDb().tuple(l));
    var handlers = readMemberFromHashChain(hashes, 1, "handlers", handlersType);
    return new BSubExprs(choice, handlers);
  }

  public static record BSubExprs(BExpr choice, BExpr handlers) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(choice, handlers);
    }
  }
}
