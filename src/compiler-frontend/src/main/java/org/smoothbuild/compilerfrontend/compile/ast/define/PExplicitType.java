package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public abstract sealed class PExplicitType extends PType
    permits PArrayType, PFuncType, PTypeReference {
  protected PExplicitType(String nameText, Location location) {
    super(nameText, location);
  }

  public SType infer() {
    return switch (this) {
      case PArrayType a -> new SArrayType(a.elemT().infer());
      case PFuncType f -> translateFunc(f);
      case PTypeReference r -> r.referenced().type();
    };
  }

  private SFuncType translateFunc(PFuncType pFuncType) {
    var paramTypes = pFuncType.params().map(PExplicitType::infer);
    var resultType = pFuncType.result().infer();
    return new SFuncType(paramTypes, resultType);
  }
}
