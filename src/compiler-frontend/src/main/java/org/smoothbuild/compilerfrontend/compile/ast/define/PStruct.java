package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.NList.nlistWithShadowing;

import java.util.List;
import java.util.Objects;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.NalImpl;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;

public final class PStruct extends NalImpl implements PScoped {
  private final NList<PItem> fields;
  private final PNamedFunc constructor;
  private PScope scope;
  private SStructType sStructType;

  public PStruct(String name, List<PItem> fields, Location location) {
    this(name, nlistWithShadowing(fields), location);
  }

  private PStruct(String name, NList<PItem> fields, Location location) {
    super(name, location);
    this.fields = fields;
    this.constructor = new PConstructor(this);
  }

  public NList<PItem> fields() {
    return fields;
  }

  public PNamedFunc constructor() {
    return constructor;
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
        && Objects.equals(this.name(), that.name())
        && Objects.equals(this.fields, that.fields)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name(), fields, location());
  }

  @Override
  public String toString() {
    var fields = list("name = " + name(), "fields = " + this.fields, "location = " + location())
        .toString("\n");
    return "StructP(\n" + indent(fields) + "\n)";
  }
}
