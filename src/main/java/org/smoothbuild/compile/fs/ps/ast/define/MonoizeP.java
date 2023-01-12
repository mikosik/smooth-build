package org.smoothbuild.compile.fs.ps.ast.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import java.util.Objects;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * Monomorphization of MonoizableP.
 */
public final class MonoizeP extends ExprP {
  private final MonoizableP monoizable;
  private ImmutableList<TypeS> typeArgs;

  public MonoizeP(MonoizableP monoizable, Location location) {
    super(location);
    this.monoizable = monoizable;
  }

  public MonoizableP monoizable() {
    return monoizable;
  }

  public void setTypeArgs(ImmutableList<TypeS> typeArgs) {
    this.typeArgs = typeArgs;
  }

  public ImmutableList<TypeS> typeArgs() {
    return typeArgs;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof MonoizeP that
        && Objects.equals(this.monoizable, that.monoizable)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(monoizable, location());
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "monoizable = " + monoizable,
        "location = " + location()
    );
    return "MonoizeP(\n" + indent(fields) + "\n)";
  }
}
