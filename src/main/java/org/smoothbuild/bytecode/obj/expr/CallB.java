package org.smoothbuild.bytecode.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongNodeClassExc;
import org.smoothbuild.bytecode.type.expr.CallCB;
import org.smoothbuild.bytecode.type.val.FuncTB;

/**
 * This class is thread-safe.
 */
public class CallB extends CallLikeB {
  public CallB(MerkleRoot merkleRoot, ObjDbImpl byteDb) {
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
      throw new DecodeObjWrongNodeClassExc(
          hash(), cat(), "func", FuncTB.class, func.type().getClass());
    }
  }

  private ObjB readFunc() {
    return readSeqElemObj(DATA_PATH, dataHash(), CALLABLE_IDX, DATA_SEQ_SIZE, ObjB.class);
  }

  private CombineB readArgs() {
    return readSeqElemObj(DATA_PATH, dataHash(), ARGS_IDX, DATA_SEQ_SIZE, CombineB.class);
  }
}
