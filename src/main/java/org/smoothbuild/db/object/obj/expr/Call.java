package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.Lists.allMatch;
import static org.smoothbuild.util.Lists.map;

import java.util.Objects;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.exc.DecodeCallWrongArgumentsSizeException;
import org.smoothbuild.db.object.exc.DecodeExprWrongEvaluationSpecOfComponentException;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.CallSpec;
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Call extends Expr {
  private static final int DATA_SEQUENCE_SIZE = 2;
  private static final int FUNCTION_INDEX = 0;
  private static final int ARGUMENTS_INDEX = 1;
  private static final String ARGUMENTS_PATH = "data[" + ARGUMENTS_INDEX + "]";

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
    ImmutableList<Expr> arguments = readArguments();
    validate(function, arguments);
    return new CallData(function, arguments);
  }

  public record CallData(Expr function, ImmutableList<Expr> arguments) {}

  private void validate(Expr function, ImmutableList<Expr> arguments) {
    ValSpec functionEvaluationSpec = function.evaluationSpec();
    if (functionEvaluationSpec instanceof LambdaSpec lambdaSpec) {
      if (!Objects.equals(evaluationSpec(), lambdaSpec.result())) {
        throw new DecodeExprWrongEvaluationSpecOfComponentException(
            hash(), spec(), "function.result", evaluationSpec(), lambdaSpec.result());
      }
      var parameters = lambdaSpec.parameters().items();
      int parametersCount = parameters.size();
      int argumentsCount = arguments.size();
      if (argumentsCount != parametersCount) {
        throw new DecodeCallWrongArgumentsSizeException(
            hash(), spec(), parametersCount, argumentsCount);
      }
      var argumentSpecs = map(arguments, Expr::evaluationSpec);
      if (!allMatch(parameters, argumentSpecs, Spec::equals)) {
        // TODO replace workaround with RecSpec once arguments are RecExpr
        RecSpec workaround = lambdaSpec.parameters();
        throw new DecodeExprWrongEvaluationSpecOfComponentException(
            hash(), spec(), "arguments", lambdaSpec.parameters(), workaround);
      }
    } else {
      throw new DecodeExprWrongEvaluationSpecOfComponentException(
          hash(), spec(), "function", LambdaSpec.class, functionEvaluationSpec);
    }
  }

  private Expr readFunction() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), FUNCTION_INDEX, DATA_SEQUENCE_SIZE, Expr.class);
  }

  private ImmutableList<Expr> readArguments() {
    var hash = readSequenceElementHash(DATA_PATH, dataHash(), ARGUMENTS_INDEX, DATA_SEQUENCE_SIZE);
    return readSequenceObjs(ARGUMENTS_PATH, hash, Expr.class);
  }

  @Override
  public String valueToString() {
    return "Call(???)";
  }
}
