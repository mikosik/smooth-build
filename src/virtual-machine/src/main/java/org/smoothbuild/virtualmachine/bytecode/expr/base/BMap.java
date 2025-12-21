package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
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

  private static final List<String> MEMBER_NAMES = list("array", "mapper");

  private final Function0<BSubExprs, BytecodeException> subExprs =
      Function0.memoizer(this::fetchAndValidateSubExprs);

  public BMap(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb, 2);
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

  private BSubExprs fetchAndValidateSubExprs() throws BytecodeException {
    var members = members(MEMBER_NAMES);
    var array = members.get(ARRAY_INDEX).asExpr(BArrayType.class);
    var arrayType = (BArrayType) array.evaluationType();
    var expectedMapperEvaluationType =
        kindDb().lambda(list(arrayType.element()), evaluationType().element());
    var mapper = members.get(MAPPER_INDEX).asExpr(expectedMapperEvaluationType);
    return new BSubExprs(array, mapper);
  }

  public BExpr array() throws BytecodeException {
    return subExprs.apply().array();
  }

  public BExpr mapper() throws BytecodeException {
    return subExprs.apply().mapper();
  }

  @Override
  public String exprToString() throws BytecodeException {
    var subExprs = this.subExprs.apply();
    return new ToStringBuilder(getClass().getSimpleName())
        .addField("hash", hash())
        .addField("evaluationType", evaluationType())
        .addField("array", subExprs.array())
        .addField("mapper", subExprs.mapper())
        .toString();
  }

  private record BSubExprs(BExpr array, BExpr mapper) {}
}
