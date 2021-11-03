package org.smoothbuild.db.object.obj.val;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationTypeOfComponentException;
import org.smoothbuild.db.object.type.val.LambdaOType;

public class Lambda extends Val {
  public Lambda(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  @Override
  public LambdaOType type() {
    return (LambdaOType) super.type();
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
