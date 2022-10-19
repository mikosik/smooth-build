package org.smoothbuild.vm.execute;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Collections.nCopies;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;
import static org.smoothbuild.vm.compute.ResSource.DISK;
import static org.smoothbuild.vm.compute.ResSource.EXECUTION;
import static org.smoothbuild.vm.compute.ResSource.MEMORY;
import static org.smoothbuild.vm.execute.ExecutionReporter.header;
import static org.smoothbuild.vm.execute.TaskKind.COMBINE;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.Vm;
import org.smoothbuild.vm.compute.CompRes;
import org.smoothbuild.vm.compute.Computer;
import org.smoothbuild.vm.compute.ResSource;
import org.smoothbuild.vm.job.ExecutingJob;
import org.smoothbuild.vm.job.ExecutionContext;
import org.smoothbuild.vm.job.Job;
import org.smoothbuild.vm.task.ConstTask;
import org.smoothbuild.vm.task.ExecutableTask;
import org.smoothbuild.vm.task.OrderTask;
import org.smoothbuild.vm.task.Output;
import org.smoothbuild.vm.task.Task;

import com.google.common.collect.ImmutableList;

public class ParallelJobExecutorTest extends TestContext {
  private ExecutionContext context;
  private ExecutionReporter reporter;

  @BeforeEach
  public void before() {
    reporter = mock(ExecutionReporter.class);
    context = executionContext(reporter, 4);
  }

  @Test
  public void tasks_are_executed() throws Exception {
    var job = concat(
        job(constStringTask("A")),
        job(constStringTask("B")));

    assertThat(executeSingleJob(job))
        .isEqualTo(arrayB(stringB("A"), stringB("B")));
  }

  @Test
  public void tasks_are_executed_in_parallel() throws Exception {
    var counterA = new AtomicInteger(10);
    var counterB = new AtomicInteger(20);
    var job = concat(
        job(sleepyWriteReadTask(Hash.of(102), counterB, counterA)),
        job(sleepyWriteReadTask(Hash.of(101), counterA, counterB)));

    assertThat(executeSingleJob(job))
        .isEqualTo(arrayB(stringB("11"), stringB("21")));
  }

  @Test
  public void task_execution_waits_and_reuses_result_of_task_with_equal_hash_that_is_being_executed()
      throws Exception {
    context = executionContext(reporter, 4);
    var counter = new AtomicInteger();
    var job1 = job(sleepGetIncrementTask(counter));
    var job2 = job(sleepGetIncrementTask(counter));
    var job3 = job(sleepGetIncrementTask(counter));
    var job4 = job(sleepGetIncrementTask(counter));
    var job = concat(job1, job2, job3, job4);

    assertThat(executeSingleJob(job))
        .isEqualTo(arrayB(stringB("0"), stringB("0"), stringB("0"), stringB("0")));

    verifyOtherTasksResSource(list(job1, job2, job3, job4), DISK);
  }

  @Nested
  class _result_source_for_computation_of_ {
    @Test
    public void impure_func_is_memory() throws Exception {
      context = executionContext(reporter, 2);
      var counter = new AtomicInteger();
      var job1 = job(sleepGetIncrementTask(counter, false));
      var job2 = job(sleepGetIncrementTask(counter, false));
      var job = concat(job1, job2);

      assertThat(executeSingleJob(job))
          .isEqualTo(arrayB(stringB("0"), stringB("0")));
      verifyOtherTasksResSource(list(job1, job2), MEMORY);
    }

    @Test
    public void pure_func_is_disk() throws Exception {
      context = executionContext(reporter, 2);
      AtomicInteger counter = new AtomicInteger();
      var job1 = job(sleepGetIncrementTask(counter));
      var job2 = job(sleepGetIncrementTask(counter));
      var job = concat(job1, job2);

      assertThat(executeSingleJob(job))
          .isEqualTo(arrayB(stringB("0"), stringB("0")));
      verifyOtherTasksResSource(list(job1, job2), DISK);
    }
  }

  @Test
  public void waiting_for_result_of_other_task_with_equal_hash_doesnt_block_executor_thread()
      throws Exception {
    context = executionContext(reporter, 2);
    var counter = new AtomicInteger();
    var job = concat(
        job(sleepGetIncrementTask(counter)),
        job(sleepGetIncrementTask(counter)),
        job(incrementTask(counter)));

    assertThat(executeSingleJob(job))
        .isEqualTo(arrayB(stringB("1"), stringB("1"), stringB("0")));
  }

  @Test
  public void task_throwing_runtime_exception_causes_error() throws Exception {
    var reporter = mock(TaskReporter.class);
    context = executionContext(new ExecutionReporter(reporter), 4);
    ArithmeticException exception = new ArithmeticException();
    var job = job(throwingTask(exception));

    assertThat(executeJobs(context, list(job)).get(0).isEmpty())
        .isTrue();
    verify(reporter).report(
        eq(job.task()),
        eq(header(job.task(), "exec")),
        eq(list(error("Execution failed with:\n" + getStackTraceAsString(exception)))));
  }

