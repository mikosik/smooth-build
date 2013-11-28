package org.smoothbuild.lang.function.def;

import org.smoothbuild.lang.convert.Converter;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.ConvertTask;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class ConvertNode extends Node {
  private final Node node;
  private final Converter<?> converter;

  public ConvertNode(Node node, Converter<?> converter, CodeLocation codeLocation) {
    super(converter.targetType(), codeLocation);
    this.node = node;
    this.converter = converter;
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    Result task = taskGenerator.generateTask(node);
    return new ConvertTask(task, converter, codeLocation());
  }
}
