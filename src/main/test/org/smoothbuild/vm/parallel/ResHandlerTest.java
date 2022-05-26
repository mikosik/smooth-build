package org.smoothbuild.vm.parallel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.vm.compute.ResSource.DISK;
import static org.smoothbuild.vm.job.TaskKind.CALL;

import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;
import org.smoothbuild.vm.algorithm.Output;
import org.smoothbuild.vm.compute.CompRes;
import org.smoothbuild.vm.job.TaskInfo;

public class ResHandlerTest extends TestingContext {
  private ExecutionReporter reporter;
  private SoftTerminationExecutor executor;
  private Consumer<CnstB> consumer;
  private CnstB cnst;

  @BeforeEach
  @SuppressWarnings("unchecked")
  public void beforeEach() {
    reporter = mock(ExecutionReporter.class);
    executor = mock(SoftTerminationExecutor.class);
    consumer = mock(Consumer.class);
    cnst = stringB();
  }

  @Nested
  class when_output_with_value_is_passed {
    @Test
    public void object_is_forwarded_to_consumer() {
      ResHandler resHandler = new ResHandler(taskInfo(), consumer, reporter, executor);
      resHandler.accept(maybeComputed(cnst));
      verify(consumer, only()).accept(cnst);
    }

    @Test
    public void executor_is_not_stopped() {
      ResHandler resHandler = new ResHandler(taskInfo(), consumer, reporter, executor);
      resHandler.accept(maybeComputed(cnst));
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
      resHandler.accept(new CompRes(new ArithmeticException(), DISK));
      verifyNoInteractions(consumer);
    }

    @Test
    public void executor_is_stopped() {
      ResHandler resHandler = new ResHandler(taskInfo(), consumer, reporter, executor);
      resHandler.accept(new CompRes(new ArithmeticException(), DISK));
      verify(executor, only()).terminate();
    }
  }

  private CompRes maybeComputed(CnstB cnst) {
    return new CompRes(output(cnst), DISK);
  }

  private Output output(CnstB cnst) {
    return new Output(cnst, arrayB(stringTB()));
  }

  private TaskInfo taskInfo() {
    return new TaskInfo(CALL, "name", loc());
  }
}
