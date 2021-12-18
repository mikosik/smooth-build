package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.ExprB;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.db.object.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.db.object.obj.exc.DecodeSelectIndexOutOfBoundsExc;
import org.smoothbuild.db.object.obj.exc.DecodeSelectWrongEvalTypeExc;
import org.smoothbuild.db.object.obj.val.IntB;
import org.smoothbuild.db.object.type.expr.SelectCB;
import org.smoothbuild.db.object.type.val.TupleTB;

/**
 * This class is thread-safe.
 */
public class SelectB extends ExprB {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int SELECTABLE_INDEX = 0;
  private static final int INDEX_INDEX = 1;

  public SelectB(MerkleRoot merkleRoot, ByteDb byteDb) {
    super(merkleRoot, byteDb);
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
      if (!Objects.equals(type(), fieldT)) {
        throw new DecodeSelectWrongEvalTypeExc(hash(), cat(), fieldT);
      }
      return new Data(selectable, index);
    } else {
      throw new DecodeObjWrongNodeTypeExc(
          hash(), cat(), "tuple", TupleTB.class, selectable.type().getClass());
    }
  }

  public record Data(ObjB selectable, ObjB index) {}

  private ObjB readSelectable() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), SELECTABLE_INDEX, DATA_SEQ_SIZE, ObjB.class);
  }

  private IntB readIndex() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), INDEX_INDEX, DATA_SEQ_SIZE, IntB.class);
  }
}
