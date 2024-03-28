package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCallKind;

/**
 * This class is thread-safe.
 */
public final class BCall extends BOperation {
  public BCall(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BCallKind);
  }

  @Override
  public BCallKind kind() {
    return (BCallKind) super.kind();
  }

  @Override
  public SubExprsB subExprs() throws BytecodeException {
    var hashes = readDataAsHashChain(2);
    var args = readAndCastMemberFromHashChain(hashes, 1, "arguments", BCombine.class);
    var expectedLambdaType = kindDb().lambda(args.evaluationType(), evaluationType());
    var lambda = readMemberFromHashChain(hashes, 0, "lambda", expectedLambdaType);
    return new SubExprsB(lambda, castNode(dataNodePath(1), args, BCombine.class));
  }

  public static record SubExprsB(BExpr lambda, BCombine args) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(lambda, args());
    }
  }
}
