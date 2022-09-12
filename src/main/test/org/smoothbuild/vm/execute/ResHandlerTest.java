package org.smoothbuild.vm.execute;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.vm.compute.ResSource.DISK;
import static org.smoothbuild.vm.execute.TaskKind.CALL;

import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;
import org.smoothbuild.vm.algorithm.Output;
import org.smoothbuild.vm.compute.CompRes;

public class ResHandlerTest extends TestContext {
  private ExecutionReporter reporter;
  private SoftTerminationExecutor executor;
  private Consumer<ValB> consumer;
  private ValB val;

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
      ResHandler resHandler = new ResHandler(taskInfo(), executor, reporter, consumer);
      resHandler.accept(maybeComputed(val));
      verify(consumer, only()).accept(val);
    }

    @Test
    public void executor_is_not_stopped() {
      ResHandler resHandler = new ResHandler(taskInfo(), executor, reporter, consumer);
      resHandler.accept(maybeComputed(val));
      verifyNoInteractions(executor);
    }
  }

  @Nested
  class when_output_without_value_is_passed {
    @Test
    public void object_is_not_forwarded_to_consumer() {
      ResHandler resHandler = new ResHandler(taskInfo(), executor, reporter, consumer);
      resHandler.accept(maybeComputed(null));
      verifyNoInteractions(consumer);
    }

    @Test
    public void executor_is_stopped() {
      ResHandler resHandler = new ResHandler(taskInfo(), executor, reporter, consumer);
      resHandler.accept(maybeComputed(null));
      verify(executor, only()).terminate();
    }
  }

  @Nested
  class when_maybe_output_with_exception_is_passed {
    @Test
    public void object_is_not_forwarded_to_consumer() {
      ResHandler resHandler = new ResHandler(taskInfo(), executor, reporter, consumer);
      resHandler.accept(new CompRes(new ArithmeticException(), DISK));
      verifyNoInteractions(consumer);
    }

    @Test
    public void executor_is_stopped() {
      ResHandler resHandler = new ResHandler(taskInfo(), executor, reporter, consumer);
      resHandler.accept(new CompRes(new ArithmeticException(), DISK));
      verify(executor, only()).terminate();
    }
  }

  private CompRes maybeComputed(ValB val) {
    return new CompRes(output(val), DISK);
  }

  private Output output(ValB val) {
    return new Output(val, arrayB(stringTB()));
  }

  private TaskInfo taskInfo() {
    return new TaskInfo(CALL, "name", loc());
  }
}
