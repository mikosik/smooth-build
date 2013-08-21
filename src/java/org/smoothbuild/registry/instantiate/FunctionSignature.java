package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.function.FullyQualifiedName;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.Type;

public class FunctionSignature {
  private final Type type;
  private final FullyQualifiedName name;
  private final Params params;

  public FunctionSignature(Type type, FullyQualifiedName name, Params params) {
    this.type = type;
    this.name = name;
    this.params = params;
  }

  public Type type() {
    return type;
  }

  public FullyQualifiedName name() {
    return name;
  }

  public Params params() {
    return params;
  }
}
