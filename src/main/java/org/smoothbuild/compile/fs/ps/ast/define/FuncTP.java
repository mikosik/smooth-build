package org.smoothbuild.compile.fs.ps.ast.define;

import org.smoothbuild.compile.fs.lang.base.location.Location;

import com.google.common.collect.ImmutableList;

public final class FuncTP extends ExplicitTP {
  private final TypeP result;
  private final ImmutableList<TypeP> params;

  public FuncTP(TypeP result, ImmutableList<TypeP> params, Location location) {
    super("[" + result.name() + "]", location);
    this.result = result;
    this.params = params;
  }

  public TypeP result() {
    return result;
  }

  public ImmutableList<TypeP> params() {
    return params;
  }
}
