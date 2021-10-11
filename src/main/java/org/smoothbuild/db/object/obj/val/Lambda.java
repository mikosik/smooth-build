package org.smoothbuild.db.object.obj.val;

import java.util.Objects;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.exc.DecodeExprWrongEvaluationSpecOfComponentException;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.expr.RecExpr;
import org.smoothbuild.db.object.spec.val.LambdaSpec;

public class Lambda extends Val {
  protected static final int DATA_SEQUENCE_SIZE = 2;
  protected static final int BODY_INDEX = 0;
  protected static final int DEFAULT_ARGUMENTS_INDEX = 1;
  protected static final String BODY_PATH = "data[" + BODY_INDEX + "]";
  protected static final String DEFAULT_ARGUMENTS_PATH = "data[" + DEFAULT_ARGUMENTS_INDEX + "]";

  public Lambda(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  @Override
  public LambdaSpec spec() {
    return (LambdaSpec) super.spec();
  }

  protected RecExpr defaultArguments() {
    RecExpr defaultArguments = readSequenceElementObj(
        DATA_PATH, dataHash(), DEFAULT_ARGUMENTS_INDEX, DATA_SEQUENCE_SIZE, RecExpr.class);
    if (!Objects.equals(spec().parameters(), defaultArguments.evaluationSpec())) {
      throw new DecodeExprWrongEvaluationSpecOfComponentException(hash(), spec(),
          DEFAULT_ARGUMENTS_PATH, spec().parameters(), defaultArguments.evaluationSpec());
    }
    return defaultArguments;
  }

  public LambdaData data() {
    Expr expr = readSequenceElementObj(
        DATA_PATH, dataHash(), BODY_INDEX, DATA_SEQUENCE_SIZE, Expr.class);
    if (!Objects.equals(expr.evaluationSpec(), spec().result())) {
      throw new DecodeExprWrongEvaluationSpecOfComponentException(
          hash(), spec(), BODY_PATH, expr.evaluationSpec(), spec().result());
    }
    return new LambdaData(expr, defaultArguments());
  }

  public record LambdaData(Expr body, RecExpr defaultArguments) {}

  @Override
  public String valueToString() {
    return "Lambda(" + spec().name() + ")";
  }
}
