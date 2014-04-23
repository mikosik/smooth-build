package org.smoothbuild.task.base;

import org.smoothbuild.db.taskresults.TaskResult;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.convert.Converter;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.NativeApiImpl;

import com.google.common.hash.HashCode;

public class ConvertWorker<S extends SValue, T extends SValue> extends TaskWorker<T> {
  private final Converter<S, T> converter;

  public ConvertWorker(Converter<S, T> converter, CodeLocation codeLocation) {
    super(workerHash(converter), converter.targetType(), converter.name(), true, true, codeLocation);
    this.converter = converter;
  }

  private static HashCode workerHash(Converter<?, ?> converter) {
    return WorkerHashes.workerHash(ConvertWorker.class, converter.hash());

  }

  @Override
  public TaskResult<T> execute(Iterable<? extends SValue> input, NativeApiImpl nativeApi) {
    @SuppressWarnings("unchecked")
    S value = (S) input.iterator().next();
    T result = converter.convert(nativeApi, value);
    return new TaskResult<>(result);
  }
}
