package org.smoothbuild.compile.ps.ast.refable;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.NalImpl;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.type.TypeP;

public final class ItemP extends NalImpl implements RefableP {
  private final TypeP type;
  private final Optional<ExprP> defaultVal;
  private TypeS typeS;

  public ItemP(TypeP type, String name, Optional<ExprP> defaultVal, Loc loc) {
    super(name, loc);
    this.type = type;
    this.defaultVal = defaultVal;
  }

  public TypeP type() {
    return type;
  }

  public Optional<ExprP> defaultVal() {
    return defaultVal;
  }

  public TypeS typeS(){
    return typeS;
  }

  public void setTypeS(TypeS type) {
    this.typeS = type;
  }
}
