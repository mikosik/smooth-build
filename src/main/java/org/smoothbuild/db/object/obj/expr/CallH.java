package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvalTypeOfCompExc;
import org.smoothbuild.db.object.type.expr.CallTypeH;
import org.smoothbuild.db.object.type.val.FuncTypeH;

/**
 * This class is immutable.
 */
public class CallH extends ExprH {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int FUNC_INDEX = 0;
  private static final int ARGS_INDEX = 1;

  public CallH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.spec() instanceof CallTypeH);
  }

  @Override
  public CallTypeH spec() {
    return (CallTypeH) super.spec();
  }

  public CallData data() {
    ObjH func = readFunc();
    CombineH args = readArgs();
    validate(func, args);
    return new CallData(func, args);
  }

  public record CallData(ObjH func, CombineH args) {}

  private void validate(ObjH func, CombineH argsCombine) {
    if (func.type() instanceof FuncTypeH funcType) {
      var typing = objDb().typing();
      var params = funcType.params();
      var args = argsCombine.spec().evalType().items();
      allMatchOtherwise(
          params,
          args,
          typing::isParamAssignable,
          (expectedSize, actualSize) -> illegalArgs(funcType, argsCombine),
          i -> illegalArgs(funcType, argsCombine)
      );
      var varBounds = typing.inferVarBoundsInCall(params, args);
      var actualResult = typing.mapVars(
          funcType.res(), varBounds, typing.factory().lower());
      if (!Objects.equals(type(), actualResult)) {
        throw new DecodeExprWrongEvalTypeOfCompExc(
            hash(), spec(), "func.result", type(), actualResult);
      }
    } else {
      throw new DecodeExprWrongEvalTypeOfCompExc(
          hash(), spec(), "func", FuncTypeH.class, func.type());
    }
  }

  private void illegalArgs(FuncTypeH funcType, CombineH args) {
    throw new DecodeExprWrongEvalTypeOfCompExc(hash(), spec(), "args",
        funcType.paramsTuple(), args.type());
  }

  private ObjH readFunc() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), FUNC_INDEX, DATA_SEQ_SIZE, ObjH.class);
  }

  private CombineH readArgs() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), ARGS_INDEX, DATA_SEQ_SIZE, CombineH.class);
  }

  @Override
  public String valToString() {
    return "Call(???)";
  }
}
