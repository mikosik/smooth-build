package org.smoothbuild.virtualmachine.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeClassException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeSelectIndexOutOfBoundsException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeSelectWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IntB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.SelectCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TupleTB;

/**
 * This class is thread-safe.
 */
public class SelectB extends OperB {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int SELECTABLE_IDX = 0;
  private static final int IDX_IDX = 1;

  public SelectB(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.category() instanceof SelectCB);
  }

  @Override
  public SelectCB category() {
    return (SelectCB) super.category();
  }

  @Override
  public SubExprsB subExprs() throws BytecodeException {
    var selectable = readSelectable();
    if (selectable.evaluationType() instanceof TupleTB tupleT) {
      var index = readIndex();
      int i = index.toJ().intValue();
      int size = tupleT.elements().size();
      if (i < 0 || size <= i) {
        throw new DecodeSelectIndexOutOfBoundsException(hash(), category(), i, size);
      }
      var fieldT = tupleT.elements().get(i);
      if (!evaluationType().equals(fieldT)) {
        throw new DecodeSelectWrongEvaluationTypeException(hash(), category(), fieldT);
      }
      return new SubExprsB(selectable, index);
    } else {
      throw new DecodeExprWrongNodeClassException(
          hash(),
          category(),
          "tuple",
          TupleTB.class,
          selectable.evaluationType().getClass());
    }
  }

  private ExprB readSelectable() throws BytecodeException {
    return readElementFromDataAsInstanceChain(SELECTABLE_IDX, DATA_SEQ_SIZE, ExprB.class);
  }

  private IntB readIndex() throws BytecodeException {
    return readElementFromDataAsInstanceChain(IDX_IDX, DATA_SEQ_SIZE, IntB.class);
  }

  public static record SubExprsB(ExprB selectable, IntB index) implements ExprsB {
    @Override
    public List<ExprB> toList() {
      return list(selectable, index);
    }
  }
}
