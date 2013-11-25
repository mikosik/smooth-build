package org.smoothbuild.lang.function.def;

import java.util.Map;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.type.SType;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class CallNode extends Node {
  private final Function function;
  private final ImmutableMap<String, Node> args;

  public CallNode(Function function, CodeLocation codeLocation, Map<String, Node> args) {
    super(function.type(), codeLocation);
    this.function = function;
    this.args = ImmutableMap.copyOf(args);
  }

  @Override
  public SType<?> type() {
    return function.type();
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    Builder<String, Result> builder = ImmutableMap.builder();
    for (Map.Entry<String, Node> entry : args.entrySet()) {
      String argName = entry.getKey();
      Result dependency = taskGenerator.generateTask(entry.getValue());
      builder.put(argName, dependency);
    }
    ImmutableMap<String, Result> dependencies = builder.build();
    return function.generateTask(taskGenerator, dependencies, codeLocation());
  }
}
