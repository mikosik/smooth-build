package org.smoothbuild.bytecode.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.ExprB;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.cnst.IntB;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongNodeClassExc;
import org.smoothbuild.bytecode.obj.exc.DecodeSelectIndexOutOfBoundsExc;
import org.smoothbuild.bytecode.obj.exc.DecodeSelectWrongEvalTypeExc;
import org.smoothbuild.bytecode.type.cnst.TupleTB;
import org.smoothbuild.bytecode.type.expr.SelectCB;

/**
 * This class is thread-safe.
 */
public class SelectB extends ExprB {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int SELECTABLE_IDX = 0;
  private static final int IDX_IDX = 1;

  public SelectB(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.cat() instanceof SelectCB);
  }

  @Override
  public SelectCB cat() {
    return (SelectCB) super.cat();
  }

  public Data data() {
    ObjB selectable = readSelectable();
    if (selectable.type() instanceof TupleTB tupleEvalT) {
      IntB index = readIndex();
      int i = index.toJ().intValue();
      int size = tupleEvalT.items().size();
      if (i < 0 || size <= i) {
        throw new DecodeSelectIndexOutOfBoundsExc(hash(), cat(), i, size);
      }
      var fieldT = tupleEvalT.items().get(i);
      if (!typing().isAssignable(type(), fieldT)) {
        throw new DecodeSelectWrongEvalTypeExc(hash(), cat(), fieldT);
      }
      return new Data(selectable, index);
    } else {
      throw new DecodeObjWrongNodeClassExc(
          hash(), cat(), "tuple", TupleTB.class, selectable.type().getClass());
    }
  }

  public record Data(ObjB selectable, IntB index) {}

  private ObjB readSelectable() {
    return readSeqElemObj(DATA_PATH, dataHash(), SELECTABLE_IDX, DATA_SEQ_SIZE, ObjB.class);
  }

  private IntB readIndex() {
    return readSeqElemObj(DATA_PATH, dataHash(), IDX_IDX, DATA_SEQ_SIZE, IntB.class);
  }
}
