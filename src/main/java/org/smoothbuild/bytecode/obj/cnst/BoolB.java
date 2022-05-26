package org.smoothbuild.bytecode.obj.cnst;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;

/**
 * This class is immutable.
 */
public final class BoolB extends CnstB {
  public BoolB(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    super(merkleRoot, objDb);
  }

  public boolean toJ() {
    return readData(() -> hashedDb().readBoolean(dataHash()));
  }

  @Override
  public String objToString() {
    return Boolean.toString(toJ());
  }
}
