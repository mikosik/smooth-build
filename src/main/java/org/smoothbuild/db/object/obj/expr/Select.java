package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationTypeOfComponentException;
import org.smoothbuild.db.object.obj.exc.DecodeSelectIndexOutOfBoundsException;
import org.smoothbuild.db.object.obj.exc.DecodeSelectWrongEvaluationTypeException;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.db.object.type.expr.SelectOType;
import org.smoothbuild.db.object.type.val.TupleTypeO;

/**
 * This class is immutable.
 */
public class Select extends Expr {
  private static final int DATA_SEQUENCE_SIZE = 2;
  private static final int TUPLE_INDEX = 0;
  private static final int INDEX_INDEX = 1;

  public Select(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.type() instanceof SelectOType);
  }

  @Override
  public SelectOType type() {
    return (SelectOType) super.type();
  }

  public SelectData data() {
    Expr tuple = readTuple();
    if (tuple.evaluationType() instanceof TupleTypeO tupleEvaluationType) {
      Int index = readIndex();
      int i = index.jValue().intValue();
      int size = tupleEvaluationType.items().size();
      if (i < 0 || size <= i) {
        throw new DecodeSelectIndexOutOfBoundsException(hash(), type(), i, size);
      }
      TypeV fieldType = tupleEvaluationType.items().get(i);
      if (!Objects.equals(evaluationType(), fieldType)) {
        throw new DecodeSelectWrongEvaluationTypeException(hash(), type(), fieldType);
      }
      return new SelectData(tuple, index);
    } else {
      throw new DecodeExprWrongEvaluationTypeOfComponentException(
          hash(), type(), "tuple", TupleTypeO.class, tuple.evaluationType());
    }
  }

  public static record SelectData(Expr tuple, Int index) {}

  private Expr readTuple() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), TUPLE_INDEX, DATA_SEQUENCE_SIZE, Expr.class);
  }

  private Int readIndex() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), INDEX_INDEX, DATA_SEQUENCE_SIZE, Int.class);
  }

  @Override
  public String valueToString() {
    return "Select(???)";
  }
}
