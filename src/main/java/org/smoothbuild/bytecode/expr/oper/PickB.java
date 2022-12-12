package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.expr.exc.DecodePickWrongEvalTypeExc;
import org.smoothbuild.bytecode.expr.value.IntB;
import org.smoothbuild.bytecode.type.oper.PickCB;
import org.smoothbuild.bytecode.type.value.ArrayTB;
import org.smoothbuild.bytecode.type.value.IntTB;

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
    if (pickable.evalT() instanceof ArrayTB arrayT) {
      var elemT = arrayT.elem();
      if (!evalT().equals(elemT)) {
        throw new DecodePickWrongEvalTypeExc(hash(), category(), elemT);
      }
      return list(pickable, readIndex());
    } else {
      throw new DecodeExprWrongNodeTypeExc(
          hash(), category(), "array", ArrayTB.class, pickable.evalT());
    }
  }

  private ExprB readPickable() {
    return readDataSeqElem(PICKABLE_IDX, DATA_SEQ_SIZE, ExprB.class);
  }

  private ExprB readIndex() {
    ExprB index = readDataSeqElem(IDX_IDX, DATA_SEQ_SIZE, ExprB.class);
    if (!(index.evalT() instanceof IntTB)) {
      throw new DecodeExprWrongNodeTypeExc(
          hash(), category(), DATA_PATH, IDX_IDX, IntB.class, index.evalT());
    }
    return index;
  }
}
