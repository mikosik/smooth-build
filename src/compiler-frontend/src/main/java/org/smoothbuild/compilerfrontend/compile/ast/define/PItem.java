package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.Id;
import org.smoothbuild.compilerfrontend.lang.base.NList;
import org.smoothbuild.compilerfrontend.lang.base.Nal;
import org.smoothbuild.compilerfrontend.lang.base.Name;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public final class PItem extends Nal implements PReferenceable {
  private final PType type;
  private Name name;
  private final Maybe<PExpr> defaultValue;
  private Maybe<Id> defaultValueId;
  private SType sType;

  public PItem(PType type, String name, Maybe<PExpr> defaultValue, Location location) {
    super(name, location);
    this.type = type;
    this.defaultValue = defaultValue;
  }

  public PType type() {
    return type;
  }

  public void setName(Name name) {
    this.name = name;
  }

  public Name name() {
    return name;
  }

  @Override
  public Name id() {
    return name();
  }

  public Maybe<PExpr> defaultValue() {
    return defaultValue;
  }

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
    var fields = list(
            "type = " + type,
            "name = " + nameText(),
            "defaultValue = " + defaultValue,
            "defaultValueId = " + defaultValueId,
            "location = " + location())
        .toString("\n");
    return "PItem(\n" + indent(fields) + "\n)";
  }
}
