package org.smoothbuild.compile.frontend.compile.ast.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.Iterables.joinToString;

import org.smoothbuild.compile.frontend.lang.base.NalImpl;
import org.smoothbuild.compile.frontend.lang.base.location.Location;

public abstract sealed class TypeP extends NalImpl permits ExplicitTP, ImplicitTP {
  protected TypeP(String name, Location location) {
    super(name, location);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof TypeP that
        && this.name().equals(that.name());
  }

  @Override
  public int hashCode() {
    return name().hashCode();
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "name = " + name(),
        "location = " + location()
    );
    return "TypeP(\n" + indent(fields) + "\n)";
  }
}
