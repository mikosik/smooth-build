package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodePickWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BPickKind;
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BIntType;

/**
 * This class is thread-safe.
 */
public class BPick extends BOper {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int PICKABLE_IDX = 0;
  private static final int IDX_IDX = 1;

  public BPick(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BPickKind);
  }

  @Override
  public BPickKind kind() {
    return (BPickKind) super.kind();
  }

  @Override
  public SubExprsB subExprs() throws BytecodeException {
    var pickable = readPickable();
    if (pickable.evaluationType() instanceof BArrayType arrayT) {
      var elementT = arrayT.elem();
      if (!evaluationType().equals(elementT)) {
        throw new DecodePickWrongEvaluationTypeException(hash(), kind(), elementT);
      }
      return new SubExprsB(readPickable(), readIndex());
    } else {
      throw new DecodeExprWrongNodeTypeException(
          hash(), kind(), "array", BArrayType.class, pickable.evaluationType());
    }
  }

  private BExpr readPickable() throws BytecodeException {
    return readElementFromDataAsInstanceChain(PICKABLE_IDX, DATA_SEQ_SIZE, BExpr.class);
  }

  private BExpr readIndex() throws BytecodeException {
    var index = readElementFromDataAsInstanceChain(IDX_IDX, DATA_SEQ_SIZE, BExpr.class);
    if (!(index.evaluationType() instanceof BIntType)) {
      throw new DecodeExprWrongNodeTypeException(
          hash(), kind(), BExpr.DATA_PATH, IDX_IDX, BInt.class, index.evaluationType());
    }
    return index;
  }

  public static record SubExprsB(BExpr pickable, BExpr index) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(pickable, index);
    }
  }
}
