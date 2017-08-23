package org.smoothbuild.parse.arg;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.padEnd;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;

import java.util.Collection;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.ast.ArgNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Ordering;

public class Argument {
  private final ArgNode arg;
  private final String name;
  private final Expression expression;
  private final CodeLocation codeLocation;

  public Argument(ArgNode arg, String name, Expression expression, CodeLocation codeLocation) {
    this.arg = arg;
    this.name = name;
    this.expression = checkNotNull(expression);
    this.codeLocation = checkNotNull(codeLocation);
  }

  /**
   * Position of this argument in function call's argument list. Value zero
   * denotes piped argument. Value one denotes first argument on the list.
   */
  public int position() {
    return arg.position();
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
    return expression.type();
  }

  public Expression expression() {
    return expression;
  }

  public CodeLocation codeLocation() {
    return codeLocation;
  }

  public boolean hasName() {
    return name != null;
  }

  public String toPaddedString(int minTypeLength, int minNameLength, int minPositionLength) {
    String type = padEnd(type().name(), minTypeLength, ' ') + ": ";
    String name = padEnd(nameSanitized(), minNameLength, ' ');
    String position = padEnd(positionString(), minPositionLength, ' ');
    String location = codeLocation.toString();
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
        .collect(toImmutableListMultimap(a -> a.expression().type(), a -> a));
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
