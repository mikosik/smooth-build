package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.location.Location;

public final class PFuncType extends PExplicitType {
  private final PType result;
  private final List<PType> params;

  public PFuncType(PType result, List<PType> params, Location location) {
    super("[" + result.name() + "]", location);
    this.result = result;
    this.params = params;
  }

  public PType result() {
    return result;
  }

  public List<PType> params() {
    return params;
  }
}
