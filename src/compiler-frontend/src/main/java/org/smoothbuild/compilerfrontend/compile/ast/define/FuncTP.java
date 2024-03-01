package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;

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
