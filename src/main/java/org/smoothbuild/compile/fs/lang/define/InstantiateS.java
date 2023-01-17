package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * Instantiation of polymorphic entity.
 */
public record InstantiateS(
      ImmutableList<TypeS> typeArgs,
      MonoizableS monoizableS,
      TypeS evaluationT,
      Location location)
    implements ExprS {

  public InstantiateS(MonoizableS monoizableS, Location location) {
    this(list(), monoizableS, location);
    checkArgument(monoizableS.schema().quantifiedVars().isEmpty());
  }

  public InstantiateS(ImmutableList<TypeS> typeArgs, MonoizableS monoizableS, Location location) {
    this(typeArgs, monoizableS, monoizableS.schema().instantiate(typeArgs), location);
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "typeArgs = " + "<" + joinToString(typeArgs, ",") + ">",
        "monoizableS = " + monoizableS,
        "evaluationT = " + evaluationT,
        "location = " + location
    );
    return "InstantiateS(\n" + indent(fields) + "\n)";
  }
}
