package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCombineKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;

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
    var items = readDataAsExprChain(BExpr.class);
    var actualType = kindDb().tuple(items.map(BExpr::evaluationType));
    if (!actualType.equals(evaluationType())) {
      throw new MemberHasWrongTypeException(
          hash(), kind(), "elements", evaluationType(), actualType);
    }
    return items;
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = subExprs();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addListField("items", subExprs.items())
        .toString();
  }

  public static record BSubExprs(List<BExpr> items) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return items;
    }
  }
}
