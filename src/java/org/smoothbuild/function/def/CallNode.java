package org.smoothbuild.function.def;

import java.util.Map;

import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class CallNode extends AbstractDefinitionNode {
  private final Function function;
  private final ImmutableMap<String, DefinitionNode> args;

  public CallNode(Function function, CodeLocation codeLocation, Map<String, DefinitionNode> args) {
    super(CallLocation.callLocation(function.name().value(), codeLocation));
    this.function = function;
    this.args = ImmutableMap.copyOf(args);
  }

  @Override
  public Type type() {
    return function.type();
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    Builder<String, Result> builder = ImmutableMap.builder();
    for (Map.Entry<String, DefinitionNode> entry : args.entrySet()) {
      String argName = entry.getKey();
      Result dependency = taskGenerator.generateTask(entry.getValue());
      builder.put(argName, dependency);
    }
    ImmutableMap<String, Result> dependencies = builder.build();
    return function.generateTask(taskGenerator, dependencies);
  }
}
