package org.smoothbuild.compile.frontend.compile.ast.define;

import com.google.common.collect.ImmutableList;
import org.smoothbuild.compile.frontend.lang.base.location.Location;

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
