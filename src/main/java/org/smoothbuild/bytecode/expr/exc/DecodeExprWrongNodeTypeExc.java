package org.smoothbuild.bytecode.expr.exc;

import org.smoothbuild.bytecode.type.CatB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.db.Hash;

public class DecodeExprWrongNodeTypeExc extends DecodeExprNodeExc {
  public DecodeExprWrongNodeTypeExc(Hash hash, CatB cat, String path, int pathIndex,
      TypeB expected, TypeB actual) {
    this(hash, cat, indexedPath(path, pathIndex), expected, actual);
  }

  public DecodeExprWrongNodeTypeExc(
      Hash hash, CatB cat, String path, TypeB expected, TypeB actual) {
    super(hash, cat, path, buildMessage(expected, actual));
  }

  private static String buildMessage(CatB expected, CatB actual) {
    return "Node has unexpected type. Expected " + expected.q() + " but was " + actual.q() + ".";
  }
}
