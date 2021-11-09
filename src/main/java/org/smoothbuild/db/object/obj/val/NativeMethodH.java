package org.smoothbuild.db.object.obj.val;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.type.val.NativeMethodTypeH;

/**
 * This class is immutable.
 */
public class NativeMethodH extends ValueH {
  private static final int DATA_SEQUENCE_SIZE = 2;
  private static final int JAR_FILE_INDEX = 0;
  private static final int CLASS_BINARY_NAME_INDEX = 1;

  public NativeMethodH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
    checkArgument(merkleRoot.type() instanceof NativeMethodTypeH);
  }

  public BlobH jarFile() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), JAR_FILE_INDEX, DATA_SEQUENCE_SIZE, BlobH.class);
  }

  public StringH classBinaryName() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), CLASS_BINARY_NAME_INDEX, DATA_SEQUENCE_SIZE, StringH.class);
  }

  @Override
  public String valueToString() {
    return "NativeMethod(???)";
  }
}
