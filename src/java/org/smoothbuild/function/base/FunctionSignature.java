package org.smoothbuild.function.base;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;

public class FunctionSignature {
  private final Type type;
  private final FullyQualifiedName name;
  private final ImmutableMap<String, Param> params;

  public FunctionSignature(Type type, FullyQualifiedName name, ImmutableMap<String, Param> params) {
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
    this.params = checkNotNull(params);
  }

  public Type type() {
    return type;
  }

  public FullyQualifiedName name() {
    return name;
  }

  public ImmutableMap<String, Param> params() {
    return params;
  }
}
