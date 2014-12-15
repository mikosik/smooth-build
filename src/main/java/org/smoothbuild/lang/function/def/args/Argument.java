package org.smoothbuild.lang.function.def.args;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.padEnd;

import java.util.Collection;

import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.message.base.CodeLocation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Ordering;

public class Argument {
  private final int number;
  private final String name;
  private final Expression expression;
  private final CodeLocation codeLocation;

  public static Argument namedArgument(int number, String name, Expression expression,
      CodeLocation codeLocation) {
    checkArgument(0 < number);
    return new Argument(number, checkNotNull(name), expression, codeLocation);
  }

  public static Argument namelessArgument(int number, Expression expression,
      CodeLocation codeLocation) {
    checkArgument(0 < number);
    return new Argument(number, null, expression, codeLocation);
  }

  public static Argument pipedArgument(Expression expression, CodeLocation codeLocation) {
    return new Argument(0, null, expression, codeLocation);
  }

  private Argument(int number, String name, Expression expression, CodeLocation codeLocation) {
    checkArgument(0 <= number);
    this.number = number;
    this.name = name;
    this.expression = checkNotNull(expression);
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

  public static ImmutableMultimap<Type, Argument> filterNameless(Collection<Argument> arguments) {
    ImmutableMultimap.Builder<Type, Argument> builder = ImmutableMultimap.builder();
    for (Argument argument : arguments) {
      if (!argument.hasName()) {
        Type type = argument.expression().type();
        builder.put(type, argument);
      }
    }
    return builder.build();
  }

  public static final Ordering<Argument> NUMBER_ORDERING = new Ordering<Argument>() {
    @Override
    public int compare(Argument argument1, Argument argument2) {
      int number1 = argument1.number();
      int number2 = argument2.number();
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
