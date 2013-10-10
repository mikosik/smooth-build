package org.smoothbuild.function.def;

import java.util.Map;

import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;

import com.google.common.collect.ImmutableList;

public class NodeCreator {
  public DefinitionNode stringSet(ImmutableList<? extends DefinitionNode> elemNodes,
      CodeLocation codeLocation) {
    return new StringSetNode(elemNodes, codeLocation);
  }

  public DefinitionNode emptySet() {
    return new EmptySetNode();
  }

  public DefinitionNode fileSet(ImmutableList<? extends DefinitionNode> elemNodes,
      CodeLocation codeLocation) {
    return new FileSetNode(elemNodes, codeLocation);
  }

  public DefinitionNode invalid(Type type) {
    return new InvalidNode(type);
  }

  public DefinitionNode call(Function function, CodeLocation codeLocation,
      Map<String, DefinitionNode> argNodes) {
    return new CallNode(function, codeLocation, argNodes);
  }

  public DefinitionNode string(String string) {
    return new StringNode(string);
  }
}
