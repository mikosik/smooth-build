package org.smoothbuild.db.object.obj.val;

import static org.smoothbuild.db.object.type.base.SpecKindH.NAT_FUNC;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

/**
 * This class is immutable.
 */
public class NatFuncH extends FuncH {
  private static final int DATA_SEQ_SIZE = 3;
  private static final int JAR_FILE_INDEX = 0;
  private static final int CLASS_BINARY_NAME_INDEX = 1;
  private static final int IS_PURE_INDEX = 2;

  public NatFuncH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb, NAT_FUNC);
  }

  public BlobH jarFile() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), JAR_FILE_INDEX, DATA_SEQ_SIZE, BlobH.class);
  }

  public StringH classBinaryName() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), CLASS_BINARY_NAME_INDEX, DATA_SEQ_SIZE, StringH.class);
  }

  public BoolH isPure() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), IS_PURE_INDEX, DATA_SEQ_SIZE, BoolH.class);
  }

  @Override
  public String valueToString() {
    return "NatFuncH(???)";
  }
}