package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvalTypeOfCompExc;
import org.smoothbuild.db.object.type.expr.CallCH;
import org.smoothbuild.db.object.type.val.FuncTH;

/**
 * This class is thread-safe.
 */
public class CallH extends ExprH {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int FUNC_INDEX = 0;
  private static final int ARGS_INDEX = 1;

  public CallH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.cat() instanceof CallCH);
  }

  @Override
  public CallCH cat() {
    return (CallCH) super.cat();
  }

  public CallData data() {
    ObjH func = readFunc();
    CombineH args = readArgs();
    validate(func, args);
    return new CallData(func, args);
  }

  public record CallData(ObjH callable, CombineH args) {}

  private void validate(ObjH callable, CombineH argsCombine) {
    if (callable.type() instanceof FuncTH funcT) {
      var typing = objDb().typing();
      var params = funcT.params();
      var args = argsCombine.cat().evalT().items();
      allMatchOtherwise(
          params,
          args,
          typing::isParamAssignable,
          (expectedSize, actualSize) -> illegalArgs(funcT, argsCombine),
          i -> illegalArgs(funcT, argsCombine)
      );
      var varBounds = typing.inferVarBoundsLower(params, args);
      var actualResult = typing.mapVars(funcT.res(), varBounds, typing.factory().lower());
      if (!Objects.equals(type(), actualResult)) {
        throw new DecodeExprWrongEvalTypeOfCompExc(
            hash(), this.cat(), "func.result", type(), actualResult);
      }
    } else {
      throw new DecodeExprWrongEvalTypeOfCompExc(
          hash(), this.cat(), "func", FuncTH.class, callable.type());
    }
  }

  private void illegalArgs(FuncTH funcT, CombineH args) {
    throw new DecodeExprWrongEvalTypeOfCompExc(hash(), this.cat(), "args",
        funcT.paramsTuple(), args.type());
  }

  private ObjH readFunc() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), FUNC_INDEX, DATA_SEQ_SIZE, ObjH.class);
  }

  private CombineH readArgs() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), ARGS_INDEX, DATA_SEQ_SIZE, CombineH.class);
  }
}
