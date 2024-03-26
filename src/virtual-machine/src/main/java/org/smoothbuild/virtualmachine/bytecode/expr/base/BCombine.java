package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeCombineWrongElementsSizeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.bytecode.type.base.BCombineKind;
import org.smoothbuild.virtualmachine.bytecode.type.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.base.BType;

/**
 * This class is thread-safe.
 */
public final class BCombine extends BOper {
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
  public SubExprsB subExprs() throws BytecodeException {
    return new SubExprsB(items());
  }

  public List<BExpr> items() throws BytecodeException {
    List<BType> expectedTypes = kind().evaluationType().elements();
    List<BExpr> items = readDataAsExprChain(BExpr.class);
    if (items.size() != expectedTypes.size()) {
      throw new DecodeCombineWrongElementsSizeException(hash(), kind(), items.size());
    }
    for (int i = 0; i < items.size(); i++) {
      BExpr item = items.get(i);
      BType type = expectedTypes.get(i);
      if (!type.equals(item.evaluationType())) {
        throw new DecodeExprWrongNodeTypeException(
            hash(), kind(), "elements", i, expectedTypes.get(i), items.get(i).evaluationType());
      }
    }
    return items;
  }

  public static record SubExprsB(List<BExpr> items) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return items;
    }
  }
}
