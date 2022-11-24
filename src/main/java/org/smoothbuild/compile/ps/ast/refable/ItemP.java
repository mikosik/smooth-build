package org.smoothbuild.compile.ps.ast.refable;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.NalImpl;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public final class ItemP extends NalImpl implements RefableP {
  private final TypeP type;
  private final Optional<ExprP> defaultValue;
  private TypeS typeS;

  public ItemP(TypeP type, String name, Optional<ExprP> defaultValue, Loc loc) {
    super(name, loc);
    this.type = type;
    this.defaultValue = defaultValue;
  }

  public TypeP type() {
    return type;
  }

  public Optional<ExprP> defaultValue() {
    return defaultValue;
  }

  public TypeS typeS(){
    return typeS;
  }

  public TypeS setTypeS(TypeS type) {
    this.typeS = type;
    return type;
  }

  public static ImmutableList<TypeS> toTypeS(NList<ItemP> params) {
    return map(params, ItemP::typeS);
  }
}
