package org.smoothbuild.virtualmachine.evaluate;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static java.util.Collections.nCopies;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smoothbuild.common.collect.Either.right;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.common.log.base.Level.FATAL;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.virtualmachine.VirtualMachineConstants.EVALUATE;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.common.log.base.ResultSource;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.testing.MemoryReporter;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMethod;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationResult;
import org.smoothbuild.virtualmachine.evaluate.compute.Computer;
import org.smoothbuild.virtualmachine.evaluate.execute.BReferenceInliner;
import org.smoothbuild.virtualmachine.evaluate.execute.BScheduler;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;
import org.smoothbuild.virtualmachine.evaluate.execute.Job;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskExecutor;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;
import org.smoothbuild.virtualmachine.evaluate.task.OrderTask;
import org.smoothbuild.virtualmachine.evaluate.task.Task;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BEvaluatorTest extends TestingVirtualMachine {
  public static final ConcurrentHashMap<String, AtomicInteger> COUNTERS = new ConcurrentHashMap<>();
  public static final ConcurrentHashMap<String, CountDownLatch> COUNTDOWNS =
      new ConcurrentHashMap<>();

  @Nested
  class _laziness {
    @Nested
    class _task_execution {
      @Test
      public void learning_test() throws Exception {
        // This test makes sure that it is possible to detect Task creation using a mock.
        var order = bOrder(bInt(7));

        var spyingExecutor = Mockito.spy(taskExecutor());
        assertThat(evaluate(bEvaluator(spyingExecutor), order)).isEqualTo(bArray(bInt(7)));

        verify(spyingExecutor, times(1)).enqueue(isA(OrderTask.class), any(), any());
      }

      @Test
      public void no_task_is_executed_for_lambda_arg_that_is_not_used() throws Exception {
        var lambda = bLambda(list(bArrayType(bBoolType())), bInt(7));
        var call = bCall(lambda, bOrder(bBoolType()));

        var spyingExecutor = Mockito.spy(taskExecutor());
        assertThat(evaluate(bEvaluator(spyingExecutor), call)).isEqualTo(bInt(7));

        verify(spyingExecutor, never()).enqueue(isA(OrderTask.class), any(), any());
      }

      @Test
      public void no_task_is_executed_for_lambda_arg_that_is_passed_to_lambda_where_it_is_not_used()
          throws Exception {
        var innerLambda = bLambda(list(bArrayType(bBoolType())), bInt(7));
        var outerLambda = bLambda(
            list(bArrayType(bBoolType())),
            bCall(innerLambda, bReference(bArrayType(bBoolType()), 0)));
        var call = bCall(outerLambda, bOrder(bBoolType()));

        var spyingExecutor = Mockito.spy(taskExecutor());
        assertThat(evaluate(bEvaluator(spyingExecutor), call)).isEqualTo(bInt(7));

        verify(spyingExecutor, never()).enqueue(isA(OrderTask.class), any(), any());
      }

      @Test
      public void task_for_lambda_arg_that_is_used_twice_is_executed_only_once() throws Exception {
        var arrayType = bArrayType(bIntType());
        var lambda =
            bLambda(list(arrayType), bCombine(bReference(arrayType, 0), bReference(arrayType, 0)));
        var call = bCall(lambda, bOrder(bInt(7)));

        var spyingExecutor = Mockito.spy(taskExecutor());
        assertThat(evaluate(bEvaluator(spyingExecutor), call))
            .isEqualTo(bTuple(bArray(bInt(7)), bArray(bInt(7))));

        verify(spyingExecutor, times(1)).enqueue(isA(OrderTask.class), any(), any());
      }
    }

    @Nested
    class _job_creation {
      @Test
      public void learning_test() throws Exception {
        // Learning test verifies that job creation is counted also inside lambda body.
        var lambda = bLambda(bOrder(bInt(7)));
        var call = bCall(lambda);

        var countingScheduler = countingSchedulerB();
        assertThat(evaluate(bEvaluator(() -> countingScheduler), call)).isEqualTo(bArray(bInt(7)));

        assertThat(countingScheduler.counters().get(BInt.class).intValue()).isEqualTo(1);
      }

      @Test
      public void job_for_unused_lambda_arg_is_created_but_not_jobs_for_its_dependencies()
          throws Exception {
        var lambda = bLambda(list(bArrayType(bBoolType())), bInt(7));
        var call = bCall(lambda, bOrder(bBool()));

        var countingScheduler = countingSchedulerB();
        assertThat(evaluate(bEvaluator(() -> countingScheduler), call)).isEqualTo(bInt(7));

        assertThat(countingScheduler.counters().get(BBool.class)).isNull();
      }
    }
  }

  @Nested
  class _evaluation {
    @Nested
    class _values {
      @Test
      public void array() throws Exception {
        assertThat(evaluate(bArray(bInt(7)))).isEqualTo(bArray(bInt(7)));
      }

      @Test
      public void blob() throws Exception {
        assertThat(evaluate(bBlob(7))).isEqualTo(bBlob(7));
      }

      @Test
      public void bool() throws Exception {
        assertThat(evaluate(bBool(true))).isEqualTo(bBool(true));
      }

      @Test
      public void int_() throws Exception {
        assertThat(evaluate(bInt(8))).isEqualTo(bInt(8));
      }

      @Test
      public void string() throws Exception {
        assertThat(evaluate(bString("abc"))).isEqualTo(bString("abc"));
      }

      @Test
      public void tuple() throws Exception {
        assertThat(evaluate(bTuple(bInt(7)))).isEqualTo(bTuple(bInt(7)));
      }
    }

    @Nested
    class _operation {
      @Nested
      class _call {
        @Test
        public void lambda_without_arguments() throws Exception {
          var lambda = bLambda(bInt(7));
          var call = bCall(lambda);
          assertThat(evaluate(call)).isEqualTo(bInt(7));
        }

        @Test
        public void lambda_with_single_argument_passed_inside_combine() throws Exception {
          var lambda = bIntIdLambda();
          var call = bCallWithArguments(lambda, bTuple(bInt(7)));
          assertThat(evaluate(call)).isEqualTo(bInt(7));
        }

        @Test
        public void lambda_with_single_argument_passed_inside_tuple() throws Exception {
          var lambda = bIntIdLambda();
          var call = bCallWithArguments(lambda, bCombine(bInt(7)));
          assertThat(evaluate(call)).isEqualTo(bInt(7));
        }

        @Test
        public void lambda_with_single_argument_passed_as_expression_that_evaluates_to_tuple()
            throws Exception {
          var lambda = bIntIdLambda();
          var call = bCallWithArguments(lambda, bPick(bOrder(bTuple(bInt(7))), 0));
          assertThat(evaluate(call)).isEqualTo(bInt(7));
        }

        @Test
        public void lambda_passed_as_argument() throws Exception {
          var paramLambda = bLambda(bInt(7));
          var paramLambdaType = paramLambda.evaluationType();
          var outerLambda = bLambda(list(paramLambdaType), bCall(bReference(paramLambdaType, 0)));
          var call = bCall(outerLambda, paramLambda);
          assertThat(evaluate(call)).isEqualTo(bInt(7));
        }

        @Test
        public void lambda_returned_from_call() throws Exception {
          var innerLambda = bLambda(bInt(7));
          var outerLambda = bLambda(innerLambda);
          var call = bCall(bCall(outerLambda));
          assertThat(evaluate(call)).isEqualTo(bInt(7));
        }

        @Test
        public void lambda_returning_param_of_enclosing_lambda() throws Exception {
          var innerLambda = bLambda(bReference(bIntType(), 0));
          var outerLambda = bLambda(list(bIntType()), innerLambda);
          var callToOuter = bCall(outerLambda, bInt(17));
          var callToInnerReturnedByOuter = bCall(callToOuter);
          assertThat(evaluate(callToInnerReturnedByOuter)).isEqualTo(bInt(17));
        }

        @Test
        public void lambda_returning_value_from_environment_that_references_another_environment()
            throws Exception {
          var innerLambda = bLambda(bReference(bIntType(), 0));
          var middleLambda = bLambda(list(bIntType()), innerLambda);
          var outerLambda =
              bLambda(list(bIntType()), bCall(middleLambda, bReference(bIntType(), 0)));
          var middleReturnedByOuter = bCall(outerLambda, bInt(17));
          assertThat(evaluate(bCall(middleReturnedByOuter))).isEqualTo(bInt(17));
        }
      }

      @Test
      public void combine() throws Exception {
        var combine = bCombine(bInt(7));
        assertThat(evaluate(combine)).isEqualTo(bTuple(bInt(7)));
      }

      @Test
      public void if_with_true_condition() throws Exception {
        var if_ = bIf(bBool(true), bInt(1), bInt(2));
        assertThat(evaluate(if_)).isEqualTo(bInt(1));
      }

      @Test
      public void if_with_false_condition() throws Exception {
        var if_ = bIf(bBool(false), bInt(1), bInt(2));
        assertThat(evaluate(if_)).isEqualTo(bInt(2));
      }

      @Test
      public void invoke() throws Exception {
        var methodTuple = bMethodTuple(bBlob(77), "classBinaryName", "methodName");
        var invoke = bInvoke(bIntType(), methodTuple, bTuple(bInt(33)));
        var nativeMethodLoader = mock(NativeMethodLoader.class);
        when(nativeMethodLoader.load(eq(new BMethod(methodTuple))))
            .thenReturn(right(
                BEvaluatorTest.class.getMethod("returnIntParam", NativeApi.class, BTuple.class)));
        assertThat(evaluate(bEvaluator(nativeMethodLoader), invoke)).isEqualTo(bInt(33));
      }

      @Test
      public void map() throws Exception {
        var array = bArray(bInt(1), bInt(4));
        var mapper = bLambda(list(bIntType()), bCombine(bReference(bIntType(), 0)));
        var map = bMap(array, mapper);
        assertThat(evaluate(map)).isEqualTo(bArray(bTuple(bInt(1)), bTuple(bInt(4))));
      }

      @Test
      public void map_with_empty_array() throws Exception {
        var map = bMap(bArray(bIntType()), bIntIdLambda());
        assertThat(evaluate(map)).isEqualTo(bArray(bIntType()));
      }

      @Test
      public void order() throws Exception {
        var order = bOrder(bInt(7), bInt(8));
        assertThat(evaluate(order)).isEqualTo(bArray(bInt(7), bInt(8)));
      }

      @Nested
      class _pick {
        @Test
        public void pick() throws Exception {
          var tuple = bArray(bInt(10), bInt(11), bInt(12), bInt(13));
          var pick = bPick(tuple, bInt(2));
          assertThat(evaluate(pick)).isEqualTo(bInt(12));
        }

        @Test
        public void pick_with_index_outside_of_bounds() throws Exception {
          var pick = bPick(bArray(bInt(10), bInt(11), bInt(12), bInt(13)), bInt(4));
          var reporter = mock(Reporter.class);
          evaluateWithFailure(bEvaluator(reporter), pick);
          verify(reporter).submit(argThat(this::isResultWithIndexOutOfBoundsError));
        }

        public boolean isResultWithIndexOutOfBoundsError(Report report) {
          return taskReportWith(report, ERROR, "Index (4) out of bounds. Array size = 4.");
        }

        @Test
        public void pick_with_index_negative() throws Exception {
          var pick = bPick(bArray(bInt(10), bInt(11), bInt(12), bInt(13)), bInt(-1));
          var reporter = mock(Reporter.class);
          evaluateWithFailure(bEvaluator(reporter), pick);
          verify(reporter).submit(argThat(this::isResultWithNegativeIndexError));
        }

        public boolean isResultWithNegativeIndexError(Report report) {
          return taskReportWith(report, ERROR, "Index (-1) out of bounds. Array size = 4.");
        }
      }

      @Nested
      class _reference {
        @Test
        public void var_referencing_lambda_param() throws Exception {
          var lambda = bLambda(list(bIntType()), bReference(bIntType(), 0));
          var callB = bCall(lambda, bInt(7));
          assertThat(evaluate(callB)).isEqualTo(bInt(7));
        }

        @Test
        public void var_inside_call_to_inner_lambda_referencing_param_of_enclosing_lambda()
            throws Exception {
          var innerLambda = bLambda(list(), bReference(bIntType(), 0));
          var outerLambda = bLambda(list(bIntType()), bCall(innerLambda));
          assertThat(evaluate(bCall(outerLambda, bInt(7)))).isEqualTo(bInt(7));
        }

        @Test
        public void var_inside_inner_lambda_referencing_param_of_enclosing_lambda()
            throws Exception {
          var innerLambda = bLambda(list(bIntType()), bReference(bIntType(), 1));
          var outerLambda = bLambda(list(bIntType()), innerLambda);
          var callOuter = bCall(outerLambda, bInt(7));
          var callInner = bCall(callOuter, bInt(8));

          assertThat(evaluate(callInner)).isEqualTo(bInt(7));
        }

        @Test
        public void var_referencing_with_index_out_of_bounds_causes_fatal() throws Exception {
          var lambda = bLambda(list(bIntType()), bReference(bIntType(), 2));
          var call = bCall(lambda, bInt(7));
          evaluateWithFailure(bEvaluator(), call);
          var reports = reporter().reports();
          assertReportsContains(
              reports,
              FATAL,
              "org.smoothbuild.virtualmachine.evaluate.execute.ReferenceIndexOutOfBoundsException:"
                  + " Reference index = 2 is out of bounds. Bound variables size = 1.");
        }

        @Test
        public void
            reference_with_eval_type_different_than_actual_environment_value_eval_type_causes_fatal()
                throws Exception {
          var lambda = bLambda(list(bBlobType()), bReference(bIntType(), 0));
          var call = bCall(lambda, bBlob());
          evaluateWithFailure(bEvaluator(), call);
          assertReportsContains(
              reporter().reports(),
              FATAL,
              "java.lang.RuntimeException: environment(0) evaluationType is `Blob` but expected `Int`.");
        }
      }

      @Test
      public void select() throws Exception {
        var tuple = bTuple(bInt(7));
        var select = bSelect(tuple, bInt(0));
        assertThat(evaluate(select)).isEqualTo(bInt(7));
      }
    }

    @Nested
    class _errors {
      @Test
      public void task_throwing_runtime_exception_causes_fatal() throws Exception {
        var reporter = mock(Reporter.class);
        var scheduler = bScheduler(reporter, 4);
        var expr = throwExceptionCall();
        evaluateWithFailure(new BEvaluator(() -> scheduler, reporter), expr);
        verify(reporter).submit(argThat(this::reportWithFatalCausedByRuntimeException));
      }

      private boolean reportWithFatalCausedByRuntimeException(Report report) {
        return reportWith(
            report, FATAL, "Native code thrown exception:\njava.lang.ArithmeticException");
      }

      private BCall throwExceptionCall() throws Exception {
        var lambdaType = bLambdaType(bStringType());
        var invoke = bInvoke(lambdaType, ThrowException.class);
        return bCall(invoke);
      }

      public static class ThrowException {
        public static BValue func(NativeApi nativeApi, BTuple args) {
          throw new ArithmeticException();
        }
      }

      @Test
      public void computer_that_throws_exception_is_detected() throws Exception {
        var expr = bString("abc");
        var runtimeException = new RuntimeException();
        var computer = new Computer(null, null, null) {
          @Override
          public void compute(Task task, BTuple input, Consumer<ComputationResult> consumer) {
            throw runtimeException;
          }
        };
        var scheduler = bScheduler(computer, reporter(), 4);

        evaluateWithFailure(bEvaluator(() -> scheduler), expr);
        assertThat(reporter().reports())
            .contains(report(EVALUATE, new Trace(), EXECUTION, list(fatal(runtimeException))));
      }
    }
  }

  private static boolean taskReportWith(Report report, Level level, String messageStart) {
    var logs = report.logs();
    return logs.size() == 1
        && logs.get(0).level() == level
        && logs.get(0).message().startsWith(messageStart);
  }

  private static boolean reportWith(Report report, Level level, String messageStart) {
    var logs = report.logs();
    return logs.size() == 1
        && logs.get(0).level() == level
        && logs.get(0).message().startsWith(messageStart);
  }

  @Nested
  class _reporting {
    @Nested
    class _empty_trace {
      @ParameterizedTest
      @MethodSource("report_const_task_cases")
      public void report_value_as_const_task(BValue value) {
        var reporter = mock(Reporter.class);
        evaluate(bEvaluator(reporter), value);
        var report = report(EVALUATE.append(label("const")), bTrace(), EXECUTION, list());
        verify(reporter).submit(report);
      }

      public static List<BValue> report_const_task_cases() throws Exception {
        var t = new TestingVirtualMachine();
        return list(
            t.bArray(t.bInt(17)),
            t.bBlob(17),
            t.bBool(true),
            t.bIntIdLambda(),
            t.bInt(17),
            t.bString("abc"),
            t.bTuple(t.bInt(17)));
      }

      @Test
      public void report_invoke_as_invoke_task() throws Exception {
        var invoke = bReturnAbcInvoke();
        assertTaskReport(invoke, label("invoke"), bTrace(), EXECUTION);
      }

      @Test
      public void report_if_as_if_task() throws Exception {
        var condition = bBool(true);
        var then_ = bInt(1);
        var else_ = bInt(2);
        var if_ = bIf(condition, then_, else_);
        var reporter = mock(Reporter.class);
        evaluate(bEvaluator(reporter), if_);
        verify(reporter, times(2))
            .submit(report(EVALUATE.append(label("const")), bTrace(), EXECUTION, list()));
      }

      @Test
      public void report_map_as_map_task() throws Exception {
        var array = bArray(bInt(3));
        var mapper = bIntIdLambda();
        var if_ = bMap(array, mapper);
        var reporter = mock(Reporter.class);
        evaluate(bEvaluator(reporter), if_);
        verify(reporter, times(3))
            .submit(report(EVALUATE.append(label("const")), bTrace(), EXECUTION, list()));
      }

      @Test
      public void report_combine_as_combine_task() throws Exception {
        var combine = bCombine(bInt(17));
        assertTaskReport(combine, label("combine"), bTrace(), EXECUTION);
      }

      @Test
      public void report_order_as_order_task() throws Exception {
        var order = bOrder(bInt(17));
        assertTaskReport(order, label("order"), bTrace(), EXECUTION);
      }

      @Test
      public void report_pick_as_pick_task() throws Exception {
        var pick = bPick(bArray(bInt(17)), bInt(0));
        assertTaskReport(pick, label("pick"), bTrace(), EXECUTION);
      }

      @Test
      public void report_select_as_select_task() throws Exception {
        var select = bSelect(bTuple(bInt(17)), bInt(0));
        assertTaskReport(select, label("select"), bTrace(), EXECUTION);
      }
    }

    @Nested
    class _with_traces {
      @Test
      public void order_inside_lambda_body() throws Exception {
        var order = bOrder(bInt(17));
        var lambda = bLambda(order);
        var lambdaAsExpr = bCall(bLambda(lambda));
        var call = bCall(lambdaAsExpr);
        assertTaskReport(call, label("order"), bTrace(call, lambda), EXECUTION);
      }

      @Test
      public void order_inside_lambda_body_that_is_called_from_other_lambda_body()
          throws Exception {
        var order = bOrder(bInt(17));
        var lambda2 = bLambda(order);
        var call2 = bCall(lambda2);
        var lambda1 = bLambda(call2);
        var call1 = bCall(lambda1);
        assertTaskReport(
            call1, label("order"), bTrace(call2, lambda2, bTrace(call1, lambda1)), EXECUTION);
      }
    }

    private void assertTaskReport(
        BExpr expr, Label label, BTrace trace, ResultSource resultSource) {
      var reporter = mock(Reporter.class);
      evaluate(bEvaluator(reporter), expr);
      var taskReport = report(EVALUATE.append(label), trace, resultSource, list());
      verify(reporter).submit(taskReport);
    }
  }

  @Nested
  class _parallelism {
    @Test
    public void tasks_are_executed_in_parallel() throws Exception {
      String testName = "tasks_are_executed_in_parallel";
      var counterA = testName + "1";
      var counterB = testName + "2";
      var countdown = testName + "1";
      COUNTERS.put(counterA, new AtomicInteger(10));
      COUNTERS.put(counterB, new AtomicInteger(20));
      COUNTDOWNS.put(countdown, new CountDownLatch(2));
      var expr = bOrder(
          invokeExecuteCommands(testName, "INC2,COUNT1,WAIT1,GET1"),
          invokeExecuteCommands(testName, "INC1,COUNT1,WAIT1,GET2"));
      assertThat(evaluate(expr)).isEqualTo(bArray(bString("11"), bString("21")));
    }

    @Test
    public void execution_waits_and_reuses_computation_with_equal_hash_that_is_being_executed()
        throws Exception {
      var testName = "execution_waits_and_reuses_computation_with_equal_hash";
      var counterName = testName + "1";
      COUNTERS.put(counterName, new AtomicInteger());
      var bExpr = bOrder(
          invokeExecuteCommands(testName, "INC1"),
          invokeExecuteCommands(testName, "INC1"),
          invokeExecuteCommands(testName, "INC1"),
          invokeExecuteCommands(testName, "INC1"));

      var reporter = reporter();
      var vm = new BEvaluator(() -> bScheduler(reporter, 4), new MemoryReporter());
      assertThat(evaluate(vm, bExpr))
          .isEqualTo(bArray(bString("1"), bString("1"), bString("1"), bString("1")));

      verifyConstTasksResultSource(4, DISK, reporter);
    }

    @Test
    public void waiting_for_result_of_other_task_with_equal_hash_doesnt_block_executor_thread()
        throws Exception {
      var testName = "waiting_for_computation_with_same_hash_doesnt_block_executor_thread";
      var counter1 = testName + "1";
      var counter2 = testName + "2";
      var countdown1 = testName + "1";
      var countdown2 = testName + "2";

      COUNTERS.put(counter1, new AtomicInteger());
      COUNTERS.put(counter2, new AtomicInteger());
      COUNTDOWNS.put(countdown1, new CountDownLatch(1));
      COUNTDOWNS.put(countdown2, new CountDownLatch(1));
      var expr = bOrder(
          invokeExecuteCommands(testName, "INC1,COUNT2,WAIT1,GET1"),
          invokeExecuteCommands(testName, "INC1,COUNT2,WAIT1,GET1"),
          invokeExecuteCommands(testName, "WAIT2,COUNT1,GET2"));

      var vm = new BEvaluator(() -> bScheduler(2), new MemoryReporter());
      assertThat(evaluate(vm, expr)).isEqualTo(bArray(bString("1"), bString("1"), bString("0")));
    }

    private BInvoke invokeExecuteCommands(String testName, String commands) throws Exception {
      return invokeExecuteCommands(testName, commands, true);
    }

    private BInvoke invokeExecuteCommands(String testName, String commands, boolean isPure)
        throws Exception {
      var arguments = bTuple(bString(testName), bString(commands));
      return bInvoke(bStringType(), ExecuteCommands.class, isPure, arguments);
    }

    public static class ExecuteCommands {
      public static BValue func(NativeApi nativeApi, BTuple args) throws Exception {
        String name = ((BString) args.get(0)).toJavaString();
        String commands = ((BString) args.get(1)).toJavaString();
        int result = 0;
        for (String command : commands.split(",")) {
          char index = command.charAt(command.length() - 1);
          final String nameAndIndex = name + index;
          var opcode = command.substring(0, command.length() - 1);
          switch (opcode) {
            case "GET" -> result = COUNTERS.get(nameAndIndex).get();
            case "INC" -> result = COUNTERS.get(nameAndIndex).incrementAndGet();
            case "COUNT" -> COUNTDOWNS.get(nameAndIndex).countDown();
            case "WAIT" -> {
              try {
                if (!COUNTDOWNS.get(nameAndIndex).await(20, SECONDS)) {
                  throw new RuntimeException();
                }
              } catch (InterruptedException e) {
                throw new RuntimeException(e);
              }
            }
            default -> throw new RuntimeException("Unknown command opcode: " + opcode);
          }
        }
        if (result == -1) {
          throw new RuntimeException("result not set.");
        }
        return nativeApi.factory().string(Integer.toString(result));
      }
    }
  }

  private BExpr evaluate(BExpr expr) {
    return evaluate(bEvaluator(), expr);
  }

  private BValue evaluate(BEvaluator bEvaluator, BExpr expr) {
    var maybeResult = bEvaluator.evaluate(list(expr));
    assertWithMessage(" ==== Console logs ==== \n" + reporter() + "\n ==========\n")
        .that(maybeResult.isSome())
        .isTrue();
    var results = maybeResult.get();
    assertThat(results.size()).isEqualTo(1);
    return results.get(0);
  }

  private void evaluateWithFailure(BEvaluator bEvaluator, BExpr expr) {
    var results = bEvaluator.evaluate(list(expr));
    assertThat(results).isEqualTo(none());
  }

  public static BInt returnIntParam(NativeApi nativeApi, BTuple args) throws Exception {
    return (BInt) args.get(0);
  }

  private static void verifyConstTasksResultSource(
      int size, ResultSource expectedSource, MemoryReporter reporter) {
    var sources = reporter
        .reports()
        .filter(r -> r.label().equals(EVALUATE.append(label("invoke"))))
        .map(Report::source);
    assertThat(sources).containsExactlyElementsIn(resSourceList(size, expectedSource));
  }

  private static ArrayList<ResultSource> resSourceList(int size, ResultSource expectedSource) {
    var expected = new ArrayList<>(nCopies(size, expectedSource));
    expected.set(0, EXECUTION);
    return expected;
  }

  private CountingSchedulerB countingSchedulerB() {
    return new CountingSchedulerB(taskExecutor(), bytecodeF(), bReferenceInliner());
  }

  private static class CountingSchedulerB extends BScheduler {
    private final ConcurrentHashMap<Class<?>, AtomicInteger> counters = new ConcurrentHashMap<>();

    public CountingSchedulerB(
        TaskExecutor taskExecutor,
        BytecodeFactory bytecodeFactory,
        BReferenceInliner bReferenceInliner) {
      super(taskExecutor, bytecodeFactory, bReferenceInliner);
    }

    @Override
    protected Job newJob(BExpr expr, List<Job> environment, BTrace trace) {
      counters.computeIfAbsent(expr.getClass(), k -> new AtomicInteger()).incrementAndGet();
      return super.newJob(expr, environment, trace);
    }

    public ConcurrentHashMap<Class<?>, AtomicInteger> counters() {
      return counters;
    }
  }

  private static void assertReportsContains(
      List<Report> reports, Level level, String messagePrefix) {
    var contains = reports.stream().anyMatch(r -> r.logs()
        .anyMatches(l -> l.level() == level && l.message().startsWith(messagePrefix)));
    assertWithMessage(reports.toString()).that(contains).isTrue();
  }
}
