package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeClassExc;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeSelectIndexOutOfBoundsExc;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeSelectWrongEvalTypeExc;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.type.oper.SelectCB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;

import com.google.common.collect.ImmutableList;

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

  @Override
  public ImmutableList<ExprB> dataSeq() {
    ExprB selectable = readSelectable();
    if (selectable.evalT() instanceof TupleTB tupleT) {
      IntB index = readIndex();
      int i = index.toJ().intValue();
      int size = tupleT.items().size();
      if (i < 0 || size <= i) {
        throw new DecodeSelectIndexOutOfBoundsExc(hash(), category(), i, size);
      }
      var fieldT = tupleT.items().get(i);
      if (!evalT().equals(fieldT)) {
        throw new DecodeSelectWrongEvalTypeExc(hash(), category(), fieldT);
      }
      return list(selectable, index);
    } else {
      throw new DecodeExprWrongNodeClassExc(
          hash(), category(), "tuple", TupleTB.class, selectable.evalT().getClass());
    }
  }

  private ExprB readSelectable() {
    return readDataSeqElem(SELECTABLE_IDX, DATA_SEQ_SIZE, ExprB.class);
  }

  private IntB readIndex() {
    return readDataSeqElem(IDX_IDX, DATA_SEQ_SIZE, IntB.class);
  }
}
