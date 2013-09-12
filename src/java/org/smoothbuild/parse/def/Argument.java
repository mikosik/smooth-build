package org.smoothbuild.parse.def;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.padEnd;

import java.util.Collection;
import java.util.Set;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.problem.CodeLocation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Argument {
  private final String name;
  private final DefinitionNode node;
  private final CodeLocation codeLocation;

  public static Argument explicitArg(String name, DefinitionNode node, CodeLocation codeLocation) {
    return new Argument(checkNotNull(name), node, codeLocation);
  }

  public static Argument implicitArg(DefinitionNode node, CodeLocation codeLocation) {
    return new Argument(null, node, codeLocation);
  }

  private Argument(String name, DefinitionNode node, CodeLocation codeLocation) {
    this.name = name;
    this.node = checkNotNull(node);
    this.codeLocation = checkNotNull(codeLocation);
  }

  public String name() {
    if (name == null) {
      throw new UnsupportedOperationException("Implicit argument does not have name.");
    }
    return name;
  }

  public String nameSanitized() {
    return name == null ? "<implicit>" : name;
  }

  public Type type() {
    return node.type();
  }

  public DefinitionNode definitionNode() {
    return node;
  }

  public CodeLocation codeLocation() {
    return codeLocation;
  }

  public boolean isExplicit() {
    return name != null;
  }

  public String toPaddedString(int minTypeLength, int minNameLength) {
    String type = padEnd(type().name(), minTypeLength, ' ') + ": ";
    String name = padEnd(nameSanitized(), minNameLength, ' ');
    String location = codeLocation.toString();
    return type + name + " " + location;
  }

  @Override
  public String toString() {
    return type().name() + ":" + nameSanitized();
  }

  public static ImmutableList<Argument> filterExplicit(Collection<Argument> arguments) {
    ImmutableList.Builder<Argument> builder = ImmutableList.builder();
    for (Argument argument : arguments) {
      if (argument.isExplicit()) {
        builder.add(argument);
      }
    }
    return builder.build();
  }

  public static ImmutableMap<Type, Set<Argument>> filterImplicit(Collection<Argument> arguments) {
    ImmutableMap<Type, Set<Argument>> result = Helpers.createMap(Type.allTypes());
    for (Argument argument : arguments) {
      if (!argument.isExplicit()) {
        Type type = argument.definitionNode().type();
        result.get(type).add(argument);
      }
    }
    return result;
  }
}
