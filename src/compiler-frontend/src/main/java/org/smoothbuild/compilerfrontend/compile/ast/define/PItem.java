package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.NList;
import org.smoothbuild.compilerfrontend.lang.base.NalImpl;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public final class PItem extends NalImpl implements PReferenceable {
  private final PType type;
  private final Maybe<String> defaultValueFullName;
  private SType sType;

  public PItem(PType type, String name, Maybe<String> defaultValueFullName, Location location) {
    super(name, location);
    this.type = type;
    this.defaultValueFullName = defaultValueFullName;
  }

  @Override
  public String shortName() {
    return name();
  }

  public PType type() {
    return type;
  }

  public Maybe<String> defaultValueFullName() {
    return defaultValueFullName;
  }

  public SType sType() {
    return sType;
  }

  public SType setSType(SType type) {
    this.sType = type;
    return type;
  }

  public static List<SType> toSType(NList<PItem> params) {
    return params.list().map(PItem::sType);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PItem that
        && Objects.equals(this.type, that.type)
        && Objects.equals(this.name(), that.name())
        && Objects.equals(this.defaultValueFullName, that.defaultValueFullName())
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name(), defaultValueFullName, location());
  }

  @Override
  public String toString() {
    var fields = list(
            "type = " + type,
            "name = " + name(),
            "defaultValueFullName = " + defaultValueFullName,
            "location = " + location())
        .toString("\n");
    return "PItem(\n" + indent(fields) + "\n)";
  }
}
