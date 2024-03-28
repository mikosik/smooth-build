package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongMemberEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BMapKind;

/**
 * 'Map' function.
 * This class is thread-safe.
 */
public final class BMap extends BOperation {
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
  public SubExprsB subExprs() throws BytecodeException {
    var hashes = readDataAsHashChain(2);
    var array = readMemberFromHashChain(hashes, 0);
    var arrayEvaluationType = array.evaluationType();
    if (!(arrayEvaluationType instanceof BArrayType arrayType)) {
      throw new DecodeExprWrongMemberEvaluationTypeException(
          hash(), kind(), "array", BArrayType.class.getSimpleName(), arrayEvaluationType);
    }
    var mapper = readMemberFromHashChain(hashes, 1);
    var mapperEvaluationType = mapper.evaluationType();
    var expectedMapperEvaluationType =
        kindDb().lambda(list(arrayType.elem()), evaluationType().elem());
    if (!(mapperEvaluationType.equals(expectedMapperEvaluationType))) {
      throw new DecodeExprWrongMemberEvaluationTypeException(
          hash(), kind(), "mapper", expectedMapperEvaluationType, mapperEvaluationType);
    }
    return new SubExprsB(array, mapper);
  }

  public static record SubExprsB(BExpr array, BExpr mapper) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(array, mapper);
    }
  }
}
