package org.smoothbuild.compile.ps.ast.type;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.NalImpl;
import org.smoothbuild.compile.lang.base.location.Location;

public sealed class TypeP extends NalImpl permits ArrayTP, FuncTP {
  public TypeP(String name, Location location) {
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
