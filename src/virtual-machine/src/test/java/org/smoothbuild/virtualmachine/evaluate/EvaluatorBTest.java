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
import static org.smoothbuild.common.log.Level.ERROR;
import static org.smoothbuild.common.log.Level.FATAL;
import static org.smoothbuild.virtualmachine.evaluate.compute.ResultSource.DISK;
import static org.smoothbuild.virtualmachine.evaluate.compute.ResultSource.EXECUTION;
import static org.smoothbuild.virtualmachine.evaluate.compute.ResultSource.NOOP;

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
import org.smoothbuild.common.log.Level;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeF;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CallB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BoolB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IntB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.helper.MessageStruct;
import org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationResult;
import org.smoothbuild.virtualmachine.evaluate.compute.Computer;
import org.smoothbuild.virtualmachine.evaluate.compute.ResultSource;
import org.smoothbuild.virtualmachine.evaluate.execute.Job;
import org.smoothbuild.virtualmachine.evaluate.execute.ReferenceIndexOutOfBoundsException;
import org.smoothbuild.virtualmachine.evaluate.execute.ReferenceInlinerB;
import org.smoothbuild.virtualmachine.evaluate.execute.SchedulerB;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskExecutor;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReporter;
import org.smoothbuild.virtualmachine.evaluate.execute.TraceB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;
import org.smoothbuild.virtualmachine.evaluate.task.InvokeTask;
import org.smoothbuild.virtualmachine.evaluate.task.OrderTask;
import org.smoothbuild.virtualmachine.evaluate.task.PickTask;
import org.smoothbuild.virtualmachine.evaluate.task.Task;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class EvaluatorBTest extends TestingVirtualMachine {
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
        assertThat(evaluate(evaluatorB(spyingExecutor), order)).isEqualTo(arrayB(intB(7)));

        verify(spyingExecutor, times(1)).enqueue(isA(OrderTask.class), any(), any());
      }

      @Test
      public void no_task_is_executed_for_func_arg_that_is_not_used() throws Exception {
        var lambdaB = lambdaB(list(arrayTB(boolTB())), intB(7));
        var call = callB(lambdaB, orderB(boolTB()));

        var spyingExecutor = Mockito.spy(taskExecutor());
        assertThat(evaluate(evaluatorB(spyingExecutor), call)).isEqualTo(intB(7));

        verify(spyingExecutor, never()).enqueue(isA(OrderTask.class), any(), any());
      }

      @Test
      public void no_task_is_executed_for_func_expr_in_call_to_map_when_array_expr_is_empty()
          throws Exception {
        var mapFuncB = mapFuncB(intTB(), intTB());
        var mappingFunc = pickB(orderB(idFuncB()), intB(0));
        var emptyIntArray = arrayB(intTB());
        var callB = callB(mapFuncB, emptyIntArray, mappingFunc);

        var spyingExecutor = Mockito.spy(taskExecutor());
        assertThat(evaluate(evaluatorB(spyingExecutor), callB)).isEqualTo(arrayB(intTB()));

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
        assertThat(evaluate(evaluatorB(spyingExecutor), call)).isEqualTo(intB(7));

        verify(spyingExecutor, never()).enqueue(isA(OrderTask.class), any(), any());
      }

      @Test
      public void task_for_func_arg_that_is_used_twice_is_executed_only_once() throws Exception {
        var arrayT = arrayTB(intTB());
        var lambdaB = lambdaB(list(arrayT), combineB(referenceB(arrayT, 0), referenceB(arrayT, 0)));
        var call = callB(lambdaB, orderB(intB(7)));

        var spyingExecutor = Mockito.spy(taskExecutor());
        assertThat(evaluate(evaluatorB(spyingExecutor), call))
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
        assertThat(evaluate(evaluatorB(() -> countingScheduler), call)).isEqualTo(arrayB(intB(7)));

        assertThat(countingScheduler.counters().get(IntB.class).intValue()).isEqualTo(1);
      }

      @Test
      public void job_for_unused_func_arg_is_created_but_not_jobs_for_its_dependencies()
          throws Exception {
        var lambdaB = lambdaB(list(arrayTB(boolTB())), intB(7));
        var call = callB(lambdaB, orderB(boolB()));

        var countingScheduler = countingSchedulerB();
        assertThat(evaluate(evaluatorB(() -> countingScheduler), call)).isEqualTo(intB(7));

        assertThat(countingScheduler.counters().get(BoolB.class)).isNull();
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
          var paramFuncT = paramFunc.evaluationType();
          var outerLambda = lambdaB(list(paramFuncT), callB(referenceB(paramFuncT, 0)));
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
          var nativeFuncB =
              nativeFuncB(funcTB(intTB(), intTB()), blobB(77), stringB("classBinaryName"));
          var call = callB(nativeFuncB, intB(33));
          var nativeMethodLoader = mock(NativeMethodLoader.class);
          when(nativeMethodLoader.load(eq(nativeFuncB)))
              .thenReturn(right(
                  EvaluatorBTest.class.getMethod("returnIntParam", NativeApi.class, TupleB.class)));
          assertThat(evaluate(evaluatorB(nativeMethodLoader), call)).isEqualTo(intB(33));
        }

        @Test
        public void native_func_passed_as_arg() throws Exception {
          var nativeFuncB =
              nativeFuncB(funcTB(intTB(), intTB()), blobB(77), stringB("classBinaryName"));
          var nativeMethodLoader = mock(NativeMethodLoader.class);
          when(nativeMethodLoader.load(eq(nativeFuncB)))
              .thenReturn(right(
                  EvaluatorBTest.class.getMethod("returnIntParam", NativeApi.class, TupleB.class)));

          var nativeFuncT = nativeFuncB.evaluationType();
          var outerLambda = lambdaB(list(nativeFuncT), callB(referenceB(nativeFuncT, 0), intB(7)));
          var call = callB(outerLambda, nativeFuncB);
          assertThat(evaluate(evaluatorB(nativeMethodLoader), call)).isEqualTo(intB(7));
        }

        @Test
        public void native_func_returned_from_call() throws Exception {
          var nativeFuncB =
              nativeFuncB(funcTB(intTB(), intTB()), blobB(77), stringB("classBinaryName"));
          var nativeMethodLoader = mock(NativeMethodLoader.class);
          when(nativeMethodLoader.load(eq(nativeFuncB)))
              .thenReturn(right(
                  EvaluatorBTest.class.getMethod("returnIntParam", NativeApi.class, TupleB.class)));

          var outerFunc = lambdaB(nativeFuncB);
          var call = callB(callB(outerFunc), intB(7));
          assertThat(evaluate(evaluatorB(nativeMethodLoader), call)).isEqualTo(intB(7));
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
          evaluateWithFailure(evaluatorB(taskReporter), pick);
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
          evaluateWithFailure(evaluatorB(taskReporter), pick);
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
          var lambdaB = lambdaB(list(intTB()), referenceB(intTB(), 0));
          var callB = callB(lambdaB, intB(7));
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
          var innerLambdaB = lambdaB(list(intTB()), referenceB(intTB(), 1));
          var outerLambdaB = lambdaB(list(intTB()), innerLambdaB);
          var callOuter = callB(outerLambdaB, intB(7));
          var callInner = callB(callOuter, intB(8));

          assertThat(evaluate(callInner)).isEqualTo(intB(7));
        }

        @Test
        public void var_referencing_with_index_out_of_bounds_causes_fatal() throws Exception {
          var lambdaB = lambdaB(list(intTB()), referenceB(intTB(), 2));
          var callB = callB(lambdaB, intB(7));
          var taskReporter = mock(TaskReporter.class);
          evaluateWithFailure(evaluatorB(taskReporter), callB);
          verify(taskReporter)
              .reportEvaluationException(any(ReferenceIndexOutOfBoundsException.class));
        }

        @Test
        public void
            reference_with_eval_type_different_than_actual_environment_value_eval_type_causes_fatal()
                throws Exception {
          var lambdaB = lambdaB(list(blobTB()), referenceB(intTB(), 0));
          var callB = callB(lambdaB, blobB());
          var taskReporter = mock(TaskReporter.class);
          evaluateWithFailure(evaluatorB(taskReporter), callB);
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
        var schedulerB = schedulerB(taskReporter, 4);
        var exprB = throwExceptionCall();
        evaluateWithFailure(new EvaluatorB(() -> schedulerB, taskReporter), exprB);
        verify(taskReporter)
            .report(any(), argThat(this::computationResultWithFatalCausedByRuntimeException));
      }

      private boolean computationResultWithFatalCausedByRuntimeException(ComputationResult result) {
        return computationResultWith(
            result, FATAL, "Native code thrown exception:\njava.lang.ArithmeticException");
      }

      private CallB throwExceptionCall() throws Exception {
        var funcTB = funcTB(stringTB());
        var nativeFuncB = nativeFuncB(funcTB, ThrowException.class);
        return callB(nativeFuncB);
      }

      public static class ThrowException {
        public static ValueB func(NativeApi nativeApi, TupleB args) {
          throw new ArithmeticException();
        }
      }

      @Test
      public void computer_that_throws_exception_is_detected() throws Exception {
        var taskReporter = mock(TaskReporter.class);
        var exprB = stringB("abc");
        var runtimeException = new RuntimeException();
        var computer = new Computer(null, null, null) {
          @Override
          public void compute(Task task, TupleB input, Consumer<ComputationResult> consumer) {
            throw runtimeException;
          }
        };
        var schedulerB = schedulerB(computer, taskReporter, 4);

        evaluateWithFailure(new EvaluatorB(() -> schedulerB, taskReporter), exprB);
        verify(taskReporter, times(1)).reportEvaluationException(runtimeException);
      }
    }
  }

  private static boolean computationResultWith(
      ComputationResult result, Level level, String messageStart) {
    ArrayB messages = result.output().messages();
    try {
      return messages.size() == 1
          && MessageStruct.level(messages.elements(TupleB.class).get(0)) == level
          && MessageStruct.text(messages.elements(TupleB.class).get(0)).startsWith(messageStart);
    } catch (BytecodeException e) {
      throw new RuntimeException(e);
    }
  }

  private static ArgumentMatcher<List<Log>> isLogListWithFatalMessageStartingWith(
      String messageStart) {
    return argument -> argument.size() == 1
        && argument.get(0).level() == Level.FATAL
        && argument.get(0).message().startsWith(messageStart);
  }

  @Nested
  class _reporting {
    @Nested
    class _empty_trace {
      @ParameterizedTest
      @MethodSource("report_const_task_cases")
      public void report_value_as_const_task(ValueB valueB) throws Exception {
        var taskReporter = mock(TaskReporter.class);
        evaluate(evaluatorB(taskReporter), valueB);
        verify(taskReporter).report(constTask(valueB, traceB()), computationResult(valueB, NOOP));
      }

      public static List<ValueB> report_const_task_cases() throws Exception {
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
        var funcB = returnAbcNativeFunc();
        var callB = callB(funcB);
        assertReport(
            callB,
            invokeTask(callB, funcB, traceB(callB, funcB)),
            computationResult(stringB("abc"), EXECUTION));
      }

      @Test
      public void report_combine_as_combine_task() throws Exception {
        var combineB = combineB(intB(17));
        assertReport(
            combineB,
            combineTask(combineB, traceB()),
            computationResult(tupleB(intB(17)), EXECUTION));
      }

      @Test
      public void report_order_as_order_task() throws Exception {
        var orderB = orderB(intB(17));
        assertReport(
            orderB, orderTask(orderB, traceB()), computationResult(arrayB(intB(17)), EXECUTION));
      }

      @Test
      public void report_pick_as_pick_task() throws Exception {
        var pickB = pickB(arrayB(intB(17)), intB(0));
        assertReport(pickB, pickTask(pickB, traceB()), computationResult(intB(17), EXECUTION));
      }

      @Test
      public void report_select_as_select_task() throws Exception {
        var selectB = selectB(tupleB(intB(17)), intB(0));
        assertReport(
            selectB, selectTask(selectB, traceB()), computationResult(intB(17), EXECUTION));
      }
    }

    @Nested
    class _with_traces {
      @Test
      public void order_inside_func_body() throws Exception {
        var orderB = orderB(intB(17));
        var funcB = lambdaB(orderB);
        var funcAsExpr = callB(lambdaB(funcB));
        var callB = callB(funcAsExpr);
        assertReport(
            callB,
            orderTask(orderB, traceB(callB, funcB)),
            computationResult(arrayB(intB(17)), EXECUTION));
      }

      @Test
      public void order_inside_func_body_that_is_called_from_other_func_body() throws Exception {
        var orderB = orderB(intB(17));
        var func2 = lambdaB(orderB);
        var call2 = callB(func2);
        var func1 = lambdaB(call2);
        var call1 = callB(func1);
        assertReport(
            call1,
            orderTask(orderB, traceB(call2, func2, traceB(call1, func1))),
            computationResult(arrayB(intB(17)), EXECUTION));
      }
    }

    private void assertReport(ExprB exprB, Task task, ComputationResult result)
        throws BytecodeException {
      var taskReporter = mock(TaskReporter.class);
      evaluate(evaluatorB(taskReporter), exprB);
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
      var exprB = orderB(
          commandCall(testName, "INC1"),
          commandCall(testName, "INC1"),
          commandCall(testName, "INC1"),
          commandCall(testName, "INC1"));

      var taskReporter = mock(TaskReporter.class);
      var vm = new EvaluatorB(() -> schedulerB(taskReporter, 4), taskReporter);
      assertThat(evaluate(vm, exprB))
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
      var exprB = orderB(
          commandCall(testName, "INC1,COUNT2,WAIT1,GET1"),
          commandCall(testName, "INC1,COUNT2,WAIT1,GET1"),
          commandCall(testName, "WAIT2,COUNT1,GET2"));

      var vm = new EvaluatorB(() -> schedulerB(2), taskReporter());
      assertThat(evaluate(vm, exprB)).isEqualTo(arrayB(stringB("1"), stringB("1"), stringB("0")));
    }

    private CallB commandCall(String testName, String commands) throws Exception {
      return commandCall(testName, commands, true);
    }

    private CallB commandCall(String testName, String commands, boolean isPure) throws Exception {
      var nativeFuncB =
          nativeFuncB(funcTB(stringTB(), stringTB(), stringTB()), ExecuteCommands.class, isPure);
      return callB(nativeFuncB, stringB(testName), stringB(commands));
    }

    public static class ExecuteCommands {
      public static ValueB func(NativeApi nativeApi, TupleB args) throws Exception {
        String name = ((StringB) args.get(0)).toJavaString();
        String commands = ((StringB) args.get(1)).toJavaString();
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

  private ExprB evaluate(ExprB expr) {
    return evaluate(evaluatorB(), expr);
  }

  private ValueB evaluate(EvaluatorB evaluatorB, ExprB expr) {
    var maybeResult = evaluatorB.evaluate(list(expr));
    assertWithMessage(
            " ==== Console logs ==== \n" + inMemorySystemOut().toString() + "\n ==========\n")
        .that(maybeResult.isSome())
        .isTrue();
    var results = maybeResult.get();
    assertThat(results.size()).isEqualTo(1);
    return results.get(0);
  }

  private void evaluateWithFailure(EvaluatorB evaluatorB, ExprB expr) {
    var results = evaluatorB.evaluate(list(expr));
    assertThat(results).isEqualTo(none());
  }

  public static IntB returnIntParam(NativeApi nativeApi, TupleB args) throws Exception {
    return (IntB) args.get(0);
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
    return new CountingSchedulerB(taskExecutor(), bytecodeF(), varReducerB());
  }

  private static class CountingSchedulerB extends SchedulerB {
    private final ConcurrentHashMap<Class<?>, AtomicInteger> counters = new ConcurrentHashMap<>();

    public CountingSchedulerB(
        TaskExecutor taskExecutor, BytecodeF bytecodeF, ReferenceInlinerB referenceInlinerB) {
      super(taskExecutor, bytecodeF, referenceInlinerB);
    }

    @Override
    protected Job newJob(ExprB exprB, List<Job> environment, TraceB trace) {
      counters.computeIfAbsent(exprB.getClass(), k -> new AtomicInteger()).incrementAndGet();
      return super.newJob(exprB, environment, trace);
    }

    public ConcurrentHashMap<Class<?>, AtomicInteger> counters() {
      return counters;
    }
  }
}
