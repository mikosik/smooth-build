package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.compilerfrontend.lang.base.Tanal;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Item is a func param or a struct field.
 * This class is immutable.
 */
public final class SItem extends Tanal implements SReferenceable {
  private final Maybe<SNamedValue> defaultValue;

  public SItem(SType type, String name, Maybe<SNamedValue> defaultValue, Location location) {
    super(type, name, location);
    this.defaultValue = defaultValue;
  }

  public Maybe<SNamedValue> defaultValue() {
    return defaultValue;
  }

  public static List<SType> toTypes(List<? extends SItem> items) {
    return items.map(SItem::type);
  }

  @Override
  public String toString() {
    var fields = list(
            "type = " + type().name(),
            "name = " + name(),
            "defaultValue = " + defaultValue,
            "location = " + location())
        .toString("\n");
    return "ItemS(\n" + indent(fields) + "\n)";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return (o instanceof SItem that)
        && Objects.equals(this.type(), that.type())
        && Objects.equals(this.name(), that.name())
        && Objects.equals(this.defaultValue, that.defaultValue)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), name(), defaultValue, location());
  }
}
