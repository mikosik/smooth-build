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
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.expr.SelectTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;

/**
 * This class is immutable.
 */
public class SelectH extends ExprH {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int TUPLE_INDEX = 0;
  private static final int INDEX_INDEX = 1;

  public SelectH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.spec() instanceof SelectTypeH);
  }

  @Override
  public SelectTypeH spec() {
    return (SelectTypeH) super.spec();
  }

  public SelectData data() {
    ObjH tuple = readTuple();
    if (tuple.type() instanceof TupleTypeH tupleEvaluationType) {
      IntH index = readIndex();
      int i = index.toJ().intValue();
      int size = tupleEvaluationType.items().size();
      if (i < 0 || size <= i) {
        throw new DecodeSelectIndexOutOfBoundsExc(hash(), spec(), i, size);
      }
      TypeH fieldType = tupleEvaluationType.items().get(i);
      if (!Objects.equals(type(), fieldType)) {
        throw new DecodeSelectWrongEvalTypeExc(hash(), spec(), fieldType);
      }
      return new SelectData(tuple, index);
    } else {
      throw new DecodeExprWrongEvalTypeOfCompExc(
          hash(), spec(), "tuple", TupleTypeH.class, tuple.type());
    }
  }

  public static record SelectData(ObjH tuple, IntH index) {}

  private ObjH readTuple() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), TUPLE_INDEX, DATA_SEQ_SIZE, ObjH.class);
  }

  private IntH readIndex() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), INDEX_INDEX, DATA_SEQ_SIZE, IntH.class);
  }

  @Override
  public String valToString() {
    return "Select(???)";
  }
}
