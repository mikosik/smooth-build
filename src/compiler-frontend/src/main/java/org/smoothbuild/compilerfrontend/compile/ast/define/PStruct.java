package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.IdentifiableCode;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;

public final class PStruct implements PTypeDefinition, PContainer, IdentifiableCode {
  private final String nameText;
  private final NList<PItem> fields;
  private final Location location;
  private Fqn fqn;
  private PScope scope;
  private SStructType sStructType;

  public PStruct(String nameText, NList<PItem> fields, Location location) {
    this.nameText = nameText;
    this.fields = fields;
    this.location = location;
  }

  public String nameText() {
    return nameText;
  }

  @Override
  public void setFqn(Fqn fqn) {
    this.fqn = fqn;
  }

  @Override
  public Fqn fqn() {
    return fqn;
  }

  public NList<PItem> fields() {
    return fields;
  }

  @Override
  public PScope scope() {
    return scope;
  }

  @Override
  public void setScope(PScope scope) {
    this.scope = scope;
  }

  @Override
  public SStructType type() {
    return sStructType;
  }

  public void setSType(SStructType sStructType) {
    this.sStructType = sStructType;
  }

  public String q() {
    return Strings.q(nameText);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PStruct that
        && Objects.equals(this.nameText(), that.nameText())
        && Objects.equals(this.fqn(), that.fqn())
        && Objects.equals(this.fields, that.fields)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(nameText, fqn(), fields, location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PStruct")
        .addField("fqn", fqn())
        .addField("fields", fields)
        .addField("location", location())
        .toString();
  }

  @Override
  public Location location() {
    return location;
  }
}
