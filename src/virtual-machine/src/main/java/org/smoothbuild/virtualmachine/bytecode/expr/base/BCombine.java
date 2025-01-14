package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.CombineHasWrongElementsSizeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.NodeHasWrongTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCombineKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

/**
 * This class is thread-safe.
 */
public final class BCombine extends BOperation {
  public BCombine(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BCombineKind);
  }

  @Override
  public BCombineKind kind() {
    return (BCombineKind) super.kind();
  }

  @Override
  public BTupleType evaluationType() {
    return kind().evaluationType();
  }

  @Override
  public BSubExprs subExprs() throws BytecodeException {
    return new BSubExprs(items());
  }

  public List<BExpr> items() throws BytecodeException {
    var expectedTypes = evaluationType().elements();
    var items = readDataAsExprChain(BExpr.class);
    if (items.size() != expectedTypes.size()) {
      throw new CombineHasWrongElementsSizeException(hash(), kind(), items.size());
    }
    for (int i = 0; i < items.size(); i++) {
      BExpr item = items.get(i);
      BType type = expectedTypes.get(i);
      if (!type.equals(item.evaluationType())) {
        throw new NodeHasWrongTypeException(
            hash(), kind(), "elements", i, expectedTypes.get(i), items.get(i).evaluationType());
      }
    }
    return items;
  }

  public static record BSubExprs(List<BExpr> items) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return items;
    }
  }
}
