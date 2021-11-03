package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationTypeOfComponentException;
import org.smoothbuild.db.object.type.expr.CallOType;
import org.smoothbuild.db.object.type.val.LambdaOType;

/**
 * This class is immutable.
 */
public class Call extends Expr {
  private static final int DATA_SEQUENCE_SIZE = 2;
  private static final int FUNCTION_INDEX = 0;
  private static final int ARGUMENTS_INDEX = 1;

  public Call(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
    checkArgument(merkleRoot.type() instanceof CallOType);
  }

  @Override
  public CallOType type() {
    return (CallOType) super.type();
  }

  public CallData data() {
    Expr function = readFunction();
    Construct arguments = readArguments();
    validate(function, arguments);
    return new CallData(function, arguments);
  }

  public record CallData(Expr function, Construct arguments) {}

  private void validate(Expr function, Construct arguments) {
    if (function.evaluationType() instanceof LambdaOType lambdaType) {
      if (!Objects.equals(evaluationType(), lambdaType.result())) {
        throw new DecodeExprWrongEvaluationTypeOfComponentException(
            hash(), type(), "function.result", evaluationType(), lambdaType.result());
      }
      if (!Objects.equals(lambdaType.parametersTuple(), arguments.type().evaluationType())) {
        throw new DecodeExprWrongEvaluationTypeOfComponentException(hash(), type(), "arguments",
            lambdaType.parametersTuple(), arguments.type().evaluationType());
      }
    } else {
      throw new DecodeExprWrongEvaluationTypeOfComponentException(
          hash(), type(), "function", LambdaOType.class, function.evaluationType());
    }
  }

  private Expr readFunction() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), FUNCTION_INDEX, DATA_SEQUENCE_SIZE, Expr.class);
  }

  private Construct readArguments() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), ARGUMENTS_INDEX, DATA_SEQUENCE_SIZE, Construct.class);
  }

  @Override
  public String valueToString() {
    return "Call(???)";
  }
}
