package org.smoothbuild.compile.frontend.compile.ast.define;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compile.frontend.lang.base.location.Location;

public final class FuncTP extends ExplicitTP {
  private final TypeP result;
  private final List<TypeP> params;

  public FuncTP(TypeP result, List<TypeP> params, Location location) {
    super("[" + result.name() + "]", location);
    this.result = result;
    this.params = params;
  }

  public TypeP result() {
    return result;
  }

  public List<TypeP> params() {
    return params;
  }
}
