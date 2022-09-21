package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeClassExc;
import org.smoothbuild.bytecode.expr.exc.DecodeSelectIndexOutOfBoundsExc;
import org.smoothbuild.bytecode.expr.exc.DecodeSelectWrongEvalTypeExc;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.type.oper.SelectCB;
import org.smoothbuild.bytecode.type.val.TupleTB;

/**
 * This class is thread-safe.
 */
public class SelectB extends OperB {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int SELECTABLE_IDX = 0;
  private static final int IDX_IDX = 1;

  public SelectB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof SelectCB);
  }

  @Override
  public SelectCB category() {
    return (SelectCB) super.category();
  }

  public Data data() {
    ExprB selectable = readSelectable();
    if (selectable.type() instanceof TupleTB tupleEvalT) {
      IntB index = readIndex();
      int i = index.toJ().intValue();
      int size = tupleEvalT.items().size();
      if (i < 0 || size <= i) {
        throw new DecodeSelectIndexOutOfBoundsExc(hash(), this.category(), i, size);
      }
      var fieldT = tupleEvalT.items().get(i);
      if (!type().equals(fieldT)) {
        throw new DecodeSelectWrongEvalTypeExc(hash(), this.category(), fieldT);
      }
      return new Data(selectable, index);
    } else {
      throw new DecodeExprWrongNodeClassExc(
          hash(), this.category(), "tuple", TupleTB.class, selectable.type().getClass());
    }
  }

  public record Data(ExprB selectable, IntB index) {}

  private ExprB readSelectable() {
    return readSeqElemExpr(DATA_PATH, dataHash(), SELECTABLE_IDX, DATA_SEQ_SIZE, ExprB.class);
  }

  private IntB readIndex() {
    return readSeqElemExpr(DATA_PATH, dataHash(), IDX_IDX, DATA_SEQ_SIZE, IntB.class);
  }
}
