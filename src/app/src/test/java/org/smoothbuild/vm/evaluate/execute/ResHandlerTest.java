package org.smoothbuild.vm.evaluate.execute;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.concurrent.SoftTerminationExecutor;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;

public class ResHandlerTest extends TestContext {
  private TaskReporter reporter;
  private SoftTerminationExecutor executor;
  private Consumer<ValueB> consumer;
  private ValueB value;

  @BeforeEach
  @SuppressWarnings("unchecked")
  public void beforeEach() throws Exception {
    reporter = mock(TaskReporter.class);
    executor = mock(SoftTerminationExecutor.class);
    consumer = mock(Consumer.class);
    value = stringB();
  }

  @Nested
  class when_output_with_value_is_passed {
    @Test
    public void object_is_forwarded_to_consumer() throws Exception {
      ResHandler resHandler = new ResHandler(task(), executor, reporter, consumer);
      resHandler.accept(computationResult(value));
      verify(consumer, only()).accept(value);
    }

    @Test
    public void executor_is_not_stopped() throws Exception {
      ResHandler resHandler = new ResHandler(task(), executor, reporter, consumer);
      resHandler.accept(computationResult(value));
      verifyNoInteractions(executor);
    }
  }

  @Nested
  class when_output_without_value_is_passed {
    @Test
    public void object_is_not_forwarded_to_consumer() throws Exception {
      ResHandler resHandler = new ResHandler(task(), executor, reporter, consumer);
      resHandler.accept(computationResult(null));
      verifyNoInteractions(consumer);
    }

    @Test
    public void executor_is_stopped() throws Exception {
      ResHandler resHandler = new ResHandler(task(), executor, reporter, consumer);
      resHandler.accept(computationResult(null));
      verify(executor, only()).terminate();
    }
  }
}
