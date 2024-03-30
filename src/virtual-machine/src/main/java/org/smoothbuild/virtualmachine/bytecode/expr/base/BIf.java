package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

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
  public BIf(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BIfKind);
  }

  @Override
  public BSubExprs subExprs() throws BytecodeException {
    var hashes = readDataAsHashChain(3);
    var condition = readMemberFromHashChain(hashes, 0, "condition", kindDb().bool());
    var then_ = readMemberFromHashChain(hashes, 1, "then", evaluationType());
    var else_ = readMemberFromHashChain(hashes, 2, "else", evaluationType());
    return new BSubExprs(condition, then_, else_);
  }

  public static record BSubExprs(BExpr condition, BExpr then_, BExpr else_) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(condition, then_, else_);
    }
  }
}
