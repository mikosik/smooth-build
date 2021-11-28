package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjectH;
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
    ObjectH function = readFunction();
    ConstructH arguments = readArguments();
    validate(function, arguments);
    return new CallData(function, arguments);
  }

  public record CallData(ObjectH function, ConstructH arguments) {}

  private void validate(ObjectH function, ConstructH argumentsConstruct) {
    if (function.evaluationType() instanceof FunctionTypeH functionType) {
      var typing = objectDb().typing();
      var params = functionType.params();
      var arguments = argumentsConstruct.type().evaluationType().items();
      allMatchOtherwise(
          params,
          arguments,
          typing::isParamAssignable,
          (expectedSize, actualSize) -> illegalArguments(functionType, argumentsConstruct),
          i -> illegalArguments(functionType, argumentsConstruct)
      );
      var variableBounds = typing.inferVariableBoundsInCall(params, arguments);
      var actualResult = typing.mapVariables(
          functionType.result(), variableBounds, typing.factory().lower());
      if (!Objects.equals(evaluationType(), actualResult)) {
        throw new DecodeExprWrongEvaluationTypeOfComponentException(
            hash(), type(), "function.result", evaluationType(), actualResult);
      }
    } else {
      throw new DecodeExprWrongEvaluationTypeOfComponentException(
          hash(), type(), "function", FunctionTypeH.class, function.evaluationType());
    }
  }

  private void illegalArguments(FunctionTypeH functionType, ConstructH arguments) {
    throw new DecodeExprWrongEvaluationTypeOfComponentException(hash(), type(), "arguments",
        functionType.paramsTuple(), arguments.evaluationType());
  }

  private ObjectH readFunction() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), FUNCTION_INDEX, DATA_SEQUENCE_SIZE, ObjectH.class);
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
