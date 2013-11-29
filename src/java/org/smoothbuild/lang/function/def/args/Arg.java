package org.smoothbuild.lang.function.def.args;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.padEnd;
import static org.smoothbuild.lang.type.STypes.allTypes;

import java.util.Collection;
import java.util.Set;

import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.type.SType;
import org.smoothbuild.message.base.CodeLocation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;

public class Arg {
  private final int number;
  private final String name;
  private final Node node;
  private final CodeLocation codeLocation;

  public static Arg namedArg(int number, String name, Node node, CodeLocation codeLocation) {
    checkArgument(0 < number);
    return new Arg(number, checkNotNull(name), node, codeLocation);
  }

  public static Arg namelessArg(int number, Node node, CodeLocation codeLocation) {
    checkArgument(0 < number);
    return new Arg(number, null, node, codeLocation);
  }

  public static Arg pipedArg(Node node, CodeLocation codeLocation) {
    return new Arg(0, null, node, codeLocation);
  }

  private Arg(int number, String name, Node node, CodeLocation codeLocation) {
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

  public SType<?> type() {
    return node.type();
  }

  public Node node() {
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

  public static ImmutableList<Arg> filterNamed(Collection<Arg> args) {
    ImmutableList.Builder<Arg> builder = ImmutableList.builder();
    for (Arg arg : args) {
      if (arg.hasName()) {
        builder.add(arg);
      }
    }
    return builder.build();
  }

  public static ImmutableMap<SType<?>, Set<Arg>> filterNameless(Collection<Arg> args) {
    ImmutableMap<SType<?>, Set<Arg>> result = Helpers.createMap(allTypes());
    for (Arg arg : args) {
      if (!arg.hasName()) {
        SType<?> type = arg.node().type();
        result.get(type).add(arg);
      }
    }
    return result;
  }

  public static final Ordering<Arg> NUMBER_ORDERING = new Ordering<Arg>() {
    @Override
    public int compare(Arg arg1, Arg arg2) {
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
