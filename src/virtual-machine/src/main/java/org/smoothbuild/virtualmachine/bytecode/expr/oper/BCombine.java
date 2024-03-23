package org.smoothbuild.virtualmachine.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeCombineWrongElementsSizeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BCombineCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

/**
 * This class is thread-safe.
 */
public class BCombine extends BOper {
  public BCombine(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.category() instanceof BCombineCategory);
  }

  @Override
  public BCombineCategory category() {
    return (BCombineCategory) super.category();
  }

  @Override
  public BTupleType evaluationType() {
    return category().evaluationType();
  }

  @Override
  public SubExprsB subExprs() throws BytecodeException {
    return new SubExprsB(items());
  }

  public List<BExpr> items() throws BytecodeException {
    List<BType> expectedTypes = category().evaluationType().elements();
    List<BExpr> items = readDataAsExprChain(BExpr.class);
    if (items.size() != expectedTypes.size()) {
      throw new DecodeCombineWrongElementsSizeException(hash(), category(), items.size());
    }
    for (int i = 0; i < items.size(); i++) {
      BExpr item = items.get(i);
      BType type = expectedTypes.get(i);
      if (!type.equals(item.evaluationType())) {
        throw new DecodeExprWrongNodeTypeException(
            hash(),
            category(),
            "elements",
            i,
            expectedTypes.get(i),
            items.get(i).evaluationType());
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
