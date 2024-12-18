package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.HasIdAndNameTextAndLocation;
import org.smoothbuild.compilerfrontend.lang.base.Id;
import org.smoothbuild.compilerfrontend.lang.base.NList;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;

public final class PStruct extends HasIdAndNameTextAndLocation implements PScoped {
  private final NList<PItem> fields;
  private PScope scope;
  private SStructType sStructType;

  public PStruct(String name, NList<PItem> fields, Location location) {
    super(name, location);
    this.fields = fields;
  }

  @Override
  public void setId(Id id) {
    super.setId(id);
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
    var fields = list("name = " + id(), "fields = " + this.fields, "location = " + location())
        .toString("\n");
    return "PStruct(\n" + indent(fields) + "\n)";
  }
}
