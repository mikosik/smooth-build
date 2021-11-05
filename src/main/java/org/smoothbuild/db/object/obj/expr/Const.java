package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeException;
import org.smoothbuild.db.object.type.expr.ConstTypeO;

/**
 * This class is immutable.
 */
public class Const extends Expr {
  public Const(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.type() instanceof ConstTypeO);
  }

  @Override
  public ConstTypeO type() {
    return (ConstTypeO) super.type();
  }

  public Val value() {
    Val val = readObj(DATA_PATH, dataHash(), Val.class);
    if (!Objects.equals(evaluationType(), val.type())) {
      throw new UnexpectedObjNodeException(
          hash(), type(), DATA_PATH, evaluationType(), val.type());
    }
    return val;
  }

  @Override
  public String valueToString() {
    return "Const(" + value().valueToString() + ")";
  }
}
