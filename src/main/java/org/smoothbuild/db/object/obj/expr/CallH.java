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
  private static final int FUNC_INDEX = 0;
  private static final int ARGS_INDEX = 1;

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
    ConstructH args = readArgs();
    validate(func, args);
    return new CallData(func, args);
  }

  public record CallData(ObjectH func, ConstructH args) {}

  private void validate(ObjectH func, ConstructH argsConstruct) {
    if (func.type() instanceof FuncTypeH funcType) {
      var typing = objectDb().typing();
      var params = funcType.params();
      var args = argsConstruct.spec().evalType().items();
      allMatchOtherwise(
          params,
          args,
          typing::isParamAssignable,
          (expectedSize, actualSize) -> illegalArgs(funcType, argsConstruct),
          i -> illegalArgs(funcType, argsConstruct)
      );
      var variableBounds = typing.inferVariableBoundsInCall(params, args);
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

  private void illegalArgs(FuncTypeH funcType, ConstructH args) {
    throw new DecodeExprWrongEvalTypeOfComponentException(hash(), spec(), "args",
        funcType.paramsTuple(), args.type());
  }

  private ObjectH readFunc() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), FUNC_INDEX, DATA_SEQUENCE_SIZE, ObjectH.class);
  }

  private ConstructH readArgs() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), ARGS_INDEX, DATA_SEQUENCE_SIZE, ConstructH.class);
  }

  @Override
  public String valueToString() {
    return "Call(???)";
  }
}
