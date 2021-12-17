package org.smoothbuild.db.object.obj.expr;

import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.db.object.type.val.CallableTH;
import org.smoothbuild.db.object.type.val.TupleTH;

public class CallLikeH extends ExprH {
  protected static final int DATA_SEQ_SIZE = 2;
  protected static final int CALLABLE_INDEX = 0;
  protected static final int ARGS_INDEX = 1;

  public CallLikeH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  protected void validate(CallableTH callableT, CombineH argsCombine) {
    var typing = objDb().typing();
    var params = callableT.params();
    var argsT = argsCombine.type();
    var argTs = argsT.items();
    allMatchOtherwise(
        params,
        argTs,
        typing::isParamAssignable,
        (expectedSize, actualSize) -> illegalArgs(callableT, argsT),
        i -> illegalArgs(callableT, argsT)
    );
    var varBounds = typing.inferVarBoundsLower(params, argTs);
    var actualResult = typing.mapVars(callableT.res(), varBounds, typing.factory().lower());
    if (!Objects.equals(type(), actualResult)) {
      throw new DecodeObjWrongNodeTypeExc(hash(), cat(), "callable.result", type(), actualResult);
    }
  }

  private void illegalArgs(CallableTH callableTH, TupleTH argsType) {
    throw new DecodeObjWrongNodeTypeExc(
        hash(), this.cat(), "args", callableTH.paramsTuple(), argsType);
  }
}
