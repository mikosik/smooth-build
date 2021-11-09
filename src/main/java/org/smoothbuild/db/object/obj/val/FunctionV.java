package org.smoothbuild.db.object.obj.val;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationTypeOfComponentException;
import org.smoothbuild.db.object.type.val.FunctionTypeO;

public class FunctionV extends Val {
  public FunctionV(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  @Override
  public FunctionTypeO type() {
    return (FunctionTypeO) super.type();
  }

  public Expr body() {
    Expr expr = readObj(DATA_PATH, dataHash(), Expr.class);
    if (!Objects.equals(expr.evaluationType(), this.type().result())) {
      throw new DecodeExprWrongEvaluationTypeOfComponentException(
          hash(), this.type(), DATA_PATH, expr.evaluationType(), this.type().result());
    }
    return expr;
  }

  @Override
  public String valueToString() {
    return "Lambda(" + this.type().name() + ")";
  }
}
