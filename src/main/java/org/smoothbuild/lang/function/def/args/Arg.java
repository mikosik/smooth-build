package org.smoothbuild.lang.function.def.args;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.padEnd;

import java.util.Collection;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.message.base.CodeLocation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Ordering;

public class Arg {
  private final int number;
  private final String name;
  private final Expr<?> expr;
  private final CodeLocation codeLocation;

  public static Arg namedArg(int number, String name, Expr<?> expr, CodeLocation codeLocation) {
    checkArgument(0 < number);
    return new Arg(number, checkNotNull(name), expr, codeLocation);
  }

  public static Arg namelessArg(int number, Expr<?> expr, CodeLocation codeLocation) {
    checkArgument(0 < number);
    return new Arg(number, null, expr, codeLocation);
  }

  public static Arg pipedArg(Expr<?> expr, CodeLocation codeLocation) {
    return new Arg(0, null, expr, codeLocation);
  }

  private Arg(int number, String name, Expr<?> expr, CodeLocation codeLocation) {
    checkArgument(0 <= number);
    this.number = number;
    this.name = name;
    this.expr = checkNotNull(expr);
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
    return expr.type();
  }

  public Expr<?> expr() {
    return expr;
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

  public static ImmutableMultimap<SType<?>, Arg> filterNameless(Collection<Arg> args) {
    ImmutableMultimap.Builder<SType<? extends Value>, Arg> builder = ImmutableMultimap.builder();
    for (Arg arg : args) {
      if (!arg.hasName()) {
        SType<?> type = arg.expr().type();
        builder.put(type, arg);
      }
    }
    return builder.build();
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
