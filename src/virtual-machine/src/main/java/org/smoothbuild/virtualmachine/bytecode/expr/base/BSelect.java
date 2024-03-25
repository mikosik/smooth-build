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
import org.smoothbuild.virtualmachine.bytecode.type.oper.BSelectKind;
import org.smoothbuild.virtualmachine.bytecode.type.value.BTupleType;

/**
 * This class is thread-safe.
 */
public class BSelect extends BOper {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int SELECTABLE_IDX = 0;
  private static final int IDX_IDX = 1;

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
    var selectable = readSelectable();
    if (selectable.evaluationType() instanceof BTupleType tupleT) {
      var index = readIndex();
      int i = index.toJavaBigInteger().intValue();
      int size = tupleT.elements().size();
      if (i < 0 || size <= i) {
        throw new DecodeSelectIndexOutOfBoundsException(hash(), kind(), i, size);
      }
      var fieldT = tupleT.elements().get(i);
      if (!evaluationType().equals(fieldT)) {
        throw new DecodeSelectWrongEvaluationTypeException(hash(), kind(), fieldT);
      }
      return new SubExprsB(selectable, index);
    } else {
      throw new DecodeExprWrongNodeClassException(
          hash(), kind(), "tuple", BTupleType.class, selectable.evaluationType().getClass());
    }
  }

  private BExpr readSelectable() throws BytecodeException {
    return readElementFromDataAsInstanceChain(SELECTABLE_IDX, DATA_SEQ_SIZE, BExpr.class);
  }

  private BInt readIndex() throws BytecodeException {
    return readElementFromDataAsInstanceChain(IDX_IDX, DATA_SEQ_SIZE, BInt.class);
  }

  public static record SubExprsB(BExpr selectable, BInt index) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(selectable, index);
    }
  }
}
