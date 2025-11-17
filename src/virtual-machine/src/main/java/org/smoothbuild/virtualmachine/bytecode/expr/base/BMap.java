package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BMapKind;

/**
 * 'Map' function.
 * This class is thread-safe.
 */
public final class BMap extends BOperation {
  private static final int ARRAY_INDEX = 0;
  private static final int MAPPER_INDEX = 1;

  public BMap(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BMapKind);
  }

  @Override
  public BMapKind kind() {
    return (BMapKind) super.kind();
  }

  @Override
  public BArrayType evaluationType() {
    return kind().evaluationType();
  }

  @Override
  public BSubExprs subExprs() throws BytecodeException {
    var members = members("array", "mapper");
    var array = members.get(ARRAY_INDEX).asExpr(BArrayType.class);
    var arrayType = (BArrayType) array.evaluationType();
    var expectedMapperEvaluationType =
        kindDb().lambda(list(arrayType.element()), evaluationType().element());
    var mapper = members.get(MAPPER_INDEX).asExpr(expectedMapperEvaluationType);
    return new BSubExprs(array, mapper);
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = subExprs();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("array", subExprs.array())
        .addField("mapper", subExprs.mapper())
        .toString();
  }

  public static record BSubExprs(BExpr array, BExpr mapper) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(array, mapper);
    }
  }
}
