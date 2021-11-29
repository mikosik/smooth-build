package org.smoothbuild.db.object.obj.val;

import static org.smoothbuild.db.object.type.base.SpecKindH.MAP_FUNC;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

/**
 * This class is immutable.
 */
public class MapFuncH extends FuncH {
  private static final int DATA_SEQ_SIZE = 3;
  private static final int NAT_FUN_INDEX = 0;
  private static final int IS_PURE_INDEX = 1;
  private static final int ARG_COUNT_INDEX = 2;

  public MapFuncH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb, MAP_FUNC);
  }

  public MapData data() {
    var natFuncH = readNatFunc();
    var isPure = readIsPure();
    var argCount = readArgCount();
    return new MapData(natFuncH, isPure, argCount);
  }

  public static record MapData(NatFuncH natFuncH, BoolH isPure, IntH argCount) {}

  private NatFuncH readNatFunc() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), NAT_FUN_INDEX, DATA_SEQ_SIZE, NatFuncH.class);
  }

  private BoolH readIsPure() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), IS_PURE_INDEX, DATA_SEQ_SIZE, BoolH.class);
  }

  private IntH readArgCount() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), ARG_COUNT_INDEX, DATA_SEQ_SIZE, IntH.class);
  }

  @Override
  public String valueToString() {
    return "MapH(???)";
  }
}