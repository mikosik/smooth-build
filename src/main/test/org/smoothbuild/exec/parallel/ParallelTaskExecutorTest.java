package org.smoothbuild.exec.parallel;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.exec.compute.ResultSource.DISK;
import static org.smoothbuild.exec.compute.ResultSource.EXECUTION;
import static org.smoothbuild.exec.compute.ResultSource.MEMORY;
import static org.smoothbuild.exec.compute.TaskKind.CALL;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.exec.compute.AlgorithmTask;
import org.smoothbuild.exec.compute.Computed;
import org.smoothbuild.exec.compute.Computer;
import org.smoothbuild.exec.compute.ResultSource;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.exec.plan.TaskSupplier;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;

public class ParallelTaskExecutorTest extends TestingContext {
  private ParallelTaskExecutor parallelTaskExecutor;
  private ExecutionReporter reporter;

  @BeforeEach
  public void before() {
    reporter = mock(ExecutionReporter.class);
    parallelTaskExecutor = new ParallelTaskExecutor(computer(), reporter, 4);
  }

  @Test
  public void tasks_are_executed() throws Exception {
    Task task = concat(
        concat(
            task(valueAlgorithm("A")),
            task(valueAlgorithm("B"))),
        concat(
            task(valueAlgorithm("C")),
            task(valueAlgorithm("D"))));

    assertThat(executeSingleTask(task))
        .isEqualTo(string("((A,B),(C,D))"));
  }

  @Test
  public void tasks_are_executed_in_parallel() throws Exception {
    AtomicInteger counterA = new AtomicInteger(10);
    AtomicInteger counterB = new AtomicInteger(20);
    Task task = concat(
        task(sleepyWriteReadAlgorithm(Hash.of(102), counterB, counterA)),
        task(sleepyWriteReadAlgorithm(Hash.of(101), counterA, counterB)));

    assertThat(executeSingleTask(task))
        .isEqualTo(string("(11,21)"));
  }

  @Test
  public void task_execution_waits_and_reuses_result_of_task_with_equal_hash_that_is_being_executed()
      throws Exception {
    parallelTaskExecutor = new ParallelTaskExecutor(computer(), reporter, 4);
    AtomicInteger counter = new AtomicInteger();
    Task task1 = task(sleepGetIncrementAlgorithm(counter));
    Task task2 = task(sleepGetIncrementAlgorithm(counter));
    Task task3 = task(sleepGetIncrementAlgorithm(counter));
    Task task4 = task(sleepGetIncrementAlgorithm(counter));
    Task task = concat(task1, task2, task3, task4);

    assertThat(executeSingleTask(task))
        .isEqualTo(string("(0,0,0,0)"));

    ArgumentCaptor<Computed> captor = ArgumentCaptor.forClass(Computed.class);
    verify(reporter, times(1)).report(eq(task1), captor.capture());
    verify(reporter, times(1)).report(eq(task2), captor.capture());
    verify(reporter, times(1)).report(eq(task3), captor.capture());
    verify(reporter, times(1)).report(eq(task4), captor.capture());
    List<ResultSource> reportedSources = captor.getAllValues()
        .stream()
        .map(Computed::resultSource)
        .collect(toList());

    assertThat(reportedSources)
        .containsExactly(DISK, DISK, DISK, EXECUTION);
  }

  @Nested
  class _result_source_for_computation_of_ {
    @Test
    public void impure_function_is_memory()
        throws Exception {
      parallelTaskExecutor = new ParallelTaskExecutor(computer(), reporter, 2);
      AtomicInteger counter = new AtomicInteger();
      Task task1 = task(sleepGetIncrementAlgorithm(counter, false));
      Task task2 = task(sleepGetIncrementAlgorithm(counter, false));
      Task task = concat(task1, task2);

      assertThat(executeSingleTask(task))
          .isEqualTo(string("(0,0)"));

      ArgumentCaptor<Computed> captor = ArgumentCaptor.forClass(Computed.class);
      verify(reporter, times(1)).report(eq(task1), captor.capture());
      verify(reporter, times(1)).report(eq(task2), captor.capture());
      List<ResultSource> reportedSources = captor.getAllValues()
          .stream()
          .map(Computed::resultSource)
          .collect(toList());

      assertThat(reportedSources)
          .containsExactly(MEMORY, EXECUTION);
    }

    @Test
    public void pure_function_is_disk()
        throws Exception {
      parallelTaskExecutor = new ParallelTaskExecutor(computer(), reporter, 2);
      AtomicInteger counter = new AtomicInteger();
      Task task1 = task(sleepGetIncrementAlgorithm(counter));
      Task task2 = task(sleepGetIncrementAlgorithm(counter));
      Task task = concat(task1, task2);

      assertThat(executeSingleTask(task))
          .isEqualTo(string("(0,0)"));

      ArgumentCaptor<Computed> captor = ArgumentCaptor.forClass(Computed.class);
      verify(reporter, times(1)).report(eq(task1), captor.capture());
      verify(reporter, times(1)).report(eq(task2), captor.capture());
      List<ResultSource> reportedSources = captor.getAllValues()
          .stream()
          .map(Computed::resultSource)
          .collect(toList());

      assertThat(reportedSources)
          .containsExactly(DISK, EXECUTION);
    }
  }

