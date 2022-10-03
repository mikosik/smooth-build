package org.smoothbuild.vm.execute;

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
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;
import static org.smoothbuild.vm.compute.ResSource.DISK;
import static org.smoothbuild.vm.compute.ResSource.EXECUTION;
import static org.smoothbuild.vm.compute.ResSource.MEMORY;
import static org.smoothbuild.vm.execute.ExecutionReporter.header;
import static org.smoothbuild.vm.execute.TaskKind.COMBINE;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.bytecode.expr.val.StringB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TypeB;
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
        concat(
            job(valueTask("A")),
            job(valueTask("B"))),
        concat(
            job(valueTask("C")),
            job(valueTask("D"))));

    assertThat(executeSingleJob(job))
        .isEqualTo(stringB("((A,B),(C,D))"));
  }

  @Test
  public void tasks_are_executed_in_parallel() throws Exception {
    var counterA = new AtomicInteger(10);
    var counterB = new AtomicInteger(20);
    var job = concat(
        job(sleepyWriteReadTask(Hash.of(102), counterB, counterA)),
        job(sleepyWriteReadTask(Hash.of(101), counterA, counterB)));

    assertThat(executeSingleJob(job))
        .isEqualTo(stringB("(11,21)"));
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
        .isEqualTo(stringB("(0,0,0,0)"));

    ArgumentCaptor<CompRes> captor = ArgumentCaptor.forClass(CompRes.class);
    verify(reporter, times(4))
        .report(eq(job1.info()), captor.capture());
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
      context = executionContext(reporter, 2);
      var counter = new AtomicInteger();
      var job1 = job(sleepGetIncrementTask(counter, false));
      var job2 = job(sleepGetIncrementTask(counter, false));
      var job = concat(job1, job2);

      assertThat(executeSingleJob(job))
          .isEqualTo(stringB("(0,0)"));

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
      context = executionContext(reporter, 2);
      AtomicInteger counter = new AtomicInteger();
      var job1 = job(sleepGetIncrementTask(counter));
      var job2 = job(sleepGetIncrementTask(counter));
      var job = concat(job1, job2);

      assertThat(executeSingleJob(job))
          .isEqualTo(stringB("(0,0)"));

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
    context = executionContext(reporter, 2);
    var counter = new AtomicInteger();
    var job = concat(
        job(sleepGetIncrementTask(counter)),
        job(sleepGetIncrementTask(counter)),
        job(incrementTask(counter)));

    assertThat(executeSingleJob(job))
        .isEqualTo(stringB("(1,1,0)"));
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
        eq(job.info()),
        eq(header(job.info(), "exec")),
        eq(list(error("Execution failed with:\n" + getStackTraceAsString(exception)))));
  }

  @Test
  public void computer_that_throws_exception_is_detected() throws InterruptedException {
    RuntimeException exception = new RuntimeException();
    Computer computer = new Computer(null, null, null) {
      @Override
      public void compute(Task task, TupleB input, Consumer<CompRes> consumer) {
        throw exception;
      }
    };
    context = executionContext(computer, reporter, 4);
    var job = job(valueTask("A"));

    var val = executeJobs(context, list(job)).get(0);

    verify(reporter, only()).reportComputerException(same(job.info()), same(exception));
    assertThat(val.isEmpty())
        .isTrue();
  }

  private MyJob concat(Job... deps) {
    return job(concatTask(), deps);
  }

  private Task concatTask() {
    return new TestTask("concat", stringTB(), Hash.of(1)) {
      @Override
      public Output run(TupleB input, NativeApi nativeApi) {
        String joinedArgs = toCommaSeparatedString(input.items(), v -> ((StringB) v).toJ());
        StringB result = nativeApi.factory().string("(" + joinedArgs + ")");
        return new Output(result, nativeApi.messages());
      }
    };
  }

  private MyJob job(Task task, Job... deps) {
    return new MyJob(task, list(deps), bytecodeF(), context);
  }

  private Task valueTask(String value) {
    return new TestTask("value", stringTB(), Hash.of(asList(Hash.of(2), Hash.of(value)))) {
      @Override
      public Output run(TupleB input, NativeApi nativeApi) {
        StringB result = nativeApi.factory().string(value);
        return new Output(result, nativeApi.messages());
      }
    };
  }

  private Task throwingTask(ArithmeticException exception) {
    return new TestTask("throwing", stringTB(), Hash.of(3)) {
      @Override
      public Output run(TupleB input, NativeApi nativeApi) {
        throw exception;
      }
    };
  }

  private Task sleepGetIncrementTask(AtomicInteger counter) {
    return sleepGetIncrementTask(counter, true);
  }

  private Task sleepGetIncrementTask(AtomicInteger counter, boolean isPure) {
    return new TestTask("getIncrement", stringTB(), Hash.of(4), isPure) {
      @Override
      public Output run(TupleB input, NativeApi nativeApi) {
        sleep1000ms();
        return toStr(nativeApi, counter.getAndIncrement());
      }
    };
  }

  private Task incrementTask(AtomicInteger counter) {
    return new TestTask("increment", stringTB(), Hash.of(5)) {
      @Override
      public Output run(TupleB input, NativeApi nativeApi) {
        return toStr(nativeApi, counter.getAndIncrement());
      }
    };
  }

  private Task sleepyWriteReadTask(Hash hash, AtomicInteger write, AtomicInteger read) {
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
    return Vm.evaluate(context.taskExecutor(), jobs);
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

  private static abstract class TestTask extends Task {
    private final Hash hash;

    protected TestTask(String name, TypeB type, Hash hash) {
      this(name, type, hash, true);
    }

    protected TestTask(String name, TypeB type, Hash hash, boolean isPure) {
      super(type, COMBINE, labeledLoc(name), isPure);
      this.hash = hash;
    }

    @Override
    public Hash hash() {
      return hash;
    }
  }

  private static class MyJob extends ExecutingJob {
    private final Task task;
    private final ImmutableList<Job> depJs;
    private final BytecodeF bytecodeF;

    public MyJob(Task task, ImmutableList<Job> depJs, BytecodeF bytecodeF,
        ExecutionContext context) {
      super(context);
      this.task = task;
      this.depJs = depJs;
      this.bytecodeF = bytecodeF;
    }

    public TaskInfo info() {
      return task.info();
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
