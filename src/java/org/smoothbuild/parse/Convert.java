package org.smoothbuild.parse;

import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.lang.convert.Conversions;
import org.smoothbuild.lang.convert.Converter;
import org.smoothbuild.lang.function.def.ConvertNode;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.type.SType;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.ErrorMessageException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class Convert {

  public static ImmutableList<Node> ifNeeded(SType<?> destinationType, Iterable<Node> nodes) {
    Builder<Node> builder = ImmutableList.builder();
    for (Node node : nodes) {
      builder.add(Convert.ifNeeded(destinationType, node));
    }
    return builder.build();
  }

  public static Node ifNeeded(SType<?> destinationType, Node node) {
    SType<?> type = node.type();
    if (destinationType == type) {
      return node;
    } else if (Conversions.canConvert(type, destinationType)) {
      Converter<?> converter = Conversions.converter(type, destinationType);
      return new ConvertNode(node, converter, node.codeLocation());
    } else {
      throw new ErrorMessageException(new Message(FATAL,
          "Bug in smooth binary: Cannot convert from " + type + " to " + destinationType + "."));
    }
  }
}
