package org.smoothbuild.db.object.obj.val;

import static org.smoothbuild.db.object.type.base.SpecKindH.IF_FUNC;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

/**
 * This class is immutable.
 */
public class IfFuncH extends FuncH {
  private static final int DATA_SEQ_SIZE = 3;
  private static final int NAT_FUNC_INDEX = 0;
  private static final int IS_PURE_INDEX = 1;
  private static final int ARG_COUNT_INDEX = 2;

  public IfFuncH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb, IF_FUNC);
  }


  public IfData data() {
    var natFuncH = readNatFunc();
    var isPure = readIsPure();
    var argCount = readArgCount();
    return new IfData(natFuncH, isPure, argCount);
  }

  public static record IfData(NatFuncH natFuncH, BoolH isPure, IntH argCount) {}

  private NatFuncH readNatFunc() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), NAT_FUNC_INDEX, DATA_SEQ_SIZE, NatFuncH.class);
  }

  private BoolH readIsPure() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), IS_PURE_INDEX, DATA_SEQ_SIZE, BoolH.class);
  }

  private IntH readArgCount() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), ARG_COUNT_INDEX, DATA_SEQ_SIZE, IntH.class);
  }

  @Override
  public String valToString() {
    return "IfH(???)";
  }
}
