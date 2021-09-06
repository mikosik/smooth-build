package org.smoothbuild.db.object.spec.val;

import static java.util.stream.Collectors.joining;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.SpecKind;
import org.smoothbuild.db.object.spec.base.ValSpec;

public abstract class LambdaSpec extends ValSpec {
  private final ValSpec result;
  private final RecSpec parameters;

  protected LambdaSpec(Hash hash, SpecKind kind, ValSpec result,
      RecSpec parameters, ObjectDb objectDb) {
    super(hash, kind, objectDb);
    this.result = result;
    this.parameters = parameters;
  }

  public ValSpec result() {
    return result;
  }

  public RecSpec parameters() {
    return parameters;
  }

  @Override
  public String name() {
    String map = parameters.items()
        .stream()
        .map(Spec::name)
        .collect(joining(","));
    return result.name() + "(" + map + ")";
  }
}
