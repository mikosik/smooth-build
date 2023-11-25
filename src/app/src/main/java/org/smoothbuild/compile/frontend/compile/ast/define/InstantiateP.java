package org.smoothbuild.compile.frontend.compile.ast.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.Iterables.joinToString;

import java.util.Objects;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.type.TypeS;

/**
 * Instantiation of polymorphic entity.
 */
public final class InstantiateP extends ExprP {
  private final PolymorphicP polymorphic;
  private List<TypeS> typeArgs;

  public InstantiateP(PolymorphicP polymorphic, Location location) {
    super(location);
    this.polymorphic = polymorphic;
  }

  public PolymorphicP polymorphic() {
    return polymorphic;
  }

  public void setTypeArgs(List<TypeS> typeArgs) {
    this.typeArgs = typeArgs;
  }

  public List<TypeS> typeArgs() {
    return typeArgs;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof InstantiateP that
        && Objects.equals(this.polymorphic, that.polymorphic)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(polymorphic, location());
  }

  @Override
  public String toString() {
    var fields = joinToString("\n", "polymorphic = " + polymorphic, "location = " + location());
    return "InstantiateP(\n" + indent(fields) + "\n)";
  }
}
