package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationSpecOfComponentException;
import org.smoothbuild.db.object.spec.expr.CallSpec;
import org.smoothbuild.db.object.spec.val.LambdaSpec;

/**
 * This class is immutable.
 */
public class Call extends Expr {
  private static final int DATA_SEQUENCE_SIZE = 2;
  private static final int FUNCTION_INDEX = 0;
  private static final int ARGUMENTS_INDEX = 1;

  public Call(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
    checkArgument(merkleRoot.spec() instanceof CallSpec);
  }

  @Override
  public CallSpec spec() {
    return (CallSpec) super.spec();
  }

  public CallData data() {
    Expr function = readFunction();
    RecExpr arguments = readArguments();
    validate(function, arguments);
    return new CallData(function, arguments);
  }

  public record CallData(Expr function, RecExpr arguments) {}

  private void validate(Expr function, RecExpr arguments) {
    if (function.evaluationSpec() instanceof LambdaSpec lambdaSpec) {
      if (!Objects.equals(evaluationSpec(), lambdaSpec.result())) {
        throw new DecodeExprWrongEvaluationSpecOfComponentException(
            hash(), spec(), "function.result", evaluationSpec(), lambdaSpec.result());
      }
      if (!Objects.equals(lambdaSpec.parametersRec(), arguments.spec().evaluationSpec())) {
        throw new DecodeExprWrongEvaluationSpecOfComponentException(hash(), spec(), "arguments",
            lambdaSpec.parametersRec(), arguments.spec().evaluationSpec());
      }
    } else {
      throw new DecodeExprWrongEvaluationSpecOfComponentException(
          hash(), spec(), "function", LambdaSpec.class, function.evaluationSpec());
    }
  }

  private Expr readFunction() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), FUNCTION_INDEX, DATA_SEQUENCE_SIZE, Expr.class);
  }

  private RecExpr readArguments() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), ARGUMENTS_INDEX, DATA_SEQUENCE_SIZE, RecExpr.class);
  }

  @Override
  public String valueToString() {
    return "Call(???)";
  }
}
