package org.smoothbuild.parse;

import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
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
    } else if (org.smoothbuild.lang.expr.Convert.isAssignable(expr.type(), destinationType)) {
      return org.smoothbuild.lang.expr.Convert.convertExpr(destinationType, expr);
    } else {
      throw new Message(FATAL, "Cannot convert from " + expr.type() + " to " + destinationType
          + ".");
    }
  }
}
