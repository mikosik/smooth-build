package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvalTypeOfCompExc;
import org.smoothbuild.db.object.obj.exc.DecodeSelectIndexOutOfBoundsExc;
import org.smoothbuild.db.object.obj.exc.DecodeSelectWrongEvalTypeExc;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.type.expr.SelectCH;
import org.smoothbuild.db.object.type.val.TupleTH;

/**
 * This class is thread-safe.
 */
public class SelectH extends ExprH {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int SELECTABLE_INDEX = 0;
  private static final int INDEX_INDEX = 1;

  public SelectH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.cat() instanceof SelectCH);
  }

  @Override
  public SelectCH cat() {
    return (SelectCH) super.cat();
  }

  public SelectData data() {
    ObjH selectable = readSelectable();
    if (selectable.type() instanceof TupleTH tupleEvalT) {
      IntH index = readIndex();
      int i = index.toJ().intValue();
      int size = tupleEvalT.items().size();
      if (i < 0 || size <= i) {
        throw new DecodeSelectIndexOutOfBoundsExc(hash(), cat(), i, size);
      }
      var fieldT = tupleEvalT.items().get(i);
      if (!Objects.equals(type(), fieldT)) {
        throw new DecodeSelectWrongEvalTypeExc(hash(), cat(), fieldT);
      }
      return new SelectData(selectable, index);
    } else {
      throw new DecodeExprWrongEvalTypeOfCompExc(
          hash(), cat(), "tuple", TupleTH.class, selectable.type());
    }
  }

  public record SelectData(ObjH selectable, ObjH index) {}

  private ObjH readSelectable() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), SELECTABLE_INDEX, DATA_SEQ_SIZE, ObjH.class);
  }

  private IntH readIndex() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), INDEX_INDEX, DATA_SEQ_SIZE, IntH.class);
  }
}
