package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.TypeS;

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
