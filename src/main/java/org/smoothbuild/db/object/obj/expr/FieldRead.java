package org.smoothbuild.db.object.obj.expr;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Int;

/**
 * This class is immutable.
 */
public class FieldRead extends Expr {
  private static final int DATA_SEQUENCE_SIZE = 2;
  private static final int REC_INDEX = 0;
  private static final int INDEX_INDEX = 1;

  public FieldRead(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  public Expr rec() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), REC_INDEX, DATA_SEQUENCE_SIZE, Expr.class);
  }

  public Int index() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), INDEX_INDEX, DATA_SEQUENCE_SIZE, Int.class);
  }

  @Override
  public String valueToString() {
    return "FieldRead(???)";
  }
}
