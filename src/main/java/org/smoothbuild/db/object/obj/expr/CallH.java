package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.db.object.type.expr.CallCH;
import org.smoothbuild.db.object.type.val.FuncTH;

/**
 * This class is thread-safe.
 */
public class CallH extends CallLikeH {
  public CallH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.cat() instanceof CallCH);
  }

  @Override
  public CallCH cat() {
    return (CallCH) super.cat();
  }

  public Data data() {
    var func = readFunc();
    var args = readArgs();
    validate(func, args);
    return new Data(func, args);
  }

  public record Data(ObjH callable, CombineH args) {}

  private void validate(ObjH func, CombineH argsCombine) {
    if (func.type() instanceof FuncTH funcT) {
      validate(funcT, argsCombine);
    } else {
      throw new DecodeObjWrongNodeTypeExc(
          hash(), cat(), "func", FuncTH.class, func.type().getClass());
    }
  }

  private ObjH readFunc() {
    return readSeqElemObj(DATA_PATH, dataHash(), CALLABLE_INDEX, DATA_SEQ_SIZE, ObjH.class);
  }

  private CombineH readArgs() {
    return readSeqElemObj(DATA_PATH, dataHash(), ARGS_INDEX, DATA_SEQ_SIZE, CombineH.class);
  }
}
