package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BMapKind;

/**
 * 'Map' function.
 * This class is thread-safe.
 */
public final class BMap extends BOperation {
  private static final int DATA_SEQ_SIZE = 2;
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
    var hashes = readDataAsHashChain(DATA_SEQ_SIZE);
    var array = readMemberFromHashChain(hashes, ARRAY_INDEX);
    var arrayEvaluationType = array.evaluationType();
    if (!(arrayEvaluationType instanceof BArrayType arrayType)) {
      throw new MemberHasWrongEvaluationTypeException(
          hash(), kind(), "array", BArrayType.class.getSimpleName(), arrayEvaluationType);
    }
    var mapper = readMemberFromHashChain(hashes, MAPPER_INDEX);
    var mapperEvaluationType = mapper.evaluationType();
    var expectedMapperEvaluationType =
        kindDb().lambda(list(arrayType.element()), evaluationType().element());
    if (!mapperEvaluationType.equals(expectedMapperEvaluationType)) {
      throw new MemberHasWrongEvaluationTypeException(
          hash(), kind(), "mapper", expectedMapperEvaluationType, mapperEvaluationType);
    }
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
