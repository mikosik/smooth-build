package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvalTypeOfComponentException;
import org.smoothbuild.db.object.type.expr.CallTypeH;
import org.smoothbuild.db.object.type.val.FuncTypeH;

/**
 * This class is immutable.
 */
public class CallH extends ExprH {
  private static final int DATA_SEQUENCE_SIZE = 2;
  private static final int FUNCTION_INDEX = 0;
  private static final int ARGUMENTS_INDEX = 1;

  public CallH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
    checkArgument(merkleRoot.spec() instanceof CallTypeH);
  }

  @Override
  public CallTypeH spec() {
    return (CallTypeH) super.spec();
  }

  public CallData data() {
    ObjectH func = readFunc();
    ConstructH arguments = readArguments();
    validate(func, arguments);
    return new CallData(func, arguments);
  }

  public record CallData(ObjectH func, ConstructH arguments) {}

  private void validate(ObjectH func, ConstructH argumentsConstruct) {
    if (func.type() instanceof FuncTypeH funcType) {
      var typing = objectDb().typing();
      var params = funcType.params();
      var arguments = argumentsConstruct.spec().evalType().items();
      allMatchOtherwise(
          params,
          arguments,
          typing::isParamAssignable,
          (expectedSize, actualSize) -> illegalArguments(funcType, argumentsConstruct),
          i -> illegalArguments(funcType, argumentsConstruct)
      );
      var variableBounds = typing.inferVariableBoundsInCall(params, arguments);
      var actualResult = typing.mapVariables(
          funcType.result(), variableBounds, typing.factory().lower());
      if (!Objects.equals(type(), actualResult)) {
        throw new DecodeExprWrongEvalTypeOfComponentException(
            hash(), spec(), "func.result", type(), actualResult);
      }
    } else {
      throw new DecodeExprWrongEvalTypeOfComponentException(
          hash(), spec(), "func", FuncTypeH.class, func.type());
    }
  }

  private void illegalArguments(FuncTypeH funcType, ConstructH arguments) {
    throw new DecodeExprWrongEvalTypeOfComponentException(hash(), spec(), "arguments",
        funcType.paramsTuple(), arguments.type());
  }

  private ObjectH readFunc() {
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
