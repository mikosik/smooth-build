package org.smoothbuild.db.object.obj.val;

import java.util.Objects;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.exc.DecodeExprWrongEvaluationSpecOfComponentException;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

import com.google.common.collect.ImmutableList;

public class DefinedLambda extends Lambda {
  public DefinedLambda(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  public DefinedLambdaData data() {
    Expr expr = readSequenceElementObj(
        DATA_PATH, dataHash(), BODY_INDEX, DATA_SEQUENCE_SIZE, Expr.class);
    if (!Objects.equals(expr.evaluationSpec(), spec().result())) {
      throw new DecodeExprWrongEvaluationSpecOfComponentException(
          hash(), spec(), BODY_PATH, spec().result(), expr.evaluationSpec());
    }
    return new DefinedLambdaData(expr, defaultArguments());
  }

  public record DefinedLambdaData(Expr body, ImmutableList<Expr> defaultArguments) {}

  @Override
  public String valueToString() {
    return "DefinedLambda(" + spec().name() + ")";
  }
}
