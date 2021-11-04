package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.NativeMethod;
import org.smoothbuild.db.object.type.expr.InvokeOType;

/**
 * This class is immutable.
 */
public class Invoke extends Expr {
  private static final int DATA_SEQUENCE_SIZE = 3;
  private static final int NATIVE_METHOD_INDEX = 0;
  private static final int IS_PURE_INDEX = 1;
  private static final int ARGUMENT_COUNT_INDEX = 2;

  public Invoke(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.type() instanceof InvokeOType);
  }

  @Override
  public InvokeOType type() {
    return (InvokeOType) super.type();
  }

  public InvokeData data() {
    NativeMethod nativeMethod = readNativeMethod();
    Bool isPure = readIsPure();
    Int argumentCount = readArgumentCount();
    return new InvokeData(nativeMethod, isPure, argumentCount);
  }

  public static record InvokeData(NativeMethod nativeMethod, Bool isPure, Int argumentCount) {}

  private NativeMethod readNativeMethod() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), NATIVE_METHOD_INDEX, DATA_SEQUENCE_SIZE, NativeMethod.class);
  }

  private Bool readIsPure() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), IS_PURE_INDEX, DATA_SEQUENCE_SIZE, Bool.class);
  }

  private Int readArgumentCount() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), ARGUMENT_COUNT_INDEX, DATA_SEQUENCE_SIZE, Int.class);
  }

  @Override
  public String valueToString() {
    return "Select(???)";
  }
}
