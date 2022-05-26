package org.smoothbuild.bytecode.obj.cnst;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.type.cnst.MethodTB;

/**
 * Native method. Encapsulates java jar and thus java method to invoke.
 *
 * This class is thread-safe.
 */
public final class MethodB extends CnstB {
  private static final int DATA_SEQ_SIZE = 3;
  private static final int JAR_IDX = 0;
  private static final int CLASS_BINARY_NAME_IDX = 1;
  private static final int IS_PURE_IDX = 2;

  public MethodB(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    super(merkleRoot, objDb);
  }

  @Override
  public MethodTB cat() {
    return (MethodTB) super.cat();
  }

  @Override
  public MethodTB type() {
    return cat();
  }

  public BlobB jar() {
    return readSeqElemObj(DATA_PATH, dataHash(), JAR_IDX, DATA_SEQ_SIZE, BlobB.class);
  }

  public StringB classBinaryName() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), CLASS_BINARY_NAME_IDX, DATA_SEQ_SIZE, StringB.class);
  }

  public BoolB isPure() {
    return readSeqElemObj(DATA_PATH, dataHash(), IS_PURE_IDX, DATA_SEQ_SIZE, BoolB.class);
  }

  @Override
  public String objToString() {
    return "Method(" + cat().name() + ")";
  }
}
