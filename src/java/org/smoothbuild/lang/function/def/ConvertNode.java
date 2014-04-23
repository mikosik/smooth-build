package org.smoothbuild.lang.function.def;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.convert.Converter;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.ConvertWorker;
import org.smoothbuild.task.base.TaskWorker;

import com.google.common.collect.ImmutableList;

public class ConvertNode<S extends SValue, T extends SValue> extends Node<T> {
  private final Converter<S, T> converter;

  public ConvertNode(Node<S> node, Converter<S, T> converter, CodeLocation codeLocation) {
    super(converter.targetType(), ImmutableList.of(node), codeLocation);
    this.converter = converter;
  }

  @Override
  public TaskWorker<T> createWorker() {
    return new ConvertWorker<>(converter, codeLocation());
  }
}
