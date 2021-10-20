package org.smoothbuild.db.object.obj.val;

import java.util.Objects;

import org.smoothbuild.db.object.exc.DecodeExprWrongEvaluationSpecOfComponentException;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.spec.val.LambdaSpec;

public class Lambda extends Val {
  public Lambda(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  @Override
  public LambdaSpec spec() {
    return (LambdaSpec) super.spec();
  }

  public Expr body() {
    Expr expr = readObj(DATA_PATH, dataHash(), Expr.class);
    if (!Objects.equals(expr.evaluationSpec(), spec().result())) {
      throw new DecodeExprWrongEvaluationSpecOfComponentException(
          hash(), spec(), DATA_PATH, expr.evaluationSpec(), spec().result());
    }
    return expr;
  }

  @Override
  public String valueToString() {
    return "Lambda(" + spec().name() + ")";
  }
}
