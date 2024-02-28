package org.smoothbuild.vm.bytecode.expr.exc;

import org.smoothbuild.common.Hash;
import org.smoothbuild.vm.bytecode.type.CategoryB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

public class DecodeExprWrongNodeTypeException extends DecodeExprNodeException {
  public DecodeExprWrongNodeTypeException(
      Hash hash, CategoryB cat, String path, int pathIndex, TypeB expected, TypeB actual) {
    this(hash, cat, indexedPath(path, pathIndex), expected, actual);
  }

  public DecodeExprWrongNodeTypeException(
      Hash hash, CategoryB cat, String path, TypeB expected, TypeB actual) {
    super(hash, cat, path, buildMessage(expected, actual));
  }

  private static String buildMessage(CategoryB expected, CategoryB actual) {
    return buildMessage(expected.q(), actual.q());
  }

  public DecodeExprWrongNodeTypeException(
      Hash hash, CategoryB cat, String path, TypeB expected, String actual) {
    super(hash, cat, path, buildMessage(expected.q(), actual));
  }

  public DecodeExprWrongNodeTypeException(
      Hash hash, CategoryB cat, String path, int index, Class<?> expected, TypeB actual) {
    this(hash, cat, indexedPath(path, index), expected, actual);
  }

  public DecodeExprWrongNodeTypeException(
      Hash hash, CategoryB cat, String path, Class<?> expected, TypeB actual) {
    super(hash, cat, path, buildMessage("instance of " + expected.getSimpleName(), actual.q()));
  }

  private static String buildMessage(String expected, String actual) {
    return "Node has unexpected type. Expected " + expected + " but was " + actual + ".";
  }
}
