package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeException;
import org.smoothbuild.db.object.type.expr.ConstTypeH;

/**
 * This class is immutable.
 */
public class ConstH extends ExprH {
  public ConstH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
    checkArgument(merkleRoot.type() instanceof ConstTypeH);
  }

  @Override
  public ConstTypeH type() {
    return (ConstTypeH) super.type();
  }

  public ValueH value() {
    ValueH val = readObj(DATA_PATH, dataHash(), ValueH.class);
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
