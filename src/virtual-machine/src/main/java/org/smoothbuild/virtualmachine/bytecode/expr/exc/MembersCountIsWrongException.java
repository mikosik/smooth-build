package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;

public class MembersCountIsWrongException extends DecodeExprNodeException {
  public MembersCountIsWrongException(
      Hash hash, BKind kind, String path, int expectedSize, int actualSize) {
    super(
        hash,
        kind,
        path,
        "Wrong members count. Expected " + expectedSize + " but was " + actualSize + ".");
  }
}
