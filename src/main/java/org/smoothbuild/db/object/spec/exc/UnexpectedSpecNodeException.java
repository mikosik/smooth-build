package org.smoothbuild.db.object.spec.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.SpecKind;

public class UnexpectedSpecNodeException extends DecodeSpecNodeException {
  public UnexpectedSpecNodeException(Hash hash, SpecKind spec, String path, int pathIndex,
      Spec expected, Spec actual) {
    this(hash, spec, indexedPath(path, pathIndex), expected, actual);
  }

  public UnexpectedSpecNodeException(Hash hash, SpecKind spec, String path, Spec expected,
      Spec actual) {
    super(hash, spec, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Spec expected, Spec actual) {
    return "Node has unexpected spec. Expected " + expected.name() + " but was " + actual.name()
        + ".";
  }

  public UnexpectedSpecNodeException(Hash hash, SpecKind spec, String memberPath, int pathIndex,
      Class<?> expected, Class<?> actual) {
    this(hash, spec, indexedPath(memberPath, pathIndex), expected, actual);
  }

  private static String indexedPath(String memberPath, int pathIndex) {
    return memberPath + "[" + pathIndex + "]";
  }

  public UnexpectedSpecNodeException(Hash hash, SpecKind spec, String path, Class<?> expected,
      Class<?> actual) {
    super(hash, spec, path, buildMessage(expected, actual));
  }

  private static String buildMessage(Class<?> expected, Class<?> actual) {
    return "Node has unexpected class. Expected " + expected.getCanonicalName() + " but was "
        + actual.getCanonicalName() + ".";
  }
}