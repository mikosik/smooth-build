package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;

public class SubExprsCountIsWrongException extends DecodeExprNodeException {
  public SubExprsCountIsWrongException(
      Hash hash, BKind kind, String path, int expectedSize, int actualSize) {
    super(
        hash,
        kind,
        path,
        "Wrong sub-expressions count. Expected " + expectedSize + " but was " + actualSize + ".");
  }
}
