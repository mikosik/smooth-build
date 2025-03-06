package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.location.Location;

public final class PFuncType extends PExplicitType {
  private final PExplicitType result;
  private final List<PExplicitType> params;

  public PFuncType(PExplicitType result, List<PExplicitType> params, Location location) {
    super(createName(result, params), location);
    this.result = result;
    this.params = params;
  }

  private static String createName(PExplicitType result, List<PExplicitType> params) {
    return "(" + params.map(PType::nameText).toString(",") + ")->" + result.nameText();
  }

  public PExplicitType result() {
    return result;
  }

  public List<PExplicitType> params() {
    return params;
  }
}
