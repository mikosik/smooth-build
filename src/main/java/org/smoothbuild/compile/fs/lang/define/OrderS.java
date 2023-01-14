package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.ArrayTS;

import com.google.common.collect.ImmutableList;

public record OrderS(ArrayTS evaluationT, ImmutableList<ExprS> elems, Location location)
    implements ExprS {
  @Override
  public String toString() {
    var fields = joinToString("\n",
        "evaluationT = " + evaluationT,
        "elems = [\n" + indent(joinToString(elems, "\n")) + "\n]",
        "location = " + location
    );
    return "OrderS(\n" + indent(fields) + "\n)";
  }
}
