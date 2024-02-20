package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeClassException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeSelectIndexOutOfBoundsException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeSelectWrongEvaluationTypeException;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.type.oper.SelectCB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;

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
    if (selectable.evaluationT() instanceof TupleTB tupleT) {
      var index = readIndex();
      int i = index.toJ().intValue();
      int size = tupleT.elements().size();
      if (i < 0 || size <= i) {
        throw new DecodeSelectIndexOutOfBoundsException(hash(), category(), i, size);
      }
      var fieldT = tupleT.elements().get(i);
      if (!evaluationT().equals(fieldT)) {
        throw new DecodeSelectWrongEvaluationTypeException(hash(), category(), fieldT);
      }
      return new SubExprsB(selectable, index);
    } else {
      throw new DecodeExprWrongNodeClassException(
          hash(), category(), "tuple", TupleTB.class, selectable.evaluationT().getClass());
    }
  }

  private ExprB readSelectable() throws BytecodeException {
    return readDataSeqElem(SELECTABLE_IDX, DATA_SEQ_SIZE, ExprB.class);
  }

  private IntB readIndex() throws BytecodeException {
    return readDataSeqElem(IDX_IDX, DATA_SEQ_SIZE, IntB.class);
  }

  public static record SubExprsB(ExprB selectable, IntB index) implements ExprsB {
    @Override
    public List<ExprB> toList() {
      return list(selectable, index);
    }
  }
}
