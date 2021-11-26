package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationTypeOfComponentException;
import org.smoothbuild.db.object.obj.exc.DecodeSelectIndexOutOfBoundsException;
import org.smoothbuild.db.object.obj.exc.DecodeSelectWrongEvaluationTypeException;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.expr.SelectTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;

/**
 * This class is immutable.
 */
public class SelectH extends ExprH {
  private static final int DATA_SEQUENCE_SIZE = 2;
  private static final int TUPLE_INDEX = 0;
  private static final int INDEX_INDEX = 1;

  public SelectH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
    checkArgument(merkleRoot.type() instanceof SelectTypeH);
  }

  @Override
  public SelectTypeH type() {
    return (SelectTypeH) super.type();
  }

  public SelectData data() {
    ObjectH tuple = readTuple();
    if (tuple.evaluationType() instanceof TupleTypeH tupleEvaluationType) {
      IntH index = readIndex();
      int i = index.jValue().intValue();
      int size = tupleEvaluationType.items().size();
      if (i < 0 || size <= i) {
        throw new DecodeSelectIndexOutOfBoundsException(hash(), type(), i, size);
      }
      TypeHV fieldType = tupleEvaluationType.items().get(i);
      if (!Objects.equals(evaluationType(), fieldType)) {
        throw new DecodeSelectWrongEvaluationTypeException(hash(), type(), fieldType);
      }
      return new SelectData(tuple, index);
    } else {
      throw new DecodeExprWrongEvaluationTypeOfComponentException(
          hash(), type(), "tuple", TupleTypeH.class, tuple.evaluationType());
    }
  }

  public static record SelectData(ObjectH tuple, IntH index) {}

  private ObjectH readTuple() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), TUPLE_INDEX, DATA_SEQUENCE_SIZE, ObjectH.class);
  }

  private IntH readIndex() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), INDEX_INDEX, DATA_SEQUENCE_SIZE, IntH.class);
  }

  @Override
  public String valueToString() {
    return "Select(???)";
  }
}
