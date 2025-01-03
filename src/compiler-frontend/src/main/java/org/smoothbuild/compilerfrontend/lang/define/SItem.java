package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.HasName;
import org.smoothbuild.compilerfrontend.lang.base.Identifiable;
import org.smoothbuild.compilerfrontend.lang.base.Item;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.name.Name;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Item is a func param or a struct field.
 * This class is immutable.
 */
public final class SItem implements Item, SReferenceable, HasName, Identifiable {
  private final Maybe<Id> defaultValueId;
  private final SType type;
  private final Name name;
  private final Location location;

  public SItem(SType type, Name name, Maybe<Id> defaultValueId, Location location) {
    this.type = type;
    this.name = name;
    this.defaultValueId = defaultValueId;
    this.location = location;
  }

  public SType type() {
    return type;
  }

  @Override
  public SSchema schema() {
    return new SSchema(varSetS(), type);
  }

  @Override
  public Maybe<Id> defaultValueId() {
    return defaultValueId;
  }

  @Override
  public Location location() {
    return location;
  }

  @Override
  public Name id() {
    return name();
  }

  @Override
  public Name name() {
    return name;
  }

  public static List<SType> toTypes(List<? extends SItem> items) {
    return items.map(SItem::type);
  }

  public String toSourceCode() {
    return type.toSourceCode() + " " + name.toString()
        + defaultValueId.map(id -> " = " + id.toSourceCode()).getOr("");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return (o instanceof SItem that)
        && Objects.equals(this.type(), that.type())
        && Objects.equals(this.id(), that.id())
        && Objects.equals(this.defaultValueId, that.defaultValueId)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), id(), defaultValueId, location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SItem")
        .addField("type", type().name())
        .addField("name", id())
        .addField("defaultValueId", defaultValueId)
        .addField("location", location())
        .toString();
  }
}
