package org.smoothbuild.compile.fs.ps.ast.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.compile.fs.lang.base.NalImpl;
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.TypeS;
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

  @Override
  public String shortName() {
    return name();
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
    var fields = joinToString("\n",
        "type = " + type,
        "name = " + name(),
        "defaultValue = " + defaultValue,
        "location = " + location()
    );
    return "ItemP(\n" + indent(fields) + "\n)";
  }
}
