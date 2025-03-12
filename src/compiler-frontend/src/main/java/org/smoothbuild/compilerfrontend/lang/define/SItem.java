package org.smoothbuild.compilerfrontend.lang.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.DefaultValue;
import org.smoothbuild.compilerfrontend.lang.base.Item;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Item is a func param or a struct field.
 * This class is immutable.
 */
public final class SItem implements Item, SMonoReferenceable {
  private final Maybe<SDefaultValue> defaultValue;
  private final SType type;
  private final Fqn fqn;
  private final Location location;

  public SItem(SType type, Fqn fqn, Maybe<SDefaultValue> defaultValue, Location location) {
    this.type = type;
    this.fqn = fqn;
    this.defaultValue = defaultValue;
    this.location = location;
  }

  @Override
  public SType type() {
    return type;
  }

  public SType pType() {
    return type;
  }

  @Override
  public Maybe<? extends DefaultValue> defaultValue() {
    return defaultValue;
  }

  @Override
  public Location location() {
    return location;
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  public static List<SType> toTypes(List<? extends SItem> items) {
    return items.map(SItem::type);
  }

  public String toSourceCode() {
    return type.specifier() + " " + name().toString()
        + defaultValue.map(dv -> " = " + dv.fqn().toSourceCode()).getOr("");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return (o instanceof SItem that)
        && Objects.equals(this.type, that.type)
        && Objects.equals(this.fqn, that.fqn)
        && Objects.equals(this.defaultValue, that.defaultValue)
        && Objects.equals(this.location, that.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), fqn(), defaultValue, location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SItem")
        .addField("type", type())
        .addField("fqn", fqn())
        .addField("defaultValue", defaultValue)
        .addField("location", location())
        .toString();
  }
}
