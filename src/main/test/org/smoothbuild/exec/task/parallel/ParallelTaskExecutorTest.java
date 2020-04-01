package org.smoothbuild.exec.task.parallel;

import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.joining;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.comp.Computation;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.comp.Output;
import org.smoothbuild.exec.task.base.ExecutionResult;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.exec.task.base.TaskExecutor;
import org.smoothbuild.exec.task.base.TaskResult;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class ParallelTaskExecutorTest extends TestingContext {
  private ParallelTaskExecutor parallelTaskExecutor;
  private ExecutionReporter reporter;

  @BeforeEach
  public void before() {
    reporter = mock(ExecutionReporter.class);
    parallelTaskExecutor = new ParallelTaskExecutor(taskExecutor(), reporter, 4);
  }

  @Test
  public void tasks_are_executed() throws Exception {
    Task task = concat(
        concat(
            task(valueComputation("A")),
            task(valueComputation("B"))),
        concat(
            task(valueComputation("C")),
            task(valueComputation("D"))));

    assertThat(executeSingleTask(task))
        .isEqualTo(toOutput("((A,B),(C,D))"));
  }

  @Test
  public void tasks_are_executed_in_parallel() throws Exception {
    AtomicInteger counterA = new AtomicInteger(10);
    AtomicInteger counterB = new AtomicInteger(20);
    Task task = concat(
        task(sleepyWriteReadComputation(Hash.of(102), counterB, counterA)),
        task(sleepyWriteReadComputation(Hash.of(101), counterA, counterB)));

    assertThat(executeSingleTask(task))
        .isEqualTo(toOutput("(11,21)"));
  }

  @Test
  public void task_execution_waits_and_reuses_result_of_task_with_equal_hash_that_is_being_executed()
      throws Exception {
    parallelTaskExecutor = new ParallelTaskExecutor(taskExecutor(), reporter, 4);
    AtomicInteger counter = new AtomicInteger();
    Task task = concat(
        task(sleepGetIncrementComputation(counter)),
        task(sleepGetIncrementComputation(counter)),
        task(sleepGetIncrementComputation(counter)),
        task(sleepGetIncrementComputation(counter)));

    assertThat(executeSingleTask(task))
        .isEqualTo(toOutput("(0,0,0,0)"));
  }

  @Test
  public void waiting_for_result_of_other_task_with_equal_hash_doesnt_block_executor_thread()
      throws Exception {
    parallelTaskExecutor = new ParallelTaskExecutor(taskExecutor(), reporter, 2);
    AtomicInteger counter = new AtomicInteger();
    Task task = concat(
        task(sleepGetIncrementComputation(counter)),
        task(sleepGetIncrementComputation(counter)),
        task(getIncrementComputation(counter)));

    assertThat(executeSingleTask(task))
        .isEqualTo(toOutput("(1,1,0)"));
  }

  @Test
  public void task_throwing_runtime_exception_causes_error() throws Exception {
    ArithmeticException exception = new ArithmeticException();
    Task task = task(throwingComputation(exception));
    assertThat(parallelTaskExecutor.executeAll(list(task)).get(task))
        .isNull();
    verify(reporter).report(same(exception));
  }

  @Test
  public void task_executor_that_throws_exception_is_detected() throws InterruptedException {
    RuntimeException exception = new RuntimeException();
    TaskExecutor taskExecutor = new TaskExecutor(null, null, null) {
      @Override
      public void execute(Task task, Input input, Consumer<ExecutionResult> consumer) {
        throw exception;
      }
    };
    parallelTaskExecutor = new ParallelTaskExecutor(taskExecutor, reporter);
    Task task = task(valueComputation("A"));

    TaskResult result = parallelTaskExecutor.executeAll(list(task)).get(task);

    verify(reporter, only()).report(same(exception));
    assertThat(result).isNull();
  }

  private Task concat(Task... dependencies) {
    return task(concatComputation(), list(dependencies));
  }

  private Computation concatComputation() {
    return new TestComputation("concat", Hash.of(1)) {
      @Override
      public Output execute(Input input, NativeApi nativeApi) {
        String joinedArgs = input.objects()
            .stream()
            .map(o -> ((SString) o).jValue())
            .collect(joining(","));
        SString result = nativeApi.factory().string("(" + joinedArgs + ")");
        return new Output(result, nativeApi.messages());
      }
    };
  }

  private Computation valueComputation(String value) {
    return new TestComputation("value", Hash.of(Hash.of(2), Hash.of(value))) {
      @Override
      public Output execute(Input input, NativeApi nativeApi) {
        SString result = nativeApi.factory().string(value);
        return new Output(result, nativeApi.messages());
      }
    };
  }

  private Computation throwingComputation(ArithmeticException exception) {
    return new TestComputation("runtimeException", Hash.of(3)) {
      @Override
      public Output execute(Input input, NativeApi nativeApi) {
        throw exception;
      }
    };
  }

  private Computation sleepGetIncrementComputation(AtomicInteger counter) {
    return new TestComputation("sleepyCounter", Hash.of(4)) {
      @Override
      public Output execute(Input input, NativeApi nativeApi) {
        sleep1000ms();
        return toSString(nativeApi, counter.getAndIncrement());
      }
    };
  }

  private Computation getIncrementComputation(AtomicInteger counter) {
    return new TestComputation("sleepyCounter", Hash.of(5)) {
      @Override
      public Output execute(Input input, NativeApi nativeApi) {
        return toSString(nativeApi, counter.getAndIncrement());
      }
    };
  }

  private Computation sleepyWriteReadComputation(
      Hash hash, AtomicInteger write, AtomicInteger read) {
    return new TestComputation("sleepyWriteRead", hash) {
      @Override
      public Output execute(Input input, NativeApi nativeApi) {
        write.incrementAndGet();
        sleep1000ms();
        return toSString(nativeApi, read.get());
      }
    };
  }

  private Output executeSingleTask(Task task) throws InterruptedException {
    return executeSingleTask(parallelTaskExecutor, task);
  }

  private static Output executeSingleTask(ParallelTaskExecutor parallelTaskExecutor, Task task)
      throws InterruptedException {
    return parallelTaskExecutor.executeAll(list(task)).get(task).output();
  }

  private Task task(Computation computation) {
    return task(computation, ImmutableList.of());
  }

  private static Task task(Computation computation, List<Task> dependencies) {
    return new Task(computation, dependencies, unknownLocation(), true);
  }

  private static Output toSString(NativeApi nativeApi, int i) {
    return new Output(nativeApi.factory().string(Integer.toString(i)), nativeApi.messages());
  }

  private Output toOutput(String string) {
    return new Output(string(string), emptyMessageArray());
  }

  private static void sleep1000ms() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private abstract class TestComputation implements Computation {
    private final String name;
    private final Hash hash;

    protected TestComputation(String name, Hash hash) {
      this.name = name;
      this.hash = hash;
    }

    @Override
    public String name() {
      return name;
    }

    @Override
    public Hash hash() {
      return hash;
    }

    @Override
    public ConcreteType type() {
      return stringType();
    }
  }
}
