package org.smoothbuild.db.object.obj.val;

import static org.smoothbuild.db.object.type.base.TypeKindH.IF_FUNCTION;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

/**
 * This class is immutable.
 */
public class IfFunctionH extends FunctionH {
  private static final int DATA_SEQ_SIZE = 3;
  private static final int NAT_FUNC_INDEX = 0;
  private static final int IS_PURE_INDEX = 1;
  private static final int ARG_COUNT_INDEX = 2;

  public IfFunctionH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb, IF_FUNCTION);
  }


  public IfData data() {
    var nativeFunctionH = readNativeFunction();
    var isPure = readIsPure();
    var argCount = readArgumentCount();
    return new IfData(nativeFunctionH, isPure, argCount);
  }

  public static record IfData(NativeFunctionH nativeFunctionH, BoolH isPure, IntH argumentCount) {}

  private NativeFunctionH readNativeFunction() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), NAT_FUNC_INDEX, DATA_SEQ_SIZE, NativeFunctionH.class);
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
    return "IfH(???)";
  }
}
