package org.smoothbuild.function.def;

import java.util.Map;

import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class CallNode extends AbstractDefinitionNode {
  private final Function function;
  private final ImmutableMap<String, DefinitionNode> args;

  public CallNode(Function function, CodeLocation codeLocation, Map<String, DefinitionNode> args) {
    super(codeLocation);
    this.function = function;
    this.args = ImmutableMap.copyOf(args);
  }

  @Override
  public Type type() {
    return function.type();
  }

  @Override
  public Task generateTask() {
    Builder<String, Task> builder = ImmutableMap.builder();
    for (Map.Entry<String, DefinitionNode> entry : args.entrySet()) {
      String argName = entry.getKey();
      Task dependency = entry.getValue().generateTask();
      builder.put(argName, dependency);
    }
    ImmutableMap<String, Task> dependencies = builder.build();
    return function.generateTask(dependencies, codeLocation());
  }
}
