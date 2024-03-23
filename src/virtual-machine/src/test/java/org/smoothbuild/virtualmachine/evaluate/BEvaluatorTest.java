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
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.common.log.base.Level.FATAL;
import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.base.ResultSource.NOOP;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.ResultSource;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct;
import org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationResult;
import org.smoothbuild.virtualmachine.evaluate.compute.Computer;
import org.smoothbuild.virtualmachine.evaluate.execute.BReferenceInliner;
import org.smoothbuild.virtualmachine.evaluate.execute.BScheduler;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;
import org.smoothbuild.virtualmachine.evaluate.execute.Job;
import org.smoothbuild.virtualmachine.evaluate.execute.ReferenceIndexOutOfBoundsException;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskExecutor;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReporter;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;
import org.smoothbuild.virtualmachine.evaluate.task.InvokeTask;
import org.smoothbuild.virtualmachine.evaluate.task.OrderTask;
import org.smoothbuild.virtualmachine.evaluate.task.PickTask;
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
        var order = orderB(intB(7));

        var spyingExecutor = Mockito.spy(taskExecutor());
        assertThat(evaluate(bEvaluator(spyingExecutor), order)).isEqualTo(arrayB(intB(7)));

        verify(spyingExecutor, times(1)).enqueue(isA(OrderTask.class), any(), any());
      }

      @Test
      public void no_task_is_executed_for_func_arg_that_is_not_used() throws Exception {
        var lambda = lambdaB(list(arrayTB(boolTB())), intB(7));
        var call = callB(lambda, orderB(boolTB()));

        var spyingExecutor = Mockito.spy(taskExecutor());
        assertThat(evaluate(bEvaluator(spyingExecutor), call)).isEqualTo(intB(7));

        verify(spyingExecutor, never()).enqueue(isA(OrderTask.class), any(), any());
      }

      @Test
      public void no_task_is_executed_for_func_expr_in_call_to_map_when_array_expr_is_empty()
          throws Exception {
        var map = mapFuncB(intTB(), intTB());
        var mappingFunc = pickB(orderB(idFuncB()), intB(0));
        var emptyIntArray = arrayB(intTB());
        var call = callB(map, emptyIntArray, mappingFunc);

        var spyingExecutor = Mockito.spy(taskExecutor());
        assertThat(evaluate(bEvaluator(spyingExecutor), call)).isEqualTo(arrayB(intTB()));

        verify(spyingExecutor, never()).enqueue(isA(PickTask.class), any(), any());
      }

      @Test
      public void no_task_is_executed_for_func_arg_that_is_passed_to_func_where_it_is_not_used()
          throws Exception {
        var innerLambda = lambdaB(list(arrayTB(boolTB())), intB(7));
        var outerLambda =
            lambdaB(list(arrayTB(boolTB())), callB(innerLambda, referenceB(arrayTB(boolTB()), 0)));
        var call = callB(outerLambda, orderB(boolTB()));

        var spyingExecutor = Mockito.spy(taskExecutor());
        assertThat(evaluate(bEvaluator(spyingExecutor), call)).isEqualTo(intB(7));

        verify(spyingExecutor, never()).enqueue(isA(OrderTask.class), any(), any());
      }

      @Test
      public void task_for_func_arg_that_is_used_twice_is_executed_only_once() throws Exception {
        var arrayType = arrayTB(intTB());
        var lambda =
            lambdaB(list(arrayType), combineB(referenceB(arrayType, 0), referenceB(arrayType, 0)));
        var call = callB(lambda, orderB(intB(7)));

        var spyingExecutor = Mockito.spy(taskExecutor());
        assertThat(evaluate(bEvaluator(spyingExecutor), call))
            .isEqualTo(tupleB(arrayB(intB(7)), arrayB(intB(7))));

        verify(spyingExecutor, times(1)).enqueue(isA(OrderTask.class), any(), any());
      }
    }

    @Nested
    class _job_creation {
      @Test
      public void learning_test() throws Exception {
        // Learning test verifies that job creation is counted also inside func body.
        var func = lambdaB(orderB(intB(7)));
        var call = callB(func);

        var countingScheduler = countingSchedulerB();
        assertThat(evaluate(bEvaluator(() -> countingScheduler), call)).isEqualTo(arrayB(intB(7)));

        assertThat(countingScheduler.counters().get(BInt.class).intValue()).isEqualTo(1);
      }

      @Test
      public void job_for_unused_func_arg_is_created_but_not_jobs_for_its_dependencies()
          throws Exception {
        var lambda = lambdaB(list(arrayTB(boolTB())), intB(7));
        var call = callB(lambda, orderB(boolB()));

        var countingScheduler = countingSchedulerB();
        assertThat(evaluate(bEvaluator(() -> countingScheduler), call)).isEqualTo(intB(7));

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
        assertThat(evaluate(arrayB(intB(7)))).isEqualTo(arrayB(intB(7)));
      }

      @Test
      public void blob() throws Exception {
        assertThat(evaluate(blobB(7))).isEqualTo(blobB(7));
      }

      @Test
      public void bool() throws Exception {
        assertThat(evaluate(boolB(true))).isEqualTo(boolB(true));
      }

      @Test
      public void int_() throws Exception {
        assertThat(evaluate(intB(8))).isEqualTo(intB(8));
      }

      @Test
      public void string() throws Exception {
        assertThat(evaluate(stringB("abc"))).isEqualTo(stringB("abc"));
      }

      @Test
      public void tuple() throws Exception {
        assertThat(evaluate(tupleB(intB(7)))).isEqualTo(tupleB(intB(7)));
      }
    }

    @Nested
    class _operators {
      @Nested
      class _call {
        @Test
        public void lambda() throws Exception {
          var func = lambdaB(intB(7));
          var call = callB(func);
          assertThat(evaluate(call)).isEqualTo(intB(7));
        }

        @Test
        public void lambda_passed_as_argument() throws Exception {
          var paramFunc = lambdaB(intB(7));
          var paramFuncType = paramFunc.evaluationType();
          var outerLambda = lambdaB(list(paramFuncType), callB(referenceB(paramFuncType, 0)));
          var call = callB(outerLambda, paramFunc);
          assertThat(evaluate(call)).isEqualTo(intB(7));
        }

        @Test
        public void lambda_returned_from_call() throws Exception {
          var innerLambda = lambdaB(intB(7));
          var outerLambda = lambdaB(innerLambda);
          var call = callB(callB(outerLambda));
          assertThat(evaluate(call)).isEqualTo(intB(7));
        }

        @Test
        public void lambda_returning_param_of_enclosing_lambda() throws Exception {
          var innerLambda = lambdaB(referenceB(intTB(), 0));
          var outerLambda = lambdaB(list(intTB()), innerLambda);
          var callToOuter = callB(outerLambda, intB(17));
          var callToInnerReturnedByOuter = callB(callToOuter);
          assertThat(evaluate(callToInnerReturnedByOuter)).isEqualTo(intB(17));
        }

        @Test
        public void lambda_returning_value_from_environment_that_references_another_environment()
            throws Exception {
          var innerLambda = lambdaB(referenceB(intTB(), 0));
          var middleLambda = lambdaB(list(intTB()), innerLambda);
          var outerLambda = lambdaB(list(intTB()), callB(middleLambda, referenceB(intTB(), 0)));
          var middleReturnedByOuter = callB(outerLambda, intB(17));
          assertThat(evaluate(callB(middleReturnedByOuter))).isEqualTo(intB(17));
        }

        @Test
        public void if_function_with_true_condition() throws Exception {
          var ifFunc = ifFuncB(intTB());
          var call = callB(ifFunc, boolB(true), intB(7), intB(0));
          assertThat(evaluate(call)).isEqualTo(intB(7));
        }

        @Test
        public void if_func_with_false_condition() throws Exception {
          var ifFunc = ifFuncB(intTB());
          var call = callB(ifFunc, boolB(false), intB(7), intB(0));
          assertThat(evaluate(call)).isEqualTo(intB(0));
        }

        @Test
        public void map_func() throws Exception {
          var s = intTB();
          var r = tupleTB(s);
          var lambda = lambdaB(funcTB(s, r), combineB(referenceB(s, 0)));
          var mapFunc = mapFuncB(r, s);
          var map = callB(mapFunc, arrayB(intB(1), intB(2)), lambda);
          assertThat(evaluate(map)).isEqualTo(arrayB(tupleB(intB(1)), tupleB(intB(2))));
        }

        @Test
        public void native_func() throws Exception {
          var nativeFunc =
              nativeFuncB(funcTB(intTB(), intTB()), blobB(77), stringB("classBinaryName"));
          var call = callB(nativeFunc, intB(33));
          var nativeMethodLoader = mock(NativeMethodLoader.class);
          when(nativeMethodLoader.load(eq(nativeFunc)))
              .thenReturn(right(
                  BEvaluatorTest.class.getMethod("returnIntParam", NativeApi.class, BTuple.class)));
          assertThat(evaluate(bEvaluator(nativeMethodLoader), call)).isEqualTo(intB(33));
        }

        @Test
        public void native_func_passed_as_arg() throws Exception {
          var nativeFunc =
              nativeFuncB(funcTB(intTB(), intTB()), blobB(77), stringB("classBinaryName"));
          var nativeMethodLoader = mock(NativeMethodLoader.class);
          when(nativeMethodLoader.load(eq(nativeFunc)))
              .thenReturn(right(
                  BEvaluatorTest.class.getMethod("returnIntParam", NativeApi.class, BTuple.class)));

          var nativeFuncType = nativeFunc.evaluationType();
          var outerLambda =
              lambdaB(list(nativeFuncType), callB(referenceB(nativeFuncType, 0), intB(7)));
          var call = callB(outerLambda, nativeFunc);
          assertThat(evaluate(bEvaluator(nativeMethodLoader), call)).isEqualTo(intB(7));
        }

        @Test
        public void native_func_returned_from_call() throws Exception {
          var nativeFunc =
              nativeFuncB(funcTB(intTB(), intTB()), blobB(77), stringB("classBinaryName"));
          var nativeMethodLoader = mock(NativeMethodLoader.class);
          when(nativeMethodLoader.load(eq(nativeFunc)))
              .thenReturn(right(
                  BEvaluatorTest.class.getMethod("returnIntParam", NativeApi.class, BTuple.class)));

          var outerFunc = lambdaB(nativeFunc);
          var call = callB(callB(outerFunc), intB(7));
          assertThat(evaluate(bEvaluator(nativeMethodLoader), call)).isEqualTo(intB(7));
        }
      }

      @Test
      public void combine() throws Exception {
        var combine = combineB(intB(7));
        assertThat(evaluate(combine)).isEqualTo(tupleB(intB(7)));
      }

      @Test
      public void order() throws Exception {
        var order = orderB(intB(7), intB(8));
        assertThat(evaluate(order)).isEqualTo(arrayB(intB(7), intB(8)));
      }

      @Nested
      class _pick {
        @Test
        public void pick() throws Exception {
          var tuple = arrayB(intB(10), intB(11), intB(12), intB(13));
          var pick = pickB(tuple, intB(2));
          assertThat(evaluate(pick)).isEqualTo(intB(12));
        }

        @Test
        public void pick_with_index_outside_of_bounds() throws Exception {
          var pick = pickB(arrayB(intB(10), intB(11), intB(12), intB(13)), intB(4));
          var taskReporter = mock(TaskReporter.class);
          evaluateWithFailure(bEvaluator(taskReporter), pick);
          verify(taskReporter)
              .report(any(Task.class), argThat(this::isResultWithIndexOutOfBoundsError));
        }

        public boolean isResultWithIndexOutOfBoundsError(ComputationResult computationResult) {
          return computationResultWith(
              computationResult, ERROR, "Index (4) out of bounds. Array size = 4.");
        }

        @Test
        public void pick_with_index_negative() throws Exception {
          var pick = pickB(arrayB(intB(10), intB(11), intB(12), intB(13)), intB(-1));
          var taskReporter = mock(TaskReporter.class);
          evaluateWithFailure(bEvaluator(taskReporter), pick);
          verify(taskReporter)
              .report(any(Task.class), argThat(this::isResultWithNegativeIndexError));
        }

        public boolean isResultWithNegativeIndexError(ComputationResult computationResult) {
          return computationResultWith(
              computationResult, ERROR, "Index (-1) out of bounds. Array size = 4.");
        }
      }

      @Nested
      class _reference {
        @Test
        public void var_referencing_func_param() throws Exception {
          var lambda = lambdaB(list(intTB()), referenceB(intTB(), 0));
          var callB = callB(lambda, intB(7));
          assertThat(evaluate(callB)).isEqualTo(intB(7));
        }

        @Test
        public void var_inside_call_to_inner_lambda_referencing_param_of_enclosing_lambda()
            throws Exception {
          var innerLambda = lambdaB(list(), referenceB(intTB(), 0));
          var outerLambda = lambdaB(list(intTB()), callB(innerLambda));
          assertThat(evaluate(callB(outerLambda, intB(7)))).isEqualTo(intB(7));
        }

        @Test
        public void var_inside_inner_lambda_referencing_param_of_enclosing_lambda()
            throws Exception {
          var innerLambda = lambdaB(list(intTB()), referenceB(intTB(), 1));
          var outerLambda = lambdaB(list(intTB()), innerLambda);
          var callOuter = callB(outerLambda, intB(7));
          var callInner = callB(callOuter, intB(8));

          assertThat(evaluate(callInner)).isEqualTo(intB(7));
        }

        @Test
        public void var_referencing_with_index_out_of_bounds_causes_fatal() throws Exception {
          var lambda = lambdaB(list(intTB()), referenceB(intTB(), 2));
          var call = callB(lambda, intB(7));
          var taskReporter = mock(TaskReporter.class);
          evaluateWithFailure(bEvaluator(taskReporter), call);
          verify(taskReporter)
              .reportEvaluationException(any(ReferenceIndexOutOfBoundsException.class));
        }

        @Test
        public void
            reference_with_eval_type_different_than_actual_environment_value_eval_type_causes_fatal()
                throws Exception {
          var lambda = lambdaB(list(blobTB()), referenceB(intTB(), 0));
          var call = callB(lambda, blobB());
          var taskReporter = mock(TaskReporter.class);
          evaluateWithFailure(bEvaluator(taskReporter), call);
          verify(taskReporter).reportEvaluationException(argThat(e -> e.getMessage()
              .equals("environment(0) evaluationType is `Blob` but expected `Int`.")));
        }
      }

      @Test
      public void select() throws Exception {
        var tuple = tupleB(intB(7));
        var select = selectB(tuple, intB(0));
        assertThat(evaluate(select)).isEqualTo(intB(7));
      }
    }

    @Nested
    class _errors {
      @Test
      public void task_throwing_runtime_exception_causes_fatal() throws Exception {
        var taskReporter = mock(TaskReporter.class);
        var scheduler = bScheduler(taskReporter, 4);
        var expr = throwExceptionCall();
        evaluateWithFailure(new BEvaluator(() -> scheduler, taskReporter), expr);
        verify(taskReporter)
            .report(any(), argThat(this::computationResultWithFatalCausedByRuntimeException));
      }

      private boolean computationResultWithFatalCausedByRuntimeException(ComputationResult result) {
        return computationResultWith(
            result, FATAL, "Native code thrown exception:\njava.lang.ArithmeticException");
      }

      private BCall throwExceptionCall() throws Exception {
        var funcType = funcTB(stringTB());
        var nativeFunc = nativeFuncB(funcType, ThrowException.class);
        return callB(nativeFunc);
      }

      public static class ThrowException {
        public static BValue func(NativeApi nativeApi, BTuple args) {
          throw new ArithmeticException();
        }
      }

      @Test
      public void computer_that_throws_exception_is_detected() throws Exception {
        var taskReporter = mock(TaskReporter.class);
        var expr = stringB("abc");
        var runtimeException = new RuntimeException();
        var computer = new Computer(null, null, null) {
          @Override
          public void compute(Task task, BTuple input, Consumer<ComputationResult> consumer) {
            throw runtimeException;
          }
        };
        var scheduler = bScheduler(computer, taskReporter, 4);

        evaluateWithFailure(new BEvaluator(() -> scheduler, taskReporter), expr);
        verify(taskReporter, times(1)).reportEvaluationException(runtimeException);
      }
    }
  }

  private static boolean computationResultWith(
      ComputationResult result, Level level, String messageStart) {
    BArray messages = result.output().storedLogs();
    try {
      return messages.size() == 1
          && StoredLogStruct.level(messages.elements(BTuple.class).get(0)) == level
          && StoredLogStruct.message(messages.elements(BTuple.class).get(0))
              .startsWith(messageStart);
    } catch (BytecodeException e) {
      throw new RuntimeException(e);
    }
  }

  private static ArgumentMatcher<List<Log>> isLogListWithFatalMessageStartingWith(
      String messageStart) {
    return argument -> argument.size() == 1
        && argument.get(0).level() == FATAL
        && argument.get(0).message().startsWith(messageStart);
  }

  @Nested
  class _reporting {
    @Nested
    class _empty_trace {
      @ParameterizedTest
      @MethodSource("report_const_task_cases")
      public void report_value_as_const_task(BValue value) throws Exception {
        var taskReporter = mock(TaskReporter.class);
        evaluate(bEvaluator(taskReporter), value);
        verify(taskReporter).report(constTask(value, traceB()), computationResult(value, NOOP));
      }

      public static List<BValue> report_const_task_cases() throws Exception {
        var t = new TestingVirtualMachine();
        return list(
            t.arrayB(t.intB(17)),
            t.blobB(17),
            t.boolB(true),
            t.idFuncB(),
            t.ifFuncB(t.intTB()),
            t.mapFuncB(t.intTB(), t.blobTB()),
            t.nativeFuncB(),
            t.intB(17),
            t.stringB("abc"),
            t.tupleB(t.intB(17)));
      }

      @Test
      public void report_native_call_as_invoke_task() throws Exception {
        var func = returnAbcNativeFunc();
        var call = callB(func);
        assertReport(
            call,
            invokeTask(call, func, traceB(call, func)),
            computationResult(stringB("abc"), EXECUTION));
      }

      @Test
      public void report_combine_as_combine_task() throws Exception {
        var combine = combineB(intB(17));
        assertReport(
            combine,
            combineTask(combine, traceB()),
            computationResult(tupleB(intB(17)), EXECUTION));
      }

      @Test
      public void report_order_as_order_task() throws Exception {
        var order = orderB(intB(17));
        assertReport(
            order, orderTask(order, traceB()), computationResult(arrayB(intB(17)), EXECUTION));
      }

      @Test
      public void report_pick_as_pick_task() throws Exception {
        var pick = pickB(arrayB(intB(17)), intB(0));
        assertReport(pick, pickTask(pick, traceB()), computationResult(intB(17), EXECUTION));
      }

      @Test
      public void report_select_as_select_task() throws Exception {
        var select = selectB(tupleB(intB(17)), intB(0));
        assertReport(select, selectTask(select, traceB()), computationResult(intB(17), EXECUTION));
      }
    }

    @Nested
    class _with_traces {
      @Test
      public void order_inside_func_body() throws Exception {
        var order = orderB(intB(17));
        var func = lambdaB(order);
        var funcAsExpr = callB(lambdaB(func));
        var call = callB(funcAsExpr);
        assertReport(
            call,
            orderTask(order, traceB(call, func)),
            computationResult(arrayB(intB(17)), EXECUTION));
      }

      @Test
      public void order_inside_func_body_that_is_called_from_other_func_body() throws Exception {
        var order = orderB(intB(17));
        var func2 = lambdaB(order);
        var call2 = callB(func2);
        var func1 = lambdaB(call2);
        var call1 = callB(func1);
        assertReport(
            call1,
            orderTask(order, traceB(call2, func2, traceB(call1, func1))),
            computationResult(arrayB(intB(17)), EXECUTION));
      }
    }

    private void assertReport(BExpr expr, Task task, ComputationResult result)
        throws BytecodeException {
      var taskReporter = mock(TaskReporter.class);
      evaluate(bEvaluator(taskReporter), expr);
      verify(taskReporter).report(task, result);
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
      var expr = orderB(
          commandCall(testName, "INC2,COUNT1,WAIT1,GET1"),
          commandCall(testName, "INC1,COUNT1,WAIT1,GET2"));
      assertThat(evaluate(expr)).isEqualTo(arrayB(stringB("11"), stringB("21")));
    }

    @Test
    public void execution_waits_and_reuses_computation_with_equal_hash_that_is_being_executed()
        throws Exception {
      var testName = "execution_waits_and_reuses_computation_with_equal_hash";
      var counterName = testName + "1";
      COUNTERS.put(counterName, new AtomicInteger());
      var bExpr = orderB(
          commandCall(testName, "INC1"),
          commandCall(testName, "INC1"),
          commandCall(testName, "INC1"),
          commandCall(testName, "INC1"));

      var taskReporter = mock(TaskReporter.class);
      var vm = new BEvaluator(() -> bScheduler(taskReporter, 4), taskReporter);
      assertThat(evaluate(vm, bExpr))
          .isEqualTo(arrayB(stringB("1"), stringB("1"), stringB("1"), stringB("1")));

      verifyConstTasksResSource(4, DISK, taskReporter);
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
      var expr = orderB(
          commandCall(testName, "INC1,COUNT2,WAIT1,GET1"),
          commandCall(testName, "INC1,COUNT2,WAIT1,GET1"),
          commandCall(testName, "WAIT2,COUNT1,GET2"));

      var vm = new BEvaluator(() -> bScheduler(2), taskReporter());
      assertThat(evaluate(vm, expr)).isEqualTo(arrayB(stringB("1"), stringB("1"), stringB("0")));
    }

    private BCall commandCall(String testName, String commands) throws Exception {
      return commandCall(testName, commands, true);
    }

    private BCall commandCall(String testName, String commands, boolean isPure) throws Exception {
      var nativeFunc =
          nativeFuncB(funcTB(stringTB(), stringTB(), stringTB()), ExecuteCommands.class, isPure);
      return callB(nativeFunc, stringB(testName), stringB(commands));
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
    assertWithMessage(
            " ==== Console logs ==== \n" + inMemorySystemOut().toString() + "\n ==========\n")
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

  private static void verifyConstTasksResSource(
      int size, ResultSource expectedSource, TaskReporter reporter) throws Exception {
    var argCaptor = ArgumentCaptor.forClass(ComputationResult.class);
    verify(reporter, times(size)).report(taskMatcher(), argCaptor.capture());
    var resSources = listOfAll(argCaptor.getAllValues()).map(ComputationResult::source);
    assertThat(resSources).containsExactlyElementsIn(resSourceList(size, expectedSource));
  }

  private static Task taskMatcher() {
    return argThat(a -> a instanceof InvokeTask);
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
}
