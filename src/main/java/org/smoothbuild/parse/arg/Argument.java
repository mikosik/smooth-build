package org.smoothbuild.parse.arg;

import static com.google.common.base.Strings.padEnd;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;

import java.util.Collection;

import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.ExprNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Ordering;

public class Argument {
  private final ArgNode arg;

  public Argument(ArgNode arg) {
    this.arg = arg;
  }

  public ArgNode arg() {
    return arg;
  }

  /**
   * Position of this argument in function call's argument list. Value zero
   * denotes piped argument. Value one denotes first argument on the list.
   */
  public int position() {
    return arg.position();
  }

  public boolean hasName() {
    return arg.hasName();
  }

  public String name() {
    String name = arg.hasName() ? arg.name() : null;
    if (name == null) {
      throw new UnsupportedOperationException("Nameless argument does not have name.");
    }
    return name;
  }

  public String nameSanitized() {
    return arg.hasName() ? arg.name() : "<nameless>";
  }

  public Type type() {
    return expr().get(Type.class);
  }

  public boolean hasType() {
    return expr().has(Type.class);
  }

  public ExprNode expr() {
    return arg.expr();
  }

  public CodeLocation codeLocation() {
    return arg.codeLocation();
  }

  public String toPaddedString(int minTypeLength, int minNameLength, int minPositionLength) {
    String type = padEnd(type().name(), minTypeLength, ' ') + ": ";
    String name = padEnd(nameSanitized(), minNameLength, ' ');
    String position = padEnd(positionString(), minPositionLength, ' ');
    String location = codeLocation().toString();
    return type + name + " #" + position + " " + location;
  }

  private String positionString() {
    return position() == 0 ? "|" : Integer.toString(position());
  }

  public String toString() {
    return type().name() + ":" + nameSanitized();
  }

  public static ImmutableList<Argument> filterNamed(Collection<Argument> arguments) {
    return arguments
        .stream()
        .filter(a -> a.hasName())
        .collect(toImmutableList());
  }

  public static ImmutableMultimap<Type, Argument> filterNameless(Collection<Argument> arguments) {
    return arguments
        .stream()
        .filter(a -> !a.hasName())
        .collect(toImmutableListMultimap(a -> a.type(), a -> a));
  }

  public static final Ordering<Argument> POSITION_ORDERING = new Ordering<Argument>() {
    public int compare(Argument argument1, Argument argument2) {
      int position1 = argument1.position();
      int position2 = argument2.position();
      if (position1 == position2) {
        return 0;
      }
      if (position1 < position2) {
        return -1;
      } else {
        return 1;
      }
    }
  };
}
