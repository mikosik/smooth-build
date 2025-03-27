package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BOrderKind;

/**
 * This class is thread-safe.
 */
public final class BOrder extends BOperation {
  public BOrder(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BOrderKind);
  }

  @Override
  public BOrderKind kind() {
    return (BOrderKind) super.kind();
  }

  @Override
  public BArrayType evaluationType() {
    return kind().evaluationType();
  }

  @Override
  public BSubExprs subExprs() throws BytecodeException {
    return new BSubExprs(elements());
  }

  public List<BExpr> elements() throws BytecodeException {
    var elements = readDataAsExprChain(BExpr.class);
    var expectedElementType = evaluationType().element();
    for (int i = 0; i < elements.size(); i++) {
      var actualType = elements.get(i).evaluationType();
      if (!expectedElementType.equals(actualType)) {
        throw new MemberHasWrongTypeException(
            hash(), kind(), "elements[" + i + "]", expectedElementType, actualType);
      }
    }
    return elements;
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = subExprs();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addListField("elements", subExprs.elements())
        .toString();
  }

  public static record BSubExprs(List<BExpr> elements) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return elements;
    }
  }
}