  @Test
  public void waiting_for_result_of_other_task_with_equal_hash_doesnt_block_executor_thread()
      throws Exception {
    parallelTaskExecutor = new ParallelTaskExecutor(computer(), reporter, 2);
    AtomicInteger counter = new AtomicInteger();
    Task task = concat(
        task(sleepGetIncrementAlgorithm(counter)),
        task(sleepGetIncrementAlgorithm(counter)),
        task(getIncrementAlgorithm(counter)));

    assertThat(executeSingleTask(task))
        .isEqualTo(string("(1,1,0)"));
  }

  @Test
  public void task_throwing_runtime_exception_causes_error() throws Exception {
    Reporter reporter = mock(Reporter.class);
    parallelTaskExecutor = new ParallelTaskExecutor(computer(), new ExecutionReporter(reporter), 4);
    ArithmeticException exception = new ArithmeticException();
    Task task = task(throwingAlgorithm(exception));

    assertThat(parallelTaskExecutor.executeAll(list(task)).get(task))
        .isNull();
    verify(reporter).report(
        eq(task),
        eq("task-name                                smooth internal"),
        eq(list(error("Execution failed with:\n" + getStackTraceAsString(exception)))));
  }

  @Test
  public void computer_that_throws_exception_is_detected() throws InterruptedException {
    RuntimeException exception = new RuntimeException();
    Computer computer = new Computer(null, null, null) {
      @Override
      public void compute(AlgorithmTask task, Input input, Consumer<Computed> consumer) {
        throw exception;
      }
    };
    parallelTaskExecutor = new ParallelTaskExecutor(computer, reporter);
    Task task = task(valueAlgorithm("A"));

    Obj object = parallelTaskExecutor.executeAll(list(task)).get(task);

    verify(reporter, only()).reportComputerException(same(task), same(exception));
    assertThat(object).isNull();
  }

  private Task concat(Task... dependencies) {
    return task(concatAlgorithm(), list(dependencies));
  }

  private Algorithm concatAlgorithm() {
    return new TestAlgorithm(Hash.of(1)) {
      @Override
      public Output run(Input input, NativeApi nativeApi) {
        String joinedArgs = input.objects()
            .stream()
            .map(o -> ((Str) o).jValue())
            .collect(joining(","));
        Str result = nativeApi.factory().string("(" + joinedArgs + ")");
        return new Output(result, nativeApi.messages());
      }
    };
  }

  private Algorithm valueAlgorithm(String value) {
    return new TestAlgorithm(Hash.of(asList(Hash.of(2), Hash.of(value)))) {
      @Override
      public Output run(Input input, NativeApi nativeApi) {
        Str result = nativeApi.factory().string(value);
        return new Output(result, nativeApi.messages());
      }
    };
  }

  private Algorithm throwingAlgorithm(ArithmeticException exception) {
    return new TestAlgorithm(Hash.of(3)) {
      @Override
      public Output run(Input input, NativeApi nativeApi) {
        throw exception;
      }
    };
  }

  private Algorithm sleepGetIncrementAlgorithm(AtomicInteger counter) {
    return sleepGetIncrementAlgorithm(counter, true);
  }

  private Algorithm sleepGetIncrementAlgorithm(AtomicInteger counter, boolean isPure) {
    return new TestAlgorithm(Hash.of(4), isPure) {
      @Override
      public Output run(Input input, NativeApi nativeApi) {
        sleep1000ms();
        return toStr(nativeApi, counter.getAndIncrement());
      }
    };
  }

  private Algorithm getIncrementAlgorithm(AtomicInteger counter) {
    return new TestAlgorithm(Hash.of(5)) {
      @Override
      public Output run(Input input, NativeApi nativeApi) {
        return toStr(nativeApi, counter.getAndIncrement());
      }
    };
  }

  private Algorithm sleepyWriteReadAlgorithm(
      Hash hash, AtomicInteger write, AtomicInteger read) {
    return new TestAlgorithm(hash) {
      @Override
      public Output run(Input input, NativeApi nativeApi) {
        write.incrementAndGet();
        sleep1000ms();
        return toStr(nativeApi, read.get());
      }
    };
  }

  private Obj executeSingleTask(Task task) throws InterruptedException {
    return executeSingleTask(parallelTaskExecutor, task);
  }

  private static Obj executeSingleTask(ParallelTaskExecutor parallelTaskExecutor, Task task)
      throws InterruptedException {
    return parallelTaskExecutor.executeAll(list(task)).get(task);
  }

  private static Task task(Algorithm algorithm) {
    return task(algorithm, list());
  }

  private static Task task(Algorithm algorithm, List<Task> dependencies) {
    var suppliers = map(dependencies, d -> new TaskSupplier(d.type(), d.location(), () -> d));
    return new AlgorithmTask(CALL, STRING, "task-name", algorithm, suppliers, internal());
  }

  private static Output toStr(NativeApi nativeApi, int i) {
    return new Output(nativeApi.factory().string(Integer.toString(i)), nativeApi.messages());
  }

  private static void sleep1000ms() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private abstract class TestAlgorithm extends Algorithm {
    private final Hash hash;

    protected TestAlgorithm(Hash hash) {
      this(hash, true);
    }

    protected TestAlgorithm(Hash hash, boolean isPure) {
      super(stringSpec(), isPure);
      this.hash = hash;
    }

    @Override
    public Hash hash() {
      return hash;
    }

    @Override
    public Spec outputSpec() {
      return stringSpec();
    }
  }
}
