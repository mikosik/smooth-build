package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodePickWrongEvaluationTypeException;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.type.oper.PickCB;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.bytecode.type.value.IntTB;

/**
 * This class is thread-safe.
 */
public class PickB extends OperB {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int PICKABLE_IDX = 0;
  private static final int IDX_IDX = 1;

  public PickB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof PickCB);
  }

  @Override
  public PickCB category() {
    return (PickCB) super.category();
  }

  @Override
  public SubExprsB subExprs() throws BytecodeException {
    var pickable = readPickable();
    if (pickable.evaluationT() instanceof ArrayTB arrayT) {
      var elementT = arrayT.elem();
      if (!evaluationT().equals(elementT)) {
        throw new DecodePickWrongEvaluationTypeException(hash(), category(), elementT);
      }
      return new SubExprsB(readPickable(), readIndex());
    } else {
      throw new DecodeExprWrongNodeTypeException(
          hash(), category(), "array", ArrayTB.class, pickable.evaluationT());
    }
  }

  private ExprB readPickable() throws BytecodeException {
    return readDataSeqElem(PICKABLE_IDX, DATA_SEQ_SIZE, ExprB.class);
  }

  private ExprB readIndex() throws BytecodeException {
    var index = readDataSeqElem(IDX_IDX, DATA_SEQ_SIZE, ExprB.class);
    if (!(index.evaluationT() instanceof IntTB)) {
      throw new DecodeExprWrongNodeTypeException(
          hash(), category(), ExprB.DATA_PATH, IDX_IDX, IntB.class, index.evaluationT());
    }
    return index;
  }

  public static record SubExprsB(ExprB pickable, ExprB index) implements ExprsB {
    @Override
    public List<ExprB> toList() {
      return list(pickable, index);
    }
  }
}
