package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvalTypeOfComponentException;
import org.smoothbuild.db.object.obj.exc.DecodeSelectIndexOutOfBoundsException;
import org.smoothbuild.db.object.obj.exc.DecodeSelectWrongEvalTypeException;
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

  public SelectH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
    checkArgument(merkleRoot.spec() instanceof SelectTypeH);
  }

  @Override
  public SelectTypeH spec() {
    return (SelectTypeH) super.spec();
  }

  public SelectData data() {
    ObjectH tuple = readTuple();
    if (tuple.type() instanceof TupleTypeH tupleEvaluationType) {
      IntH index = readIndex();
      int i = index.toJ().intValue();
      int size = tupleEvaluationType.items().size();
      if (i < 0 || size <= i) {
        throw new DecodeSelectIndexOutOfBoundsException(hash(), spec(), i, size);
      }
      TypeH fieldType = tupleEvaluationType.items().get(i);
      if (!Objects.equals(type(), fieldType)) {
        throw new DecodeSelectWrongEvalTypeException(hash(), spec(), fieldType);
      }
      return new SelectData(tuple, index);
    } else {
      throw new DecodeExprWrongEvalTypeOfComponentException(
          hash(), spec(), "tuple", TupleTypeH.class, tuple.type());
    }
  }

  public static record SelectData(ObjectH tuple, IntH index) {}

  private ObjectH readTuple() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), TUPLE_INDEX, DATA_SEQ_SIZE, ObjectH.class);
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
