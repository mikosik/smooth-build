package org.smoothbuild.vm.execute;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.vm.compute.ResultSource.DISK;

import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;
import org.smoothbuild.vm.compute.ComputationResult;
import org.smoothbuild.vm.task.Output;

public class ResHandlerTest extends TestContext {
  private ExecutionReporter reporter;
  private SoftTerminationExecutor executor;
  private Consumer<InstB> consumer;
  private InstB val;

  @BeforeEach
  @SuppressWarnings("unchecked")
  public void beforeEach() {
    reporter = mock(ExecutionReporter.class);
    executor = mock(SoftTerminationExecutor.class);
    consumer = mock(Consumer.class);
    val = stringB();
  }

  @Nested
  class when_output_with_value_is_passed {
    @Test
    public void object_is_forwarded_to_consumer() {
      ResHandler resHandler = new ResHandler(task(), executor, reporter, consumer);
      resHandler.accept(result(val));
      verify(consumer, only()).accept(val);
    }

    @Test
    public void executor_is_not_stopped() {
      ResHandler resHandler = new ResHandler(task(), executor, reporter, consumer);
      resHandler.accept(result(val));
      verifyNoInteractions(executor);
    }
  }

  @Nested
  class when_output_without_value_is_passed {
    @Test
    public void object_is_not_forwarded_to_consumer() {
      ResHandler resHandler = new ResHandler(task(), executor, reporter, consumer);
      resHandler.accept(result(null));
      verifyNoInteractions(consumer);
    }

    @Test
    public void executor_is_stopped() {
      ResHandler resHandler = new ResHandler(task(), executor, reporter, consumer);
      resHandler.accept(result(null));
      verify(executor, only()).terminate();
    }
  }

  @Nested
  class when_maybe_output_with_exception_is_passed {
    @Test
    public void object_is_not_forwarded_to_consumer() {
      ResHandler resHandler = new ResHandler(task(), executor, reporter, consumer);
      resHandler.accept(new ComputationResult(new ArithmeticException(), DISK));
      verifyNoInteractions(consumer);
    }

    @Test
    public void executor_is_stopped() {
      ResHandler resHandler = new ResHandler(task(), executor, reporter, consumer);
      resHandler.accept(new ComputationResult(new ArithmeticException(), DISK));
      verify(executor, only()).terminate();
    }
  }

  private ComputationResult result(InstB val) {
    return new ComputationResult(output(val), DISK);
  }

  private Output output(InstB val) {
    return new Output(val, arrayB(stringTB()));
  }
}
