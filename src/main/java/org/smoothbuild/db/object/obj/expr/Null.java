package org.smoothbuild.db.object.obj.expr;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

/**
 * This class is immutable.
 */
public class Null extends Expr {
  public Null(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  @Override
  public String valueToString() {
    return "Null(???)";
  }
}
