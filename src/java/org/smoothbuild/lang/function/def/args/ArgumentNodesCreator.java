package org.smoothbuild.lang.function.def.args;

import static org.smoothbuild.lang.type.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.type.STypes.EMPTY_ARRAY;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;
import static org.smoothbuild.message.base.MessageType.FATAL;

import java.util.Collection;
import java.util.Map;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.def.ArrayNode;
import org.smoothbuild.lang.function.def.CachingNode;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.type.SType;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ArgumentNodesCreator {

  public Map<String, Node> createArgumentNodes(CodeLocation codeLocation, MessageGroup messages,
      Function function, Collection<Argument> arguments) {
    ParamToArgMapper mapper = new ParamToArgMapper(codeLocation, messages, function, arguments);
    Map<Param, Argument> paramToArgMap = mapper.detectMapping();
    messages.failIfContainsProblems();
    return createArgumentNodes(paramToArgMap);
  }

  private Map<String, Node> createArgumentNodes(Map<Param, Argument> paramToArgMap) {
    Builder<String, Node> builder = ImmutableMap.builder();

    for (Map.Entry<Param, Argument> entry : paramToArgMap.entrySet()) {
      Param param = entry.getKey();
      Argument arg = entry.getValue();
      Node node = argumentNode(param, arg);
      builder.put(param.name(), node);
    }

    return builder.build();
  }

  private Node argumentNode(Param param, Argument arg) {
    SType<?> paramType = param.type();
    if (arg.type() == EMPTY_ARRAY) {
      if (paramType == STRING_ARRAY) {
        ArrayNode node = new ArrayNode(STRING_ARRAY, Empty.nodeList(), arg.codeLocation());
        return new CachingNode(node);
      } else if (paramType == FILE_ARRAY) {
        ArrayNode node = new ArrayNode(FILE_ARRAY, Empty.nodeList(), arg.codeLocation());
        return new CachingNode(node);
      } else if (paramType == BLOB_ARRAY) {
        ArrayNode node = new ArrayNode(BLOB_ARRAY, Empty.nodeList(), arg.codeLocation());
        return new CachingNode(node);
      } else {
        throw new ErrorMessageException(new Message(FATAL,
            "Bug in smooth binary: Cannot convert from " + arg.type() + " to " + paramType + "."));
      }
    } else {
      return arg.node();
    }
  }
}
