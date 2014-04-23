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
      Iterable<Expr<?>> nodes) {
    Builder<Expr<T>> builder = ImmutableList.builder();
    for (Expr<?> node : nodes) {
      builder.add(Convert.ifNeeded(destinationType, node));
    }
    return builder.build();
  }

  public static <T extends SValue> Expr<T> ifNeeded(SType<T> destinationType, Expr<?> node) {
    if (destinationType == node.type()) {

      /*
       * This is safe as we've just checked types.
       */
      @SuppressWarnings("unchecked")
      Expr<T> result = (Expr<T>) node;

      return result;
    } else if (Conversions.canConvert(node.type(), destinationType)) {
      return convert(destinationType, node);
    } else {
      throw new Message(FATAL, "Bug in smooth binary: Cannot convert from " + node.type() + " to "
          + destinationType + ".");
    }
  }

  private static <S extends SValue, T extends SValue> Expr<T> convert(SType<T> destinationType,
      Expr<S> node) {
    Converter<S, T> converter = Conversions.converter(node.type(), destinationType);
    return new ConvertExpr<>(node, converter, node.codeLocation());
  }
}
