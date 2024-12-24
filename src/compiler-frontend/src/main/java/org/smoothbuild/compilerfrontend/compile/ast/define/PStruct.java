package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.HasIdAndLocation;
import org.smoothbuild.compilerfrontend.lang.base.HasNameTextAndLocationImpl;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;

public final class PStruct extends HasNameTextAndLocationImpl implements PScoped, HasIdAndLocation {
  private Id id;
  private final NList<PItem> fields;
  private PScope scope;
  private SStructType sStructType;

  public PStruct(String name, NList<PItem> fields, Location location) {
    super(name, location);
    this.fields = fields;
  }

  public void setId(Id id) {
    this.id = id;
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

  public SStructType sType() {
    return sStructType;
  }

  public void setSType(SStructType sStructType) {
    this.sStructType = sStructType;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PStruct that
        && Objects.equals(this.id(), that.id())
        && Objects.equals(this.fields, that.fields)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(id(), fields, location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PStruct")
        .addField("id", id())
        .addField("fields", fields)
        .addField("location", location())
        .toString();
  }

  @Override
  public Id id() {
    return id;
  }
}
