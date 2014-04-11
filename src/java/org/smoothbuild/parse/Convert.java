package org.smoothbuild.parse;

import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.lang.convert.Conversions;
import org.smoothbuild.lang.convert.Converter;
import org.smoothbuild.lang.function.def.ConvertNode;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.type.SType;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.Message;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class Convert {

  public static <T extends SValue> ImmutableList<Node<T>> ifNeeded(SType<T> destinationType,
      Iterable<Node<?>> nodes) {
    Builder<Node<T>> builder = ImmutableList.builder();
    for (Node<?> node : nodes) {
      builder.add(Convert.ifNeeded(destinationType, node));
    }
    return builder.build();
  }

  public static <T extends SValue> Node<T> ifNeeded(SType<T> destinationType, Node<?> node) {
    if (destinationType == node.type()) {

      /*
       * This is safe as we've just checked types.
       */
      @SuppressWarnings("unchecked")
      Node<T> result = (Node<T>) node;

      return result;
    } else if (Conversions.canConvert(node.type(), destinationType)) {
      return convert(destinationType, node);
    } else {
      throw new Message(FATAL, "Bug in smooth binary: Cannot convert from " + node.type() + " to "
          + destinationType + ".");
    }
  }

  private static <S extends SValue, T extends SValue> Node<T> convert(SType<T> destinationType,
      Node<S> node) {
    Converter<S, T> converter = Conversions.converter(node.type(), destinationType);
    return new ConvertNode<>(node, converter, node.codeLocation());
  }
}
