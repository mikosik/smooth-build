package org.smoothbuild.bytecode.expr.exc;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.CategoryB;

public class DecodeExprWrongNodeClassExc extends DecodeExprNodeExc {
  public DecodeExprWrongNodeClassExc(Hash hash, CategoryB cat, String memberPath, int pathIndex,
      Class<?> expected, Class<?> actual) {
    this(hash, cat, indexedPath(memberPath, pathIndex), expected, actual);
  }

  public DecodeExprWrongNodeClassExc(Hash hash, CategoryB cat, String path, Class<?> expected,
      Class<?> actual) {
    super(hash, cat, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected class. Expected " + expected.getCanonicalName()
        + " class but was " + actual.getCanonicalName() + " class.";
  }
}
