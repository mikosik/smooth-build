package org.smoothbuild.exec.parallel;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.exec.compute.ResSource.DISK;
import static org.smoothbuild.exec.compute.ResSource.EXECUTION;
import static org.smoothbuild.exec.compute.ResSource.MEMORY;
import static org.smoothbuild.exec.job.TaskKind.CALL;
import static org.smoothbuild.exec.parallel.ExecutionReporter.header;
import static org.smoothbuild.lang.base.define.TestingLoc.loc;
import static org.smoothbuild.lang.base.type.TestingTsS.STRING;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.type.TestingCatsH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.exec.compute.Computed;
import org.smoothbuild.exec.compute.Computer;
import org.smoothbuild.exec.compute.ResSource;
import org.smoothbuild.exec.job.Job;
import org.smoothbuild.exec.job.Task;
import org.smoothbuild.exec.job.TaskInfo;
import org.smoothbuild.lang.expr.TopRefS;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;

public class ParallelJobExecutorTest extends TestingContext {
  private ParallelJobExecutor parallelJobExecutor;
  private ExecutionReporter reporter;

  @BeforeEach
  public void before() {
    reporter = mock(ExecutionReporter.class);
    parallelJobExecutor = new ParallelJobExecutor(computer(), reporter, 4);
  }

  @Test
  public void tasks_are_executed() throws Exception {
    var job = concat(
        concat(
            job(valueAlgorithm("A")),
            job(valueAlgorithm("B"))),
        concat(
            job(valueAlgorithm("C")),
            job(valueAlgorithm("D"))));

    assertThat(executeSingleJob(job))
        .isEqualTo(stringH("((A,B),(C,D))"));
  }

  @Test
  public void tasks_are_executed_in_parallel() throws Exception {
    var counterA = new AtomicInteger(10);
    var counterB = new AtomicInteger(20);
    var job = concat(
        job(sleepyWriteReadAlgorithm(Hash.of(102), counterB, counterA)),
        job(sleepyWriteReadAlgorithm(Hash.of(101), counterA, counterB)));

    assertThat(executeSingleJob(job))
        .isEqualTo(stringH("(11,21)"));
  }

  @Test
  public void task_execution_waits_and_reuses_result_of_task_with_equal_hash_that_is_being_executed()
      throws Exception {
    parallelJobExecutor = new ParallelJobExecutor(computer(), reporter, 4);
    var counter = new AtomicInteger();
    var job1 = job(sleepGetIncrementAlgorithm(counter));
    var job2 = job(sleepGetIncrementAlgorithm(counter));
    var job3 = job(sleepGetIncrementAlgorithm(counter));
    var job4 = job(sleepGetIncrementAlgorithm(counter));
    var job = concat(job1, job2, job3, job4);

    assertThat(executeSingleJob(job))
        .isEqualTo(stringH("(0,0,0,0)"));

    ArgumentCaptor<Computed> captor = ArgumentCaptor.forClass(Computed.class);
    ExecutionReporter reporter = this.reporter;
    verify(reporter, times(4)).report(eq(job1.info()), captor.capture());
    List<ResSource> reportedSources = captor.getAllValues()
        .stream()
        .map(Computed::resSource)
        .collect(toList());

    assertThat(reportedSources)
        .containsExactly(DISK, DISK, DISK, EXECUTION);
  }

  @Nested
  class _result_source_for_computation_of_ {
    @Test
    public void impure_func_is_memory()
        throws Exception {
      parallelJobExecutor = new ParallelJobExecutor(computer(), reporter, 2);
      var counter = new AtomicInteger();
      var job1 = job(sleepGetIncrementAlgorithm(counter, false));
      var job2 = job(sleepGetIncrementAlgorithm(counter, false));
      var job = concat(job1, job2);

      assertThat(executeSingleJob(job))
          .isEqualTo(stringH("(0,0)"));

      ArgumentCaptor<Computed> captor = ArgumentCaptor.forClass(Computed.class);
      verify(reporter, times(2)).report(eq(job1.info()), captor.capture());
      List<ResSource> reportedSources = captor.getAllValues()
          .stream()
          .map(Computed::resSource)
          .collect(toList());

      assertThat(reportedSources)
          .containsExactly(MEMORY, EXECUTION);
    }

    @Test
    public void pure_func_is_disk()
        throws Exception {
      parallelJobExecutor = new ParallelJobExecutor(computer(), reporter, 2);
      AtomicInteger counter = new AtomicInteger();
      var job1 = job(sleepGetIncrementAlgorithm(counter));
      var job2 = job(sleepGetIncrementAlgorithm(counter));
      var job = concat(job1, job2);

      assertThat(executeSingleJob(job))
          .isEqualTo(stringH("(0,0)"));

      ArgumentCaptor<Computed> captor = ArgumentCaptor.forClass(Computed.class);
      verify(reporter, times(2)).report(eq(job1.info()), captor.capture());
      List<ResSource> reportedSources = captor.getAllValues()
          .stream()
          .map(Computed::resSource)
          .collect(toList());

      assertThat(reportedSources)
          .containsExactly(DISK, EXECUTION);
    }
  }

  @Test
  public void waiting_for_result_of_other_task_with_equal_hash_doesnt_block_executor_thread()
      throws Exception {
    parallelJobExecutor = new ParallelJobExecutor(computer(), reporter, 2);
    var counter = new AtomicInteger();
    var job = concat(
        job(sleepGetIncrementAlgorithm(counter)),
        job(sleepGetIncrementAlgorithm(counter)),
        job(getIncrementAlgorithm(counter)));

    assertThat(executeSingleJob(job))
        .isEqualTo(stringH("(1,1,0)"));
  }

  @Test
  public void task_throwing_runtime_exception_causes_error() throws Exception {
    Reporter reporter = mock(Reporter.class);
    parallelJobExecutor = new ParallelJobExecutor(computer(), new ExecutionReporter(reporter), 4);
    ArithmeticException exception = new ArithmeticException();
    var job = job(throwingAlgorithm(exception));
    var topRef = new TopRefS(STRING, "name", loc());

    assertThat(parallelJobExecutor.executeAll(Map.of(topRef, job)).get(topRef).isEmpty())
        .isTrue();
    verify(reporter).report(
        eq(job.info()),
        eq(header(job.info(), "exec")),
        eq(list(error("Execution failed with:\n" + getStackTraceAsString(exception)))));
  }

  @Test
  public void computer_that_throws_exception_is_detected() throws InterruptedException {
    RuntimeException exception = new RuntimeException();
    Computer computer = new Computer(null, null, null) {
      @Override
      public void compute(Algorithm algorithm, Input input, Consumer<Computed> consumer) {
        throw exception;
      }
    };
    parallelJobExecutor = new ParallelJobExecutor(computer, reporter);
    var topRef = new TopRefS(STRING, "name", loc());
    var job = job(valueAlgorithm("A"));

    Optional<ObjH> obj = parallelJobExecutor.executeAll(Map.of(topRef, job)).get(topRef);

    verify(reporter, only()).reportComputerException(same(job.info()), same(exception));
    assertThat(obj.isEmpty())
        .isTrue();
  }

  private Task concat(Job... deps) {
    var algorithm = concatAlgorithm();
    return job("concat", algorithm, deps);
  }

  private static Algorithm concatAlgorithm() {
    return new TestAlgorithm(Hash.of(1)) {
      @Override
      public Output run(Input input, NativeApi nativeApi) {
        String joinedArgs = toCommaSeparatedString(input.vals(), v -> ((StringH) v).toJ());
        StringH result = nativeApi.factory().string("(" + joinedArgs + ")");
        return new Output(result, nativeApi.messages());
      }
    };
  }

  private Task job(Algorithm algorithm, Job... deps) {
    return job("task_name", algorithm, deps);
  }

  private Task job(String name, Algorithm algorithm, Job... deps) {
    TaskInfo info = new TaskInfo(CALL, name, loc());
    return new Task(stringTH(), list(deps), info, algorithm);
  }

  private Algorithm valueAlgorithm(String value) {
    return new TestAlgorithm(Hash.of(asList(Hash.of(2), Hash.of(value)))) {
      @Override
      public Output run(Input input, NativeApi nativeApi) {
        StringH result = nativeApi.factory().string(value);
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

  private Algorithm sleepyWriteReadAlgorithm(Hash hash, AtomicInteger write, AtomicInteger read) {
    return new TestAlgorithm(hash) {
      @Override
      public Output run(Input input, NativeApi nativeApi) {
        write.incrementAndGet();
        sleep1000ms();
        return toStr(nativeApi, read.get());
      }
    };
  }

  private ObjH executeSingleJob(Job job) throws InterruptedException {
    return executeSingleJob(parallelJobExecutor, job);
  }

  private static ObjH executeSingleJob(ParallelJobExecutor parallelJobExecutor, Job job)
      throws InterruptedException {
    var topRef = new TopRefS(STRING, "name", loc());
    return parallelJobExecutor.executeAll(Map.of(topRef, job)).get(topRef).get();
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

  private static abstract class TestAlgorithm extends Algorithm {
    private final Hash hash;

    protected TestAlgorithm(Hash hash) {
      this(hash, true);
    }

    protected TestAlgorithm(Hash hash, boolean isPure) {
      super(TestingCatsH.STRING, isPure);
      this.hash = hash;
    }

    @Override
    public Hash hash() {
      return hash;
    }

    @Override
    public TypeH outputT() {
      return TestingCatsH.STRING;
    }
  }
}
