package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.convert.Converter;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.ConvertWorker;
import org.smoothbuild.task.base.TaskWorker;

import com.google.common.collect.ImmutableList;

public class ConvertExpr<S extends SValue, T extends SValue> extends Expr<T> {
  private final Converter<S, T> converter;

  public ConvertExpr(Expr<S> expr, Converter<S, T> converter, CodeLocation codeLocation) {
    super(converter.targetType(), ImmutableList.of(expr), codeLocation);
    this.converter = converter;
  }

  @Override
  public TaskWorker<T> createWorker() {
    return new ConvertWorker<>(converter, codeLocation());
  }
}
