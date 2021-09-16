package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

public class DefinedLambda extends Lambda {
  public DefinedLambda(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  public Expr body() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), BODY_INDEX, DATA_SEQUENCE_SIZE, Expr.class);
  }

  @Override
  public String valueToString() {
    return "DefinedLambda(" + spec().name() + ")";
  }
}
