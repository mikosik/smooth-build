package org.smoothbuild.function.def.args;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.padEnd;

import java.util.Collection;
import java.util.Set;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.def.LocatedNode;
import org.smoothbuild.message.message.CodeLocation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;

public class Argument {
  private final int number;
  private final String name;
  private final LocatedNode node;
  private final CodeLocation codeLocation;

  public static Argument namedArg(int number, String name, LocatedNode node,
      CodeLocation codeLocation) {
    checkArgument(0 < number);
    return new Argument(number, checkNotNull(name), node, codeLocation);
  }

  public static Argument namelessArg(int number, LocatedNode node, CodeLocation codeLocation) {
    checkArgument(0 < number);
    return new Argument(number, null, node, codeLocation);
  }

  public static Argument pipedArg(LocatedNode node, CodeLocation codeLocation) {
    return new Argument(0, null, node, codeLocation);
  }

  private Argument(int number, String name, LocatedNode node, CodeLocation codeLocation) {
    checkArgument(0 <= number);
    this.number = number;
    this.name = name;
    this.node = checkNotNull(node);
    this.codeLocation = checkNotNull(codeLocation);
  }

  /**
   * Number of position of this argument in function call's argument list. Value
   * zero denotes piped argument. Value one denotes first argument on the list.
   */
  public int number() {
    return number;
  }

  public String name() {
    if (name == null) {
      throw new UnsupportedOperationException("Nameless argument does not have name.");
    }
    return name;
  }

  public String nameSanitized() {
    return name == null ? "<nameless>" : name;
  }

  public Type type() {
    return node.type();
  }

  public LocatedNode node() {
    return node;
  }

  public CodeLocation codeLocation() {
    return codeLocation;
  }

  public boolean hasName() {
    return name != null;
  }

  public String toPaddedString(int minTypeLength, int minNameLength, int minNumberLength) {
    String type = padEnd(type().name(), minTypeLength, ' ') + ": ";
    String name = padEnd(nameSanitized(), minNameLength, ' ');
    String number = padEnd(numberString(), minNumberLength, ' ');
    String location = codeLocation.toString();
    return type + name + " #" + number + " " + location;
  }

  private String numberString() {
    return number == 0 ? "|" : Integer.toString(number);
  }

  @Override
  public String toString() {
    return type().name() + ":" + nameSanitized();
  }

  public static ImmutableList<Argument> filterNamed(Collection<Argument> arguments) {
    ImmutableList.Builder<Argument> builder = ImmutableList.builder();
    for (Argument argument : arguments) {
      if (argument.hasName()) {
        builder.add(argument);
      }
    }
    return builder.build();
  }

  public static ImmutableMap<Type, Set<Argument>> filterNameless(Collection<Argument> arguments) {
    ImmutableMap<Type, Set<Argument>> result = Helpers.createMap(Type.allTypes());
    for (Argument argument : arguments) {
      if (!argument.hasName()) {
        Type type = argument.node().type();
        result.get(type).add(argument);
      }
    }
    return result;
  }

  public static final Ordering<Argument> NUMBER_ORDERING = new Ordering<Argument>() {
    @Override
    public int compare(Argument arg1, Argument arg2) {
      int number1 = arg1.number();
      int number2 = arg2.number();
      if (number1 == number2) {
        return 0;
      }
      if (number1 < number2) {
        return -1;
      } else {
        return 1;
      }
    }
  };
}
