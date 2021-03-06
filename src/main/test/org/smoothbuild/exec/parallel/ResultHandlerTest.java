package org.smoothbuild.exec.parallel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.exec.compute.ResultSource.CACHE;

import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.exec.base.MaybeOutput;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.exec.compute.Computed;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;

public class ResultHandlerTest {
  private ExecutionReporter reporter;
  private SoftTerminationExecutor executor;
  private Consumer<Record> consumer;
  private Record record;

  @BeforeEach
  @SuppressWarnings("unchecked")
  public void beforeEach() {
    reporter = mock(ExecutionReporter.class);
    executor = mock(SoftTerminationExecutor.class);
    consumer = mock(Consumer.class);
    record = mock(Record.class);
  }

  @Nested
  class when_output_with_value_is_passed {
    @Test
    public void record_is_forwarded_to_consumer() {
      ResultHandler resultHandler = new ResultHandler(task(), consumer, reporter, executor);
      resultHandler.accept(maybeComputed(record));
      verify(consumer, only()).accept(record);
    }

    @Test
    public void executor_is_not_stopped() {
      ResultHandler resultHandler = new ResultHandler(task(), consumer, reporter, executor);
      resultHandler.accept(maybeComputed(record));
      verifyNoInteractions(executor);
    }
  }

  @Nested
  class when_output_without_value_is_passed {
    @Test
    public void record_is_not_forwarded_to_consumer() {
      ResultHandler resultHandler = new ResultHandler(task(), consumer, reporter, executor);
      resultHandler.accept(maybeComputed(null));
      verifyNoInteractions(consumer);
    }

    @Test
    public void executor_is_stopped() {
      ResultHandler resultHandler = new ResultHandler(task(), consumer, reporter, executor);
      resultHandler.accept(maybeComputed(null));
      verify(executor, only()).terminate();
    }
  }

  @Nested
  class when_maybe_output_with_exception_is_passed {
    @Test
    public void record_is_not_forwarded_to_consumer() {
      ResultHandler resultHandler = new ResultHandler(task(), consumer, reporter, executor);
      resultHandler.accept(new Computed(new MaybeOutput(new ArithmeticException()), CACHE));
      verifyNoInteractions(consumer);
    }

    @Test
    public void executor_is_stopped() {
      ResultHandler resultHandler = new ResultHandler(task(), consumer, reporter, executor);
      resultHandler.accept(new Computed(new MaybeOutput(new ArithmeticException()), CACHE));
      verify(executor, only()).terminate();
    }
  }

  private Computed maybeComputed(Record record) {
    return new Computed(new MaybeOutput(output(record)), CACHE);
  }

  private static Output output(Record record) {
    return new Output(record, mock(Array.class));
  }

  private Task task() {
    return mock(Task.class);
  }
}
