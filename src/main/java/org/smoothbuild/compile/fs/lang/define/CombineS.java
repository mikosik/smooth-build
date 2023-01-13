package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.TupleTS;

import com.google.common.collect.ImmutableList;

public record CombineS(TupleTS evalT, ImmutableList<ExprS> elems, Location location)
    implements ExprS {
  @Override
  public String toString() {
    var fields = joinToString("\n",
        "evalT = " + evalT,
        "elems = [\n" + indent(joinToString(elems, "\n")) + "\n]",
        "location = " + location
    );
    return "CombineS(\n" + indent(fields) + "\n)";
  }
}
