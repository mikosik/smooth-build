package org.smoothbuild.db.object.obj.expr;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class EArray extends Expr {
  public EArray(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  public ImmutableList<Expr> elements() {
    return readSequenceObjs(DATA_PATH, dataHash(), Expr.class);
  }

  @Override
  public String valueToString() {
    return "EArray(???)";
  }
}
