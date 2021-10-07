package org.smoothbuild.db.object.spec.val;

import static java.util.stream.Collectors.joining;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.SpecKind;
import org.smoothbuild.db.object.spec.base.ValSpec;

public abstract class LambdaSpec extends ValSpec {
  private final ValSpec result;
  private final RecSpec parameters;
  private final RecSpec defaultArguments;

  protected LambdaSpec(Hash hash, SpecKind kind, ValSpec result, RecSpec parameters,
      RecSpec defaultArguments) {
    super(hash, kind);
    this.result = result;
    this.parameters = parameters;
    this.defaultArguments = defaultArguments;
  }

  public ValSpec result() {
    return result;
  }

  public RecSpec parameters() {
    return parameters;
  }

  public RecSpec defaultArguments() {
    return defaultArguments;
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
