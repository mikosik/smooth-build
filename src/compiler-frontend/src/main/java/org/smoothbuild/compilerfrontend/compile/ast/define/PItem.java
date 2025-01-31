package org.smoothbuild.compilerfrontend.compile.ast.define;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import java.util.Objects;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.Item;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public final class PItem implements Item, PReferenceable {
  private final PType type;
  private final String nameText;
  private final Maybe<PExpr> defaultValue;
  private final Location location;
  private Fqn fqn;
  private Maybe<Id> defaultValueId;
  private SType sType;

  public PItem(PType type, String nameText, Maybe<PExpr> defaultValue, Location location) {
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

  public Maybe<PExpr> defaultValue() {
    return defaultValue;
  }

  @Override
  public Location location() {
    return location;
  }

  @Override
  public Maybe<Id> defaultValueId() {
    return defaultValueId;
  }

  public void setDefaultValueId(Maybe<Id> defaultValueId) {
    this.defaultValueId = defaultValueId;
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
  public SSchema schema() {
    return new SSchema(varSetS(), requireNonNull(sType()));
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PItem that
        && Objects.equals(this.type, that.type)
        && Objects.equals(this.nameText(), that.nameText())
        && Objects.equals(this.defaultValue, that.defaultValue())
        && Objects.equals(this.defaultValueId, that.defaultValueId())
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, nameText(), defaultValue, location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PItem")
        .addField("type", type)
        .addField("name", nameText())
        .addField("defaultValue", defaultValue)
        .addField("defaultValueId", defaultValueId)
        .addField("location", location())
        .toString();
  }
}
