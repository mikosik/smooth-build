package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationSpecOfComponentException;
import org.smoothbuild.db.object.obj.exc.DecodeSelectIndexOutOfBoundsException;
import org.smoothbuild.db.object.obj.exc.DecodeSelectWrongEvaluationSpecException;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.SelectSpec;
import org.smoothbuild.db.object.spec.val.StructSpec;

/**
 * This class is immutable.
 */
public class Select extends Expr {
  private static final int DATA_SEQUENCE_SIZE = 2;
  private static final int STRUCT_INDEX = 0;
  private static final int INDEX_INDEX = 1;

  public Select(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
    checkArgument(merkleRoot.spec() instanceof SelectSpec);
  }

  @Override
  public SelectSpec spec() {
    return (SelectSpec) super.spec();
  }

  public SelectData data() {
    Expr struct = readStruct();
    if (struct.evaluationSpec() instanceof StructSpec structEvaluationSpec) {
      Int index = readIndex();
      int i = index.jValue().intValue();
      int size = structEvaluationSpec.fields().size();
      if (i < 0 || size <= i) {
        throw new DecodeSelectIndexOutOfBoundsException(hash(), spec(), i, size);
      }
      ValSpec fieldSpec = structEvaluationSpec.fields().objectList().get(i);
      if (!Objects.equals(evaluationSpec(), fieldSpec)) {
        throw new DecodeSelectWrongEvaluationSpecException(hash(), spec(), fieldSpec);
      }
      return new SelectData(struct, index);
    } else {
      throw new DecodeExprWrongEvaluationSpecOfComponentException(
          hash(), spec(), "struct", StructSpec.class, struct.evaluationSpec());
    }
  }

  public static record SelectData(Expr struct, Int index) {}

  private Expr readStruct() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), STRUCT_INDEX, DATA_SEQUENCE_SIZE, Expr.class);
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
