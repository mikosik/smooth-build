package org.smoothbuild.db.object.spec.base;

import org.smoothbuild.db.hashed.Hash;

public abstract class ExprSpec extends Spec {
  private final ValSpec evaluationSpec;

  protected ExprSpec(Hash hash, SpecKind kind, ValSpec evaluationSpec) {
    super(hash, kind);
    this.evaluationSpec = evaluationSpec;
  }

  public ValSpec evaluationSpec() {
    return evaluationSpec;
  }

  @Override
  public String name() {
    return expressionSpecName(kind(), evaluationSpec);
  }

  public static String expressionSpecName(SpecKind kind, Spec evaluationSpec) {
    return kind.name() + ":" + evaluationSpec.name();
  }
}
