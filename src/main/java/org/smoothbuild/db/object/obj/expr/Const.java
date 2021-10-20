package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeException;
import org.smoothbuild.db.object.spec.expr.ConstSpec;

/**
 * This class is immutable.
 */
public class Const extends Expr {
  public Const(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
    checkArgument(merkleRoot.spec() instanceof ConstSpec);
  }

  @Override
  public ConstSpec spec() {
    return (ConstSpec) super.spec();
  }

  public Val value() {
    Val val = readObj(DATA_PATH, dataHash(), Val.class);
    if (!Objects.equals(evaluationSpec(), val.spec())) {
      throw new UnexpectedObjNodeException(
          hash(), spec(), DATA_PATH, evaluationSpec(), val.spec());
    }
    return val;
  }

  @Override
  public String valueToString() {
    return "Const(" + value().valueToString() + ")";
  }
}
