package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.Item;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;

public final class PItem implements Item, PReferenceable {
  private final PType type;
  private final String nameText;
  private final Maybe<PDefaultValue> defaultValue;
  private final Location location;
  private Fqn fqn;

  public PItem(PType type, String nameText, Maybe<PDefaultValue> defaultValue, Location location) {
    this.type = type;
    this.nameText = nameText;
    this.defaultValue = defaultValue;
    this.location = location;
  }

  public PType type() {
    return type;
  }

  public String nameText() {
    return nameText;
  }

  public String q() {
    return Strings.q(nameText);
  }

  public void setFqn(Fqn fqn) {
    this.fqn = fqn;
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  @Override
  public Maybe<PDefaultValue> defaultValue() {
    return defaultValue;
  }

  @Override
  public Location location() {
    return location;
  }

  @Override
  public SSchema schema() {
    return new SSchema(list(), type().sType());
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PItem that
        && Objects.equals(this.type, that.type)
        && Objects.equals(this.nameText, that.nameText)
        && Objects.equals(this.defaultValue, that.defaultValue)
        && Objects.equals(this.location, that.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, nameText, defaultValue, location);
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PItem")
        .addField("type", type)
        .addField("name", nameText())
        .addField("defaultValue", defaultValue)
        .addField("location", location())
        .toString();
  }
}
