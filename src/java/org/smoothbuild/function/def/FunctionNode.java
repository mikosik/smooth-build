package org.smoothbuild.function.def;

import java.util.Map;

import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.Task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class FunctionNode implements DefinitionNode {
  private final Function function;
  private final ImmutableMap<String, DefinitionNode> argNodes;

  public FunctionNode(Function function, Map<String, DefinitionNode> argNodes) {
    this.function = function;
    this.argNodes = ImmutableMap.copyOf(argNodes);
  }

  @Override
  public Type type() {
    return function.type();
  }

  @Override
  public Task generateTask() {
    Builder<String, Task> builder = ImmutableMap.builder();
    for (Map.Entry<String, DefinitionNode> entry : argNodes.entrySet()) {
      String argName = entry.getKey();
      Task task = entry.getValue().generateTask();
      builder.put(argName, task);
    }
    return function.generateTask(builder.build());
  }
}