  @Test
  public void computer_that_throws_exception_is_detected() throws InterruptedException {
    RuntimeException exception = new RuntimeException();
    Computer computer = new Computer(null, null, null) {
      @Override
      public void compute(ExecutableTask task, TupleB input, Consumer<CompRes> consumer) {
        throw exception;
      }
    };
    context = executionContext(computer, reporter, 4);
    var job = job(constStringTask("A"));

    var val = executeJobs(context, list(job)).get(0);

    verify(reporter, only()).reportComputerException(same(job.task()), same(exception));
    assertThat(val.isEmpty())
        .isTrue();
  }

  private void verifyOtherTasksResSource(ImmutableList<MyJob> jobs, ResSource expectedResSource) {
    var resSources = map(jobs, j -> {
      var argCaptor = ArgumentCaptor.forClass(CompRes.class);
      verify(reporter, times(1)).report(eq(j.task()), argCaptor.capture());
      return argCaptor.getValue().resSource();
    });

    assertThat(resSources)
        .containsExactlyElementsIn(resSourceList(jobs, expectedResSource));
  }

  private static ArrayList<ResSource> resSourceList(
      ImmutableList<MyJob> jobs, ResSource expectedResSource) {
    var expected = new ArrayList<>(nCopies(jobs.size(), expectedResSource));
    expected.set(0, EXECUTION);
    return expected;
  }

  private MyJob concat(Job... deps) {
    return job(orderTask(), deps);
  }

  private ExecutableTask orderTask() {
    return new OrderTask(arrayTB(stringTB()), tagLoc(), null);
  }

  private MyJob job(ExecutableTask task, Job... deps) {
    return new MyJob(task, list(deps), bytecodeF(), context);
  }

  private ExecutableTask constStringTask(String value) {
    return new ConstTask(stringB(value), tagLoc(), null);
  }

  private ExecutableTask throwingTask(ArithmeticException exception) {
    return new TestTask("throwing", stringTB(), Hash.of(3)) {
      @Override
      public Output run(TupleB input, NativeApi nativeApi) {
        throw exception;
      }
    };
  }

  private ExecutableTask sleepGetIncrementTask(AtomicInteger counter) {
    return sleepGetIncrementTask(counter, true);
  }

  private ExecutableTask sleepGetIncrementTask(AtomicInteger counter, boolean isPure) {
    return new TestTask("getIncrement", stringTB(), Hash.of(4), isPure) {
      @Override
      public Output run(TupleB input, NativeApi nativeApi) {
        sleep1000ms();
        return toStr(nativeApi, counter.getAndIncrement());
      }
    };
  }

  private ExecutableTask incrementTask(AtomicInteger counter) {
    return new TestTask("increment", stringTB(), Hash.of(5)) {
      @Override
      public Output run(TupleB input, NativeApi nativeApi) {
        return toStr(nativeApi, counter.getAndIncrement());
      }
    };
  }

  private ExecutableTask sleepyWriteReadTask(Hash hash, AtomicInteger write, AtomicInteger read) {
    return new TestTask("sleepyWriteRead", stringTB(), hash) {
      @Override
      public Output run(TupleB input, NativeApi nativeApi) {
        write.incrementAndGet();
        sleep1000ms();
        return toStr(nativeApi, read.get());
      }
    };
  }

  private ExprB executeSingleJob(Job job) throws InterruptedException {
    return executeSingleJob(context, job);
  }

  private static ExprB executeSingleJob(ExecutionContext context, Job job)
      throws InterruptedException {
    return executeJobs(context, list(job)).get(0).get();
  }

  private static ImmutableList<Optional<InstB>> executeJobs(ExecutionContext context,
      ImmutableList<Job> jobs) throws InterruptedException {
    return Vm.evaluate(context, jobs);
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

  private static abstract class TestTask extends ExecutableTask {
    protected TestTask(String name, TypeB type, Hash hash) {
      this(name, type, hash, true);
    }

    protected TestTask(String name, TypeB type, Hash hash, boolean isPure) {
      super(type, COMBINE, tagLoc(name), traceS(), isPure, hash);
    }
  }

  private static class MyJob extends ExecutingJob {
    private final ExecutableTask task;
    private final ImmutableList<Job> depJs;
    private final BytecodeF bytecodeF;

    public MyJob(ExecutableTask task, ImmutableList<Job> depJs, BytecodeF bytecodeF,
        ExecutionContext context) {
      super(context);
      this.task = task;
      this.depJs = depJs;
      this.bytecodeF = bytecodeF;
    }

    public Task task() {
      return task;
    }

    @Override
    public Promise<InstB> evaluateImpl() {
      PromisedValue<InstB> result = new PromisedValue<>();
      var depResults = map(depJs, Job::evaluate);
      runWhenAllAvailable(depResults,
          () -> context().taskExecutor().enqueue(task, toInput(depResults), result));
      return result;
    }

    private TupleB toInput(ImmutableList<Promise<InstB>> depResults) {
      return bytecodeF.tuple(map(depResults, Promise::get));
    }
  }
}
