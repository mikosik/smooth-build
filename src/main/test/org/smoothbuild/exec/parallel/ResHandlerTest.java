package org.smoothbuild.exec.parallel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.exec.compute.ResSource.DISK;
import static org.smoothbuild.exec.job.TaskKind.CALL;

import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.val.ValB;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.exec.compute.Computed;
import org.smoothbuild.exec.job.TaskInfo;
import org.smoothbuild.lang.base.define.TestingLoc;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;

public class ResHandlerTest extends TestingContext {
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
      ResHandler resHandler = new ResHandler(taskInfo(), consumer, reporter, executor);
      resHandler.accept(maybeComputed(val));
      verify(consumer, only()).accept(val);
    }

    @Test
    public void executor_is_not_stopped() {
      ResHandler resHandler = new ResHandler(taskInfo(), consumer, reporter, executor);
      resHandler.accept(maybeComputed(val));
      verifyNoInteractions(executor);
    }
  }

  @Nested
  class when_output_without_value_is_passed {
    @Test
    public void object_is_not_forwarded_to_consumer() {
      ResHandler resHandler = new ResHandler(taskInfo(), consumer, reporter, executor);
      resHandler.accept(maybeComputed(null));
      verifyNoInteractions(consumer);
    }

    @Test
    public void executor_is_stopped() {
      ResHandler resHandler = new ResHandler(taskInfo(), consumer, reporter, executor);
      resHandler.accept(maybeComputed(null));
      verify(executor, only()).terminate();
    }
  }

  @Nested
  class when_maybe_output_with_exception_is_passed {
    @Test
    public void object_is_not_forwarded_to_consumer() {
      ResHandler resHandler = new ResHandler(taskInfo(), consumer, reporter, executor);
      resHandler.accept(new Computed(new ArithmeticException(), DISK));
      verifyNoInteractions(consumer);
    }

    @Test
    public void executor_is_stopped() {
      ResHandler resHandler = new ResHandler(taskInfo(), consumer, reporter, executor);
      resHandler.accept(new Computed(new ArithmeticException(), DISK));
      verify(executor, only()).terminate();
    }
  }

  private Computed maybeComputed(ValB val) {
    return new Computed(output(val), DISK);
  }

  private Output output(ValB val) {
    return new Output(val, arrayB(stringTB()));
  }

  private TaskInfo taskInfo() {
    return new TaskInfo(CALL, "name", TestingLoc.loc());
  }
}
