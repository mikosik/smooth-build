package org.smoothbuild.db.object.obj.val;

import static org.smoothbuild.db.object.type.base.TypeKindH.MAP_FUNCTION;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

/**
 * This class is immutable.
 */
public class MapFunctionH extends FunctionH {
  private static final int DATA_SEQ_SIZE = 3;
  private static final int NAT_FUN_INDEX = 0;
  private static final int IS_PURE_INDEX = 1;
  private static final int ARG_COUNT_INDEX = 2;

  public MapFunctionH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb, MAP_FUNCTION);
  }

  public MapData data() {
    var nativeFunctionH = readNativeFunction();
    var isPure = readIsPure();
    var argCount = readArgumentCount();
    return new MapData(nativeFunctionH, isPure, argCount);
  }

  public static record MapData(NativeFunctionH nativeFunctionH, BoolH isPure, IntH argumentCount) {}

  private NativeFunctionH readNativeFunction() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), NAT_FUN_INDEX, DATA_SEQ_SIZE, NativeFunctionH.class);
  }

  private BoolH readIsPure() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), IS_PURE_INDEX, DATA_SEQ_SIZE, BoolH.class);
  }

  private IntH readArgumentCount() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), ARG_COUNT_INDEX, DATA_SEQ_SIZE, IntH.class);
  }

  @Override
  public String valueToString() {
    return "MapH(???)";
  }
}
