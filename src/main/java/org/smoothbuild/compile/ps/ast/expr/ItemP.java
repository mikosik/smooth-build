package org.smoothbuild.compile.ps.ast.expr;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.Optional;

import org.smoothbuild.compile.lang.base.NalImpl;
import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public final class ItemP extends NalImpl implements RefableP {
  private final TypeP type;
  private final Optional<NamedValueP> defaultValue;
  private TypeS typeS;

  public ItemP(TypeP type, String name, Optional<NamedValueP> defaultValue, Location location) {
    super(name, location);
    this.type = type;
    this.defaultValue = defaultValue;
  }

  public TypeP type() {
    return type;
  }

  public Optional<NamedValueP> defaultValue() {
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
