package org.smoothbuild.parse;

import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.convert.Conversions;
import org.smoothbuild.lang.convert.Converter;
import org.smoothbuild.lang.expr.ConvertExpr;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.message.base.Message;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class Convert {

  public static <T extends SValue> ImmutableList<Expr<T>> ifNeeded(SType<T> destinationType,
      Iterable<Expr<?>> exprs) {
    Builder<Expr<T>> builder = ImmutableList.builder();
    for (Expr<?> expr : exprs) {
      builder.add(Convert.ifNeeded(destinationType, expr));
    }
    return builder.build();
  }

  public static <T extends SValue> Expr<T> ifNeeded(SType<T> destinationType, Expr<?> expr) {
    if (destinationType == expr.type()) {

      /*
       * This is safe as we've just checked types.
       */
      @SuppressWarnings("unchecked")
      Expr<T> result = (Expr<T>) expr;

      return result;
    } else if (Conversions.canConvert(expr.type(), destinationType)) {
      return convert(destinationType, expr);
    } else {
      throw new Message(FATAL, "Bug in smooth binary: Cannot convert from " + expr.type() + " to "
          + destinationType + ".");
    }
  }

  private static <S extends SValue, T extends SValue> Expr<T> convert(SType<T> destinationType,
      Expr<S> expr) {
    Converter<S, T> converter = Conversions.converter(expr.type(), destinationType);
    return new ConvertExpr<>(expr, converter, expr.codeLocation());
  }
}
