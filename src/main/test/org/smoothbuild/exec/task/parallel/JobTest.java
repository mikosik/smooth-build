package org.smoothbuild.exec.task.parallel;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.smoothbuild.exec.comp.ComputationException;
import org.smoothbuild.exec.comp.Output;
import org.smoothbuild.exec.task.base.Result;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;

public class JobTest {
  @Test
  public void initially_task_result_is_null() {
    Job job = new Job(null);
    assertThat(job.taskResult())
        .isNull();
  }

  @Test
  public void task_result_returns_value_set_by_set_task_result() {
    Job job = new Job(null);
    Result result = mock(Result.class);
    job.setTaskResult(result);

    assertThat(job.taskResult())
        .isSameInstanceAs(result);
  }

  @Test
  public void value_consumer_is_invoked_whe_task_result_with_output_is_set() {
    Job job = new Job(null);
    SObject sObject = mock(SObject.class);
    @SuppressWarnings("unchecked")
    Consumer<SObject> consumer = mock(Consumer.class);
    job.addValueConsumer(consumer);

    job.setTaskResult(taskResult(sObject));

    verify(consumer, only()).accept(same(sObject));
  }

  @Test
  public void value_available_listener_is_invoked_whe_task_result_with_output_is_set() {
    Job job = new Job(null);
    Runnable runnable = mock(Runnable.class);
    job.addValueAvailableListener(runnable);

    job.setTaskResult(taskResult(mock(SObject.class)));

    verify(runnable, only()).run();
  }

  @Test
  public void value_consumer_is_not_invoked_whe_task_result_without_output_is_set() {
    Job job = new Job(null);
    @SuppressWarnings("unchecked")
    Consumer<SObject> consumer = mock(Consumer.class);
    job.addValueConsumer(consumer);

    job.setTaskResult(new Result(new ComputationException(null)));

    verifyNoInteractions(consumer);
  }

  @Test
  public void value_available_listener_is_not_invoked_whe_task_result_without_output_is_set() {
    Job job = new Job(null);
    Runnable runnable = mock(Runnable.class);
    job.addValueAvailableListener(runnable);

    job.setTaskResult(new Result(new ComputationException(null)));

    verifyNoInteractions(runnable);
  }

  private static Result taskResult(SObject sObject) {
    return new Result(new Output(sObject, mock(Array.class)), false);
  }
}
