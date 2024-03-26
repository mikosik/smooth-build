package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeClassException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeSelectIndexOutOfBoundsException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeSelectWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BSelectKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;

/**
 * This class is thread-safe.
 */
public final class BSelect extends BOperation {
  public BSelect(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BSelectKind);
  }

  @Override
  public BSelectKind kind() {
    return (BSelectKind) super.kind();
  }

  @Override
  public SubExprsB subExprs() throws BytecodeException {
    var hashes = readDataAsHashChain(2);
    var selectable = readMemberFromHashChain(hashes, 0);
    var index = readAndCastMemberFromHashChain(hashes, 1, "index", BInt.class);
    if (!(selectable.evaluationType() instanceof BTupleType tupleType)) {
      throw new DecodeExprWrongNodeClassException(
          hash(), kind(), "tuple", BTupleType.class, selectable.evaluationType().getClass());
    }
    int i = index.toJavaBigInteger().intValue();
    int size = tupleType.elements().size();
    if (i < 0 || size <= i) {
      throw new DecodeSelectIndexOutOfBoundsException(hash(), kind(), i, size);
    }
    var fieldType = tupleType.elements().get(i);
    if (!evaluationType().equals(fieldType)) {
      throw new DecodeSelectWrongEvaluationTypeException(hash(), kind(), fieldType);
    }
    return new SubExprsB(selectable, index);
  }

  public static record SubExprsB(BExpr selectable, BInt index) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(selectable, index);
    }
  }
}
