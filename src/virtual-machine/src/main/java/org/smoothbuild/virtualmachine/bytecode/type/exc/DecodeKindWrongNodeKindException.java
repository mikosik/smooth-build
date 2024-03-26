package org.smoothbuild.virtualmachine.bytecode.type.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.base.KindId;

public class DecodeKindWrongNodeKindException extends DecodeKindNodeException {
  public DecodeKindWrongNodeKindException(
      Hash hash,
      KindId kindId,
      String memberPath,
      int pathIndex,
      Class<?> expected,
      Class<?> actual) {
    this(hash, kindId, indexedPath(memberPath, pathIndex), expected, actual);
  }

  private static String indexedPath(String memberPath, int pathIndex) {
    return memberPath + "[" + pathIndex + "]";
  }

  public DecodeKindWrongNodeKindException(
      Hash hash, KindId kindId, String path, Class<?> expected, Class<?> actual) {
    super(hash, kindId, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected kind. Expected " + expected.getCanonicalName() + " class but was "
        + actual.getCanonicalName() + " class.";
  }
}
