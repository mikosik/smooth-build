package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.type.TypeS;

public record ParamRefS(TypeS evalT, String paramName, Location location) implements ExprS {
  @Override
  public String toString() {
    var fields = joinToString("\n",
        "evalT = " + evalT,
        "paramName = " + paramName,
        "location = " + location
    );
    return "ParamRefS(\n" + indent(fields) + "\n)";
  }
}
