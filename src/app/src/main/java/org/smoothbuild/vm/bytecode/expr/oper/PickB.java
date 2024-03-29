package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.vm.bytecode.expr.exc.DecodePickWrongEvaluationTypeExc;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.type.oper.PickCB;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.bytecode.type.value.IntTB;

import com.google.common.collect.ImmutableList;

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
  public ImmutableList<ExprB> dataSeq() {
    ExprB pickable = readPickable();
    if (pickable.evaluationT() instanceof ArrayTB arrayT) {
      var elemT = arrayT.elem();
      if (!evaluationT().equals(elemT)) {
        throw new DecodePickWrongEvaluationTypeExc(hash(), category(), elemT);
      }
      return list(pickable, readIndex());
    } else {
      throw new DecodeExprWrongNodeTypeExc(
          hash(), category(), "array", ArrayTB.class, pickable.evaluationT());
    }
  }

  private ExprB readPickable() {
    return readDataSeqElem(PICKABLE_IDX, DATA_SEQ_SIZE, ExprB.class);
  }

  private ExprB readIndex() {
    ExprB index = readDataSeqElem(IDX_IDX, DATA_SEQ_SIZE, ExprB.class);
    if (!(index.evaluationT() instanceof IntTB)) {
      throw new DecodeExprWrongNodeTypeExc(
          hash(), category(), ExprB.DATA_PATH, IDX_IDX, IntB.class, index.evaluationT());
    }
    return index;
  }
}
