package org.smoothbuild.exec.task.parallel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.exec.comp.MaybeOutput;
import org.smoothbuild.exec.comp.Output;
import org.smoothbuild.exec.task.base.MaybeComputed;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;

public class ResultHandlerTest {
  private ExecutionReporter reporter;
  private SoftTerminationExecutor executor;
  private Consumer<SObject> consumer;
  private SObject sObject;

  @BeforeEach
  @SuppressWarnings("unchecked")
  public void beforeEach() {
    reporter = mock(ExecutionReporter.class);
    executor = mock(SoftTerminationExecutor.class);
    consumer = mock(Consumer.class);
    sObject = mock(SObject.class);
  }

  @Nested
  class when_output_with_value_is_passed {
    @Test
    public void object_is_forwarded_to_consumer() {
      ResultHandler resultHandler = new ResultHandler(task(), consumer, reporter, executor);
      resultHandler.accept(maybeComputed(sObject));
      verify(consumer, only()).accept(sObject);
    }

    @Test
    public void executor_is_not_stopped() {
      ResultHandler resultHandler = new ResultHandler(task(), consumer, reporter, executor);
      resultHandler.accept(maybeComputed(sObject));
      verifyNoInteractions(executor);
    }
  }

  @Nested
  class when_output_without_value_is_passed {
    @Test
    public void object_is_not_forwarded_to_consumer() {
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
    public void object_is_not_forwarded_to_consumer() {
      ResultHandler resultHandler = new ResultHandler(task(), consumer, reporter, executor);
      resultHandler.accept(new MaybeComputed(new MaybeOutput(new ArithmeticException()), true));
      verifyNoInteractions(consumer);
    }

    @Test
    public void executor_is_stopped() {
      ResultHandler resultHandler = new ResultHandler(task(), consumer, reporter, executor);
      resultHandler.accept(new MaybeComputed(new MaybeOutput(new ArithmeticException()), true));
      verify(executor, only()).terminate();
    }
  }

  @Nested
  class when_maybe_computed_with_exception_is_passed {
    @Test
    public void object_is_not_forwarded_to_consumer() {
      ResultHandler resultHandler = new ResultHandler(task(), consumer, reporter, executor);
      resultHandler.accept(new MaybeComputed(new ArithmeticException()));
      verifyNoInteractions(consumer);
    }

    @Test
    public void executor_is_stopped() {
      ResultHandler resultHandler = new ResultHandler(task(), consumer, reporter, executor);
      resultHandler.accept(new MaybeComputed(new ArithmeticException()));
      verify(executor, only()).terminate();
    }
  }

  @Nested
  class when_handle_computer_exception_is_called {
    @Test
    public void executor_is_stopped() {
      ResultHandler resultHandler = new ResultHandler(task(), consumer, reporter, executor);
      resultHandler.handleComputerException(new ArithmeticException());
      verify(executor, only()).terminate();
    }
  }

  private MaybeComputed maybeComputed(SObject sObject) {
    return new MaybeComputed(new MaybeOutput(output(sObject)), true);
  }

  private static Output output(SObject sObject) {
    return new Output(sObject, mock(Array.class));
  }

  private Task task() {
    return mock(Task.class);
  }
}
