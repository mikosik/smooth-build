package org.smoothbuild.function.def;

import java.util.Map;

import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.Task;
import org.smoothbuild.task.TaskGenerator;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.hash.HashCode;

public class CallNode implements DefinitionNode {
  private final Function function;
  private final ImmutableMap<String, DefinitionNode> args;
  private final CodeLocation codeLocation;

  public CallNode(Function function, CodeLocation codeLocation, Map<String, DefinitionNode> args) {
    this.function = function;
    this.codeLocation = codeLocation;
    this.args = ImmutableMap.copyOf(args);
  }

  @Override
  public Type type() {
    return function.type();
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    Builder<String, HashCode> builder = ImmutableMap.builder();
    for (Map.Entry<String, DefinitionNode> entry : args.entrySet()) {
      String argName = entry.getKey();
      HashCode hash = taskGenerator.generateTask(entry.getValue());
      builder.put(argName, hash);
    }
    ImmutableMap<String, HashCode> argumentHashes = builder.build();
    return function.generateTask(taskGenerator, argumentHashes, codeLocation);
  }
}
