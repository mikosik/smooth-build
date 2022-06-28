package org.smoothbuild.vm.parallel;

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
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;
import static org.smoothbuild.vm.compute.ResSource.DISK;
import static org.smoothbuild.vm.compute.ResSource.EXECUTION;
import static org.smoothbuild.vm.compute.ResSource.MEMORY;
import static org.smoothbuild.vm.job.TaskKind.CALL;
import static org.smoothbuild.vm.parallel.ExecutionReporter.header;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.cnst.StringB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.type.TestingCatsB;
import org.smoothbuild.vm.algorithm.Algorithm;
import org.smoothbuild.vm.algorithm.Output;
import org.smoothbuild.vm.compute.CompRes;
import org.smoothbuild.vm.compute.Computer;
import org.smoothbuild.vm.compute.ResSource;
import org.smoothbuild.vm.job.Job;
import org.smoothbuild.vm.job.Task;
import org.smoothbuild.vm.job.TaskInfo;

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
        .isEqualTo(stringB("((A, B), (C, D))"));
  }

  @Test
  public void tasks_are_executed_in_parallel() throws Exception {
    var counterA = new AtomicInteger(10);
    var counterB = new AtomicInteger(20);
    var job = concat(
        job(sleepyWriteReadAlgorithm(Hash.of(102), counterB, counterA)),
        job(sleepyWriteReadAlgorithm(Hash.of(101), counterA, counterB)));

    assertThat(executeSingleJob(job))
        .isEqualTo(stringB("(11, 21)"));
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
        .isEqualTo(stringB("(0, 0, 0, 0)"));

    ArgumentCaptor<CompRes> captor = ArgumentCaptor.forClass(CompRes.class);
    ExecutionReporter reporter = this.reporter;
    verify(reporter, times(4)).report(eq(job1.info()), captor.capture());
    List<ResSource> reportedSources = captor.getAllValues()
        .stream()
        .map(CompRes::resSource)
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
          .isEqualTo(stringB("(0, 0)"));

      ArgumentCaptor<CompRes> captor = ArgumentCaptor.forClass(CompRes.class);
      verify(reporter, times(2)).report(eq(job1.info()), captor.capture());
      List<ResSource> reportedSources = captor.getAllValues()
          .stream()
          .map(CompRes::resSource)
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
          .isEqualTo(stringB("(0, 0)"));

      ArgumentCaptor<CompRes> captor = ArgumentCaptor.forClass(CompRes.class);
      verify(reporter, times(2)).report(eq(job1.info()), captor.capture());
      List<ResSource> reportedSources = captor.getAllValues()
          .stream()
          .map(CompRes::resSource)
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
        .isEqualTo(stringB("(1, 1, 0)"));
  }

  @Test
  public void task_throwing_runtime_exception_causes_error() throws Exception {
    var reporter = mock(TaskReporter.class);
    parallelJobExecutor = new ParallelJobExecutor(computer(), new ExecutionReporter(reporter), 4);
    ArithmeticException exception = new ArithmeticException();
    var job = job(throwingAlgorithm(exception));

    assertThat(parallelJobExecutor.executeAll(list(job)).get(0).isEmpty())
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
      public void compute(Algorithm algorithm, TupleB input, Consumer<CompRes> consumer) {
        throw exception;
      }
    };
    parallelJobExecutor = new ParallelJobExecutor(computer, reporter);
    var job = job(valueAlgorithm("A"));

    var val = parallelJobExecutor.executeAll(list(job)).get(0);

    verify(reporter, only()).reportComputerException(same(job.info()), same(exception));
    assertThat(val.isEmpty())
        .isTrue();
  }

  private Task concat(Job... deps) {
    var algorithm = concatAlgorithm();
    return job("concat", algorithm, deps);
  }

  private static Algorithm concatAlgorithm() {
    return new TestAlgorithm(Hash.of(1)) {
      @Override
      public Output run(TupleB input, NativeApi nativeApi) {
        String joinedArgs = toCommaSeparatedString(input.items(), v -> ((StringB) v).toJ());
        StringB result = nativeApi.factory().string("(" + joinedArgs + ")");
        return new Output(result, nativeApi.messages());
      }
    };
  }

  private Task job(Algorithm algorithm, Job... deps) {
    return job("task_name", algorithm, deps);
  }

  private Task job(String name, Algorithm algorithm, Job... deps) {
    TaskInfo info = new TaskInfo(CALL, name, loc());
    return new Task(algorithm, list(deps), info, bytecodeF());
  }

  private Algorithm valueAlgorithm(String value) {
    return new TestAlgorithm(Hash.of(asList(Hash.of(2), Hash.of(value)))) {
      @Override
      public Output run(TupleB input, NativeApi nativeApi) {
        StringB result = nativeApi.factory().string(value);
        return new Output(result, nativeApi.messages());
      }
    };
  }

  private Algorithm throwingAlgorithm(ArithmeticException exception) {
    return new TestAlgorithm(Hash.of(3)) {
      @Override
      public Output run(TupleB input, NativeApi nativeApi) {
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
      public Output run(TupleB input, NativeApi nativeApi) {
        sleep1000ms();
        return toStr(nativeApi, counter.getAndIncrement());
      }
    };
  }

  private Algorithm getIncrementAlgorithm(AtomicInteger counter) {
    return new TestAlgorithm(Hash.of(5)) {
      @Override
      public Output run(TupleB input, NativeApi nativeApi) {
        return toStr(nativeApi, counter.getAndIncrement());
      }
    };
  }

  private Algorithm sleepyWriteReadAlgorithm(Hash hash, AtomicInteger write, AtomicInteger read) {
    return new TestAlgorithm(hash) {
      @Override
      public Output run(TupleB input, NativeApi nativeApi) {
        write.incrementAndGet();
        sleep1000ms();
        return toStr(nativeApi, read.get());
      }
    };
  }

  private ObjB executeSingleJob(Job job) throws InterruptedException {
    return executeSingleJob(parallelJobExecutor, job);
  }

  private static ObjB executeSingleJob(ParallelJobExecutor parallelJobExecutor, Job job)
      throws InterruptedException {
    return parallelJobExecutor.executeAll(list(job)).get(0).get();
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
      super(TestingCatsB.STRING, isPure);
      this.hash = hash;
    }

    @Override
    public Hash hash() {
      return hash;
    }

    @Override
    public TypeB outputT() {
      return TestingCatsB.STRING;
    }
  }
}
