package org.smoothbuild.db.object.spec.base;

import org.smoothbuild.db.hashed.Hash;

public abstract class ExprSpec extends Spec {
  private final ValSpec evaluationSpec;

  protected ExprSpec(String name, Hash hash, SpecKind kind, ValSpec evaluationSpec) {
    super(name + ":" + evaluationSpec.name(), hash, kind);
    this.evaluationSpec = evaluationSpec;
  }

  public ValSpec evaluationSpec() {
    return evaluationSpec;
  }
}
