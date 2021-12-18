package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.db.object.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.db.object.type.expr.CallCB;
import org.smoothbuild.db.object.type.val.FuncTB;

/**
 * This class is thread-safe.
 */
public class CallB extends CallLikeB {
  public CallB(MerkleRoot merkleRoot, ByteDb byteDb) {
    super(merkleRoot, byteDb);
    checkArgument(merkleRoot.cat() instanceof CallCB);
  }

  @Override
  public CallCB cat() {
    return (CallCB) super.cat();
  }

  public Data data() {
    var func = readFunc();
    var args = readArgs();
    validate(func, args);
    return new Data(func, args);
  }

  public record Data(ObjB callable, CombineB args) {}

  private void validate(ObjB func, CombineB argsCombine) {
    if (func.type() instanceof FuncTB funcT) {
      validate(funcT, argsCombine);
    } else {
      throw new DecodeObjWrongNodeTypeExc(
          hash(), cat(), "func", FuncTB.class, func.type().getClass());
    }
  }

  private ObjB readFunc() {
    return readSeqElemObj(DATA_PATH, dataHash(), CALLABLE_INDEX, DATA_SEQ_SIZE, ObjB.class);
  }

  private CombineB readArgs() {
    return readSeqElemObj(DATA_PATH, dataHash(), ARGS_INDEX, DATA_SEQ_SIZE, CombineB.class);
  }
}
