package org.smoothbuild.db.object.obj.val;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.EvaluableH;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationTypeOfComponentException;
import org.smoothbuild.db.object.type.val.FunctionTypeH;

public class FunctionH extends ValueH implements EvaluableH {
  public FunctionH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
  }

  @Override
  public FunctionTypeH type() {
    return (FunctionTypeH) super.type();
  }

  public ExprH body() {
    ExprH expr = readObj(DATA_PATH, dataHash(), ExprH.class);
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
