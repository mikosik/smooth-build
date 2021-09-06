package org.smoothbuild.db.object.obj.expr;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Call extends Expr {
  private static final int DATA_SEQUENCE_SIZE = 2;
  private static final int FUNCTION_INDEX = 0;
  private static final int ARGUMENTS_INDEX = 1;
  private static final String ARGUMENTS_PATH = "data[" + ARGUMENTS_INDEX + "]";

  public Call(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  public Expr function() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), FUNCTION_INDEX, DATA_SEQUENCE_SIZE, Expr.class);
  }

  public ImmutableList<Expr> arguments() {
    var hash = readSequenceElementHash(DATA_PATH, dataHash(), ARGUMENTS_INDEX, DATA_SEQUENCE_SIZE);
    return readSequenceObjs(ARGUMENTS_PATH, hash, Expr.class);
  }

  @Override
  public String valueToString() {
    return "Call(???)";
  }
}
