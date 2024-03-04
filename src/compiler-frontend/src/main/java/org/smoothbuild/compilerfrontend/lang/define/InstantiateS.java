package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;

/**
 * Instantiation of polymorphic entity.
 */
public record InstantiateS(
    List<TypeS> typeArgs, PolymorphicS polymorphicS, TypeS evaluationType, Location location)
    implements ExprS {

  public InstantiateS(PolymorphicS polymorphicS, Location location) {
    this(list(), polymorphicS, location);
    checkArgument(polymorphicS.schema().quantifiedVars().isEmpty());
  }

  public InstantiateS(List<TypeS> typeArgs, PolymorphicS polymorphicS, Location location) {
    this(typeArgs, polymorphicS, polymorphicS.schema().instantiate(typeArgs), location);
  }

  @Override
  public String toString() {
    var fields = list(
            "typeArgs = " + "<" + typeArgs.toString(",") + ">",
            "polymorphicS = " + polymorphicS,
            "evaluationType = " + evaluationType,
            "location = " + location)
        .toString("\n");
    return "InstantiateS(\n" + indent(fields) + "\n)";
  }
}
