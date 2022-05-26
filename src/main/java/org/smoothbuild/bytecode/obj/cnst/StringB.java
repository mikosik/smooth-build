package org.smoothbuild.bytecode.obj.cnst;

import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;

/**
 * This class is thread-safe.
 */
public final class StringB extends CnstB {
  public StringB(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    super(merkleRoot, objDb);
  }

  public String toJ() {
    return readData(() -> hashedDb().readString(dataHash()));
  }

  @Override
  public String objToString() {
    return escapedAndLimitedWithEllipsis(toJ(), 30);
  }
}
