package org.smoothbuild.lang.function.def;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.convert.Converter;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.ConvertTask;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class ConvertNode<S extends SValue, T extends SValue> extends Node<T> {
  private final Node<S> node;
  private final Converter<S, T> converter;

  public ConvertNode(Node<S> node, Converter<S, T> converter, CodeLocation codeLocation) {
    super(converter.targetType(), codeLocation);
    this.node = node;
    this.converter = converter;
  }

  @Override
  public Task<T> generateTask(TaskGenerator taskGenerator) {
    Result<S> task = taskGenerator.generateTask(node);
    return new ConvertTask<>(task, converter, codeLocation());
  }
}
