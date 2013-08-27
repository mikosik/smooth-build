package org.smoothbuild.function.base;


import com.google.common.collect.ImmutableMap;

public class FunctionSignature {
  private final Type type;
  private final FullyQualifiedName name;
  private final ImmutableMap<String, Param> params;

  public FunctionSignature(Type type, FullyQualifiedName name, ImmutableMap<String, Param> params) {
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

  public ImmutableMap<String, Param> params() {
    return params;
  }
}
