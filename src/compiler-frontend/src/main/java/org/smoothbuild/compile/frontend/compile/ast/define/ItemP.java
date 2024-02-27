package org.smoothbuild.compile.frontend.compile.ast.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compile.frontend.lang.base.NalImpl;
import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.type.TypeS;

public final class ItemP extends NalImpl implements ReferenceableP {
  private final TypeP type;
  private final Maybe<NamedValueP> defaultValue;
  private TypeS typeS;

  public ItemP(TypeP type, String name, Maybe<NamedValueP> defaultValue, Location location) {
    super(name, location);
    this.type = type;
    this.defaultValue = defaultValue;
  }

  @Override
  public String shortName() {
    return name();
  }

  public TypeP type() {
    return type;
  }

  public Maybe<NamedValueP> defaultValue() {
    return defaultValue;
  }

  public TypeS typeS() {
    return typeS;
  }

  public TypeS setTypeS(TypeS type) {
    this.typeS = type;
    return type;
  }

  public static List<TypeS> toTypeS(NList<ItemP> params) {
    return params.list().map(ItemP::typeS);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof ItemP that
        && Objects.equals(this.type, that.type)
        && Objects.equals(this.name(), that.name())
        && Objects.equals(this.defaultValue, that.defaultValue())
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name(), defaultValue, location());
  }

  @Override
  public String toString() {
    var fields = list(
            "type = " + type,
            "name = " + name(),
            "defaultValue = " + defaultValue,
            "location = " + location())
        .toString("\n");
    return "ItemP(\n" + indent(fields) + "\n)";
  }
}
