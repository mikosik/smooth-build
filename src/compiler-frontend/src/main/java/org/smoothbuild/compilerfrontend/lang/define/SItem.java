package org.smoothbuild.compilerfrontend.lang.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.HasName;
import org.smoothbuild.compilerfrontend.lang.base.HasTypeAndIdAndLocation;
import org.smoothbuild.compilerfrontend.lang.base.Id;
import org.smoothbuild.compilerfrontend.lang.base.Name;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Item is a func param or a struct field.
 * This class is immutable.
 */
public final class SItem extends HasTypeAndIdAndLocation implements SReferenceable, HasName {
  private final Maybe<Id> defaultValueId;

  public SItem(SType type, Name name, Maybe<Id> defaultValueId, Location location) {
    super(type, name, location);
    this.defaultValueId = defaultValueId;
  }

  public Maybe<Id> defaultValueId() {
    return defaultValueId;
  }

  @Override
  public Name id() {
    return name();
  }

  @Override
  public Name name() {
    return (Name) super.id();
  }

  public static List<SType> toTypes(List<? extends SItem> items) {
    return items.map(SItem::type);
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
