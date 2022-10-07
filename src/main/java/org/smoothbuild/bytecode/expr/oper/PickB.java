package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.expr.exc.DecodePickWrongEvalTypeExc;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.type.oper.PickCB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.IntTB;

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

  public Data data() {
    ExprB pickable = readPickable();
    if (pickable.evalT() instanceof ArrayTB arrayT) {
      var elemT = arrayT.elem();
      if (!evalT().equals(elemT)) {
        throw new DecodePickWrongEvalTypeExc(hash(), category(), elemT);
      }
      return new Data(pickable, readIndex());
    } else {
      throw new DecodeExprWrongNodeTypeExc(
          hash(), category(), "array", ArrayTB.class, pickable.evalT());
    }
  }

  public record Data(ExprB pickable, ExprB index) {}

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
