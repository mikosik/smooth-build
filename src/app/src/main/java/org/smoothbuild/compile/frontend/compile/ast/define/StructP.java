package org.smoothbuild.compile.frontend.compile.ast.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.Iterables.joinToString;
import static org.smoothbuild.common.collect.NList.nlistWithShadowing;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compile.frontend.lang.base.NalImpl;
import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.type.StructTS;

public final class StructP extends NalImpl implements ScopedP {
  private final NList<ItemP> fields;
  private final NamedFuncP constructor;
  private ScopeP scope;
  private StructTS structTS;

  public StructP(String name, List<ItemP> fields, Location location) {
    this(name, nlistWithShadowing(ImmutableList.copyOf(fields)), location);
  }

  private StructP(String name, NList<ItemP> fields, Location location) {
    super(name, location);
    this.fields = fields;
    this.constructor = new ConstructorP(this);
  }

  public NList<ItemP> fields() {
    return fields;
  }

  public NamedFuncP constructor() {
    return constructor;
  }

  @Override
  public ScopeP scope() {
    return scope;
  }

  @Override
  public void setScope(ScopeP scope) {
    this.scope = scope;
  }

  public StructTS typeS() {
    return structTS;
  }

  public void setTypeS(StructTS structTS) {
    this.structTS = structTS;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof StructP that
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
    var fields = joinToString(
        "\n", "name = " + name(), "fields = " + this.fields, "location = " + location());
    return "StructP(\n" + indent(fields) + "\n)";
  }
}
