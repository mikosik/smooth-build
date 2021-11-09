package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationTypeOfComponentException;
import org.smoothbuild.db.object.type.expr.CallTypeH;
import org.smoothbuild.db.object.type.val.FunctionTypeH;

/**
 * This class is immutable.
 */
public class CallH extends ExprH {
  private static final int DATA_SEQUENCE_SIZE = 2;
  private static final int FUNCTION_INDEX = 0;
  private static final int ARGUMENTS_INDEX = 1;

  public CallH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
    checkArgument(merkleRoot.type() instanceof CallTypeH);
  }

  @Override
  public CallTypeH type() {
    return (CallTypeH) super.type();
  }

  public CallData data() {
    ExprH function = readFunction();
    ConstructH arguments = readArguments();
    validate(function, arguments);
    return new CallData(function, arguments);
  }

  public record CallData(ExprH function, ConstructH arguments) {}

  private void validate(ExprH function, ConstructH arguments) {
    if (function.evaluationType() instanceof FunctionTypeH functionType) {
      if (!Objects.equals(evaluationType(), functionType.result())) {
        throw new DecodeExprWrongEvaluationTypeOfComponentException(
            hash(), type(), "function.result", evaluationType(), functionType.result());
      }
      if (!Objects.equals(functionType.parametersTuple(), arguments.type().evaluationType())) {
        throw new DecodeExprWrongEvaluationTypeOfComponentException(hash(), type(), "arguments",
            functionType.parametersTuple(), arguments.type().evaluationType());
      }
    } else {
      throw new DecodeExprWrongEvaluationTypeOfComponentException(
          hash(), type(), "function", FunctionTypeH.class, function.evaluationType());
    }
  }

  private ExprH readFunction() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), FUNCTION_INDEX, DATA_SEQUENCE_SIZE, ExprH.class);
  }

  private ConstructH readArguments() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), ARGUMENTS_INDEX, DATA_SEQUENCE_SIZE, ConstructH.class);
  }

  @Override
  public String valueToString() {
    return "Call(???)";
  }
}
