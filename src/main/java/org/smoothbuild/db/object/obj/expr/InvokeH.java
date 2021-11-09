package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.NativeMethodH;
import org.smoothbuild.db.object.type.expr.InvokeTypeH;

/**
 * This class is immutable.
 */
public class InvokeH extends ExprH {
  private static final int DATA_SEQUENCE_SIZE = 3;
  private static final int NATIVE_METHOD_INDEX = 0;
  private static final int IS_PURE_INDEX = 1;
  private static final int ARGUMENT_COUNT_INDEX = 2;

  public InvokeH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
    checkArgument(merkleRoot.type() instanceof InvokeTypeH);
  }

  @Override
  public InvokeTypeH type() {
    return (InvokeTypeH) super.type();
  }

  public InvokeData data() {
    NativeMethodH nativeMethod = readNativeMethod();
    BoolH isPure = readIsPure();
    IntH argumentCount = readArgumentCount();
    return new InvokeData(nativeMethod, isPure, argumentCount);
  }

  public static record InvokeData(NativeMethodH nativeMethod, BoolH isPure, IntH argumentCount) {}

  private NativeMethodH readNativeMethod() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), NATIVE_METHOD_INDEX, DATA_SEQUENCE_SIZE, NativeMethodH.class);
  }

  private BoolH readIsPure() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), IS_PURE_INDEX, DATA_SEQUENCE_SIZE, BoolH.class);
  }

  private IntH readArgumentCount() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), ARGUMENT_COUNT_INDEX, DATA_SEQUENCE_SIZE, IntH.class);
  }

  @Override
  public String valueToString() {
    return "Select(???)";
  }
}
