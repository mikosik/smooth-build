package org.smoothbuild.vm.evaluate;

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
import static org.smoothbuild.out.log.ImmutableLogs.logs;
import static org.smoothbuild.out.log.Level.FATAL;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.vm.evaluate.compute.ResultSource.DISK;
import static org.smoothbuild.vm.evaluate.compute.ResultSource.EXECUTION;
import static org.smoothbuild.vm.evaluate.compute.ResultSource.NOOP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.run.eval.MessageStruct;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.accept.MemoryReporter;
import org.smoothbuild.util.collect.Try;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.oper.CallB;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.BoolB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.compute.ComputationResult;
import org.smoothbuild.vm.evaluate.compute.Computer;
import org.smoothbuild.vm.evaluate.compute.ResultSource;
import org.smoothbuild.vm.evaluate.execute.Job;
import org.smoothbuild.vm.evaluate.execute.ReferenceInlinerB;
import org.smoothbuild.vm.evaluate.execute.SchedulerB;
import org.smoothbuild.vm.evaluate.execute.TaskExecutor;
import org.smoothbuild.vm.evaluate.execute.TaskReporter;
import org.smoothbuild.vm.evaluate.execute.TraceB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;
import org.smoothbuild.vm.evaluate.task.InvokeTask;
import org.smoothbuild.vm.evaluate.task.NativeMethodLoader;
import org.smoothbuild.vm.evaluate.task.OrderTask;
import org.smoothbuild.vm.evaluate.task.PickTask;
import org.smoothbuild.vm.evaluate.task.Task;

import com.google.common.collect.ImmutableList;

public class EvaluatorBTest extends TestContext {
  public static final ConcurrentHashMap<String, AtomicInteger> COUNTERS = new ConcurrentHashMap<>();
  public static final ConcurrentHashMap<String, CountDownLatch> COUNTDOWNS
      = new ConcurrentHashMap<>();

  @Nested
  class _laziness {
    @Nested
    class _task_execution {
      @Test
      public void learning_test() {
        // This test makes sure that it is possible to detect Task creation using a mock.
        var order = orderB(intB(7));

        var spyingExecutor = Mockito.spy(taskExecutor());
        assertThat(evaluate(evaluatorB(spyingExecutor), order))
            .isEqualTo(arrayB(intB(7)));

        verify(spyingExecutor, times(1)).enqueue(isA(OrderTask.class), any(), any());
      }

      @Test
      public void no_task_is_executed_for_func_arg_that_is_not_used() {
        var func = exprFuncB(list(arrayTB(boolTB())), intB(7));
        var call = callB(func, orderB(boolTB()));

        var spyingExecutor = Mockito.spy(taskExecutor());
        assertThat(evaluate(evaluatorB(spyingExecutor), call))
            .isEqualTo(intB(7));

        verify(spyingExecutor, never()).enqueue(isA(OrderTask.class), any(), any());
      }

      @Test
      public void no_task_is_executed_for_func_expr_in_call_to_map_when_array_expr_is_empty() {
        var mapFuncB = mapFuncB(intTB(), intTB());
        var mappingFunc = pickB(orderB(idFuncB()), intB(0));
        var emptyIntArray = arrayB(intTB());
        var callB = callB(mapFuncB, emptyIntArray, mappingFunc);

        var spyingExecutor = Mockito.spy(taskExecutor());
        assertThat(evaluate(evaluatorB(spyingExecutor), callB))
            .isEqualTo(arrayB(intTB()));

        verify(spyingExecutor, never()).enqueue(isA(PickTask.class), any(), any());
      }

      @Test
      public void no_task_is_executed_for_func_arg_that_is_passed_to_func_where_it_is_not_used() {
        var innerFunc = exprFuncB(list(arrayTB(boolTB())), intB(7));
        var outerFunc = exprFuncB(list(arrayTB(boolTB())),
            callB(innerFunc, referenceB(arrayTB(boolTB()), 0)));
        var call = callB(outerFunc, orderB(boolTB()));

        var spyingExecutor = Mockito.spy(taskExecutor());
        assertThat(evaluate(evaluatorB(spyingExecutor), call))
            .isEqualTo(intB(7));

        verify(spyingExecutor, never()).enqueue(isA(OrderTask.class), any(), any());
      }

      @Test
      public void task_for_func_arg_that_is_used_twice_is_executed_only_once() {
        var arrayT = arrayTB(intTB());
        var func = exprFuncB(list(arrayT), combineB(referenceB(arrayT, 0), referenceB(arrayT, 0)));
        var call = callB(func, orderB(intB(7)));

        var spyingExecutor = Mockito.spy(taskExecutor());
        assertThat(evaluate(evaluatorB(spyingExecutor), call))
            .isEqualTo(tupleB(arrayB(intB(7)), arrayB(intB(7))));

        verify(spyingExecutor, times(1)).enqueue(isA(OrderTask.class), any(), any());
      }
    }

    @Nested
    class _job_creation {
      @Test
      public void learning_test() {
        // Learning test verifies that job creation is counted also inside func body.
        var func = exprFuncB(orderB(intB(7)));
        var call = callB(func);

        var countingScheduler = countingSchedulerB();
        assertThat(evaluate(evaluatorB(() -> countingScheduler), call))
            .isEqualTo(arrayB(intB(7)));

        assertThat(countingScheduler.counters().get(IntB.class).intValue())
            .isEqualTo(1);
      }

      @Test
      public void job_for_unused_func_arg_is_created_but_not_jobs_for_its_dependencies() {
        var func = exprFuncB(list(arrayTB(boolTB())), intB(7));
        var call = callB(func, orderB(boolB()));

        var countingScheduler = countingSchedulerB();
        assertThat(evaluate(evaluatorB(() -> countingScheduler), call))
            .isEqualTo(intB(7));

        assertThat(countingScheduler.counters().get(BoolB.class))
            .isNull();
      }
    }
  }

  @Nested
  class _evaluation {
    @Nested
    class _values {
      @Test
      public void array() {
        assertThat(evaluate(arrayB(intB(7))))
            .isEqualTo(arrayB(intB(7)));
      }

      @Test
      public void blob() {
        assertThat(evaluate(blobB(7)))
            .isEqualTo(blobB(7));
      }

      @Test
      public void bool() {
        assertThat(evaluate(boolB(true)))
            .isEqualTo(boolB(true));
      }

      @Test
      public void int_() {
        assertThat(evaluate(intB(8)))
            .isEqualTo(intB(8));
      }

      @Test
      public void string() {
        assertThat(evaluate(stringB("abc")))
            .isEqualTo(stringB("abc"));
      }

      @Test
      public void tuple() {
        assertThat(evaluate(tupleB(intB(7))))
            .isEqualTo(tupleB(intB(7)));
      }
    }

    @Nested
    class _operators {
      @Nested
      class _call {
        @Test
        public void closure() {
          var closure = closureB(intB(7));
          var call = callB(closure);
          assertThat(evaluate(call))
              .isEqualTo(intB(7));
        }

        @Test
        public void closure_with_environment() {
          var closure = closureB(combineB(intB(7)), referenceB(intTB(), 0));
          var call = callB(closure);
          assertThat(evaluate(call))
              .isEqualTo(intB(7));
        }

        @Test
        public void expression_function() {
          var func = exprFuncB(intB(7));
          var call = callB(func);
          assertThat(evaluate(call))
              .isEqualTo(intB(7));
        }

        @Test
        public void expression_function_passed_as_argument() {
          var func = exprFuncB(intB(7));
          var paramT = func.evaluationT();
          var outerFunc = exprFuncB(list(paramT), callB(referenceB(paramT, 0)));
          var call = callB(outerFunc, func);
          assertThat(evaluate(call))
              .isEqualTo(intB(7));
        }

        @Test
        public void expression_function_returned_from_call() {
          var func = exprFuncB(intB(7));
          var outerFunc = exprFuncB(func);
          var call = callB(callB(outerFunc));
          assertThat(evaluate(call))
              .isEqualTo(intB(7));
        }

        @Test
        public void if_function_with_true_condition() {
          var ifFunc = ifFuncB(intTB());
          var call = callB(ifFunc, boolB(true), intB(7), intB(0));
          assertThat(evaluate(call))
              .isEqualTo(intB(7));
        }

        @Test
        public void if_func_with_false_condition() {
          var ifFunc = ifFuncB(intTB());
          var call = callB(ifFunc, boolB(false), intB(7), intB(0));
          assertThat(evaluate(call))
              .isEqualTo(intB(0));
        }

        @Test
        public void map_func() {
          var s = intTB();
          var r = tupleTB(s);
          var func = exprFuncB(funcTB(s, r), combineB(referenceB(s, 0)));
          var mapFunc = mapFuncB(r, s);
          var map = callB(mapFunc, arrayB(intB(1), intB(2)), func);
          assertThat(evaluate(map))
              .isEqualTo(arrayB(tupleB(intB(1)), tupleB(intB(2))));
        }

        @Test
        public void native_func() throws Exception {
          var nativeFuncB = nativeFuncB(
              funcTB(intTB(), intTB()), blobB(77), stringB("classBinaryName"));
          var call = callB(nativeFuncB, intB(33));
          var nativeMethodLoader = mock(NativeMethodLoader.class);
          when(nativeMethodLoader.load(eq(nativeFuncB)))
              .thenReturn(Try.result(
                  EvaluatorBTest.class.getMethod("returnIntParam", NativeApi.class, TupleB.class)));
          assertThat(evaluate(evaluatorB(nativeMethodLoader), call))
              .isEqualTo(intB(33));
        }

        @Test
        public void native_func_passed_as_arg() throws NoSuchMethodException {
          var nativeFuncB = nativeFuncB(
              funcTB(intTB(), intTB()), blobB(77), stringB("classBinaryName"));
          var nativeMethodLoader = mock(NativeMethodLoader.class);
          when(nativeMethodLoader.load(eq(nativeFuncB)))
              .thenReturn(Try.result(
                  EvaluatorBTest.class.getMethod("returnIntParam", NativeApi.class, TupleB.class)));

          var nativeFuncT = nativeFuncB.evaluationT();
          var outerFunc = exprFuncB(list(nativeFuncT), callB(referenceB(nativeFuncT, 0), intB(7)));
          var call = callB(outerFunc, nativeFuncB);
          assertThat(evaluate(evaluatorB(nativeMethodLoader), call))
              .isEqualTo(intB(7));
        }

        @Test
        public void native_func_returned_from_call() throws NoSuchMethodException {
          var nativeFuncB = nativeFuncB(
              funcTB(intTB(), intTB()), blobB(77), stringB("classBinaryName"));
          var nativeMethodLoader = mock(NativeMethodLoader.class);
          when(nativeMethodLoader.load(eq(nativeFuncB)))
              .thenReturn(Try.result(
                  EvaluatorBTest.class.getMethod("returnIntParam", NativeApi.class, TupleB.class)));

          var outerFunc = exprFuncB(nativeFuncB);
          var call = callB(callB(outerFunc), intB(7));
          assertThat(evaluate(evaluatorB(nativeMethodLoader), call))
              .isEqualTo(intB(7));
        }
      }

      @Test
      public void combine() {
        var combine = combineB(intB(7));
        assertThat(evaluate(combine))
            .isEqualTo(tupleB(intB(7)));
      }

      @Nested
      class _closurize {
        @Test
        public void const_func() {
          var closure = closurizeB(intB(17));
          var call = callB(closure);
          assertThat(evaluate(call))
              .isEqualTo(intB(17));
        }

        @Test
        public void closure_returning_its_arg() {
          var closurize = closurizeB(list(intTB()), referenceB(intTB(), 0));
          var outerFunc = exprFuncB(list(intTB()), closurize);
          var closureReturnedByOuterFunc = callB(outerFunc, intB(17));
          var callB = callB(closureReturnedByOuterFunc, intB(18));
          assertThat(evaluate(callB))
              .isEqualTo(intB(18));
        }

        @Test
        public void closure_returning_value_from_environment() {
          var closurize = closurizeB(referenceB(intTB(), 0));
          var outerFunc = exprFuncB(list(intTB()), closurize);
          var closureReturnedByOuterFunc = callB(outerFunc, intB(17));
          assertThat(evaluate(callB(closureReturnedByOuterFunc)))
              .isEqualTo(intB(17));
        }

        @Test
        public void closure_passed_as_argument_and_then_returned_by_another_closure() {
          // innerFunc(()->Int f) = () -> f;
          // Int outerFunc(Int i) = innerFunc(() -> i)()();
          // outerFunc(17);
          var funcReturningIntTB = funcTB(intTB());
          var closureReturningFuncReturningInt = closurizeB(referenceB(funcReturningIntTB, 0));
          var innerFunc = exprFuncB(list(funcReturningIntTB), closureReturningFuncReturningInt);

          var returnIntLambda = closurizeB(referenceB(intTB(), 0));
          var body = callB(callB(callB(innerFunc, returnIntLambda)));
          var outerFunc = exprFuncB(list(intTB()), body);

          var callB = callB(outerFunc, intB(17));
          assertThat(evaluate(callB))
              .isEqualTo(intB(17));
        }

        @Test
        public void closure_returning_value_from_environment_that_references_another_environment() {
          var closurize = closurizeB(referenceB(intTB(), 0));
          var innerFunc = exprFuncB(list(intTB()), closurize);
          var outerFunc = exprFuncB(list(intTB()), callB(innerFunc, referenceB(intTB(), 0)));
          var closureReturnedByOuterFunc = callB(outerFunc, intB(17));
          assertThat(evaluate(callB(closureReturnedByOuterFunc)))
              .isEqualTo(intB(17));
        }
      }

      @Test
      public void order() {
        var order = orderB(intB(7), intB(8));
        assertThat(evaluate(order))
            .isEqualTo(arrayB(intB(7), intB(8)));
      }

      @Nested
      class _pick {
        @Test
        public void pick() {
          var tuple = arrayB(intB(10), intB(11), intB(12), intB(13));
          var pick = pickB(tuple, intB(2));
          assertThat(evaluate(pick))
              .isEqualTo(intB(12));
        }

        @Test
        public void pick_with_index_outside_of_bounds() {
          var pick = pickB(
              arrayB(intB(10), intB(11), intB(12), intB(13)),
              intB(4));
          var memoryReporter = new MemoryReporter();
          evaluateWithFailure(evaluatorB(memoryReporter), pick);
          assertThat(memoryReporter.logs())
              .isEqualTo(logs(error("Index (4) out of bounds. Array size = 4.")));
        }

        @Test
        public void pick_with_index_negative() {
          var pick = pickB(
              arrayB(intB(10), intB(11), intB(12), intB(13)),
              intB(-1));
          var memoryReporter = new MemoryReporter();
          evaluateWithFailure(evaluatorB(memoryReporter), pick);
          assertThat(memoryReporter.logs())
              .isEqualTo(logs(error("Index (-1) out of bounds. Array size = 4.")));
        }
      }

      @Nested
      class _reference {
        @Test
        public void reference_referencing_func_param() {
          var exprFuncB = exprFuncB(list(intTB()), referenceB(intTB(), 0));
          assertThat(evaluate(callB(exprFuncB, intB(7))))
              .isEqualTo(intB(7));
        }

        @Test
        public void reference_referencing_environment() {
          var body = referenceB(intTB(), 1);
          var closureB = closureB(combineB(intB(17)), list(intTB()), body);
          assertThat(evaluate(callB(closureB, intB(7))))
              .isEqualTo(intB(17));
        }

        @Test
        public void reference_with_index_outside_of_environment_size_causes_fatal()
            throws InterruptedException {
          var closureB = closureB(combineB(intB()), list(intTB()), referenceB(intTB(), 2));
          var reporter = mock(Reporter.class);
          var vm = evaluatorB(reporter);
          vm.evaluate(list(callB(closureB, intB(7))));
          verify(reporter, times(1))
              .report(eq("Internal smooth error"), argThat(isLogListWithFatalOutOfBounds()));
        }

        @Test
        public void reference_inside_inner_func_cannot_access_params_of_func_that_called_inner_func()
            throws InterruptedException {
          var innerFuncB = exprFuncB(list(), referenceB(intTB(), 0));
          var outerFuncB = exprFuncB(list(intTB()), callB(innerFuncB));
          var reporter = mock(Reporter.class);
          var vm = evaluatorB(reporter);
          vm.evaluate(list(callB(outerFuncB, intB(7))));
          verify(reporter, times(1))
              .report(eq("Internal smooth error"), argThat(isLogListWithFatalOutOfBounds()));
        }

        private ArgumentMatcher<List<Log>> isLogListWithFatalOutOfBounds() {
          return isLogListWithFatalMessageStartingWith(
              "Computation failed with: java.lang.ArrayIndexOutOfBoundsException");
        }

        @Test
        public void reference_with_eval_type_different_than_actual_environment_value_eval_type_causes_fatal()
            throws InterruptedException {
          var funcB = exprFuncB(list(blobTB()), referenceB(intTB(), 0));
          var reporter = mock(Reporter.class);
          var vm = evaluatorB(reporter);
          vm.evaluate(list(callB(funcB, blobB())));
          verify(reporter, times(1))
              .report(
                  eq("Internal smooth error"),
                  argThat(isLogListWithFatalWrongEnvironmentType()));
        }

        private ArgumentMatcher<List<Log>> isLogListWithFatalWrongEnvironmentType() {
          return isLogListWithFatalMessageStartingWith("Computation failed with: "
              + "java.lang.RuntimeException: environment(0) evaluationT is `Blob`"
              + " but expected `Int`");
        }
      }

      @Test
      public void select() {
        var tuple = tupleB(intB(7));
        var select = selectB(tuple, intB(0));
        assertThat(evaluate(select))
            .isEqualTo(intB(7));
      }
    }

    @Nested
    class _errors {
      @Test
      public void task_throwing_runtime_exception_causes_fatal() throws Exception {
        var taskReporter = mock(TaskReporter.class);
        var schedulerB = schedulerB(taskReporter, 4);
        var exprB = throwExceptionCall();
        evaluateWithFailure(new EvaluatorB(() -> schedulerB), exprB);
        verify(taskReporter).report(
            any(),
            argThat(this::computationResultWithFatalCausedByRuntimeException));
      }

      private boolean computationResultWithFatalCausedByRuntimeException(ComputationResult result) {
        ArrayB messages = result.output().messages();
        return messages.size() == 1
            && MessageStruct.level(messages.elems(TupleB.class).get(0)) == FATAL
            && MessageStruct.text(messages.elems(TupleB.class).get(0)).startsWith(
            "Native code thrown exception:\njava.lang.ArithmeticException");

      }

      private CallB throwExceptionCall() throws IOException {
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
      public void computer_that_throws_exception_is_detected() {
        var reporter = mock(Reporter.class);
        var exprB = stringB("abc");
        var runtimeException = new RuntimeException();
        var computer = new Computer(null, null, null) {
          @Override
          public void compute(Task task, TupleB input, Consumer<ComputationResult> consumer) {
            throw runtimeException;
          }
        };
        var schedulerB = schedulerB(computer, reporter, 4);

        evaluateWithFailure(new EvaluatorB(() -> schedulerB), exprB);
        verify(reporter, times(1))
            .report(eq("Internal smooth error"), argThat(isLogListWithFatalM()));
      }

      private ArgumentMatcher<List<Log>> isLogListWithFatalM() {
        return isLogListWithFatalMessageStartingWith(
            "Computation failed with: java.lang.RuntimeException");
      }
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
      public void report_value_as_const_task(ValueB valueB) {
        var taskReporter = mock(TaskReporter.class);
        evaluate(evaluatorB(taskReporter), valueB);
        verify(taskReporter)
            .report(constTask(valueB, null), computationResult(valueB, NOOP));
      }

      public static List<ValueB> report_const_task_cases() {
        var t = new TestContext();
        return List.of(
            t.arrayB(t.intB(17)),
            t.blobB(17),
            t.boolB(true),
            t.idFuncB(),
            t.ifFuncB(t.intTB()),
            t.mapFuncB(t.intTB(), t.blobTB()),
            t.nativeFuncB(),
            t.intB(17),
            t.stringB("abc"),
            t.tupleB(t.intB(17))
        );
      }

      @Test
      public void report_native_call_as_invoke_task() throws IOException {
        var funcB = returnAbcNativeFunc();
        var callB = callB(funcB);
        assertReport(
            callB,
            invokeTask(callB, funcB, traceB(callB, funcB)),
            computationResult(stringB("abc"), EXECUTION));
      }

      @Test
      public void report_combine_as_combine_task() {
        var combineB = combineB(intB(17));
        assertReport(
            combineB,
            combineTask(combineB, null),
            computationResult(tupleB(intB(17)), EXECUTION));
      }

      @Test
      public void report_order_as_order_task() {
        var orderB = orderB(intB(17));
        assertReport(
            orderB,
            orderTask(orderB, null),
            computationResult(arrayB(intB(17)), EXECUTION));
      }

      @Test
      public void report_pick_as_pick_task() {
        var pickB = pickB(arrayB(intB(17)), intB(0));
        assertReport(
            pickB,
            pickTask(pickB, null),
            computationResult(intB(17), EXECUTION));
      }

      @Test
      public void report_select_as_select_task() {
        var selectB = selectB(tupleB(intB(17)), intB(0));
        assertReport(
            selectB,
            selectTask(selectB, null),
            computationResult(intB(17), EXECUTION));
      }
    }

    @Nested
    class _with_traces {
      @Test
      public void order_inside_func_body() {
        var orderB = orderB(intB(17));
        var funcB = exprFuncB(orderB);
        var funcAsExpr = callB(exprFuncB(funcB));
        var callB = callB(funcAsExpr);
        assertReport(
            callB,
            orderTask(orderB, traceB(callB, funcB)),
            computationResult(arrayB(intB(17)), EXECUTION));
      }

      @Test
      public void order_inside_func_body_that_is_called_from_other_func_body() {
        var orderB = orderB(intB(17));
        var func2 = exprFuncB(orderB);
        var call2 = callB(func2);
        var func1 = exprFuncB(call2);
        var call1 = callB(func1);
        assertReport(
            call1,
            orderTask(orderB, traceB(call2, func2, traceB(call1, func1))),
            computationResult(arrayB(intB(17)), EXECUTION));
      }
    }

    private void assertReport(ExprB exprB, Task task, ComputationResult result) {
      var taskReporter = mock(TaskReporter.class);
      evaluate(evaluatorB(taskReporter), exprB);
      verify(taskReporter)
          .report(task, result);
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
      assertThat(evaluate(expr))
          .isEqualTo(arrayB(stringB("11"), stringB("21")));
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
          commandCall(testName, "INC1")
      );

      var reporter = mock(TaskReporter.class);
      var vm = new EvaluatorB(() -> schedulerB(reporter, 4));
      assertThat(evaluate(vm, exprB))
          .isEqualTo(arrayB(stringB("1"), stringB("1"), stringB("1"), stringB("1")));

      verifyConstTasksResSource(4, DISK, reporter);
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

      var vm = new EvaluatorB(() -> schedulerB(2));
      assertThat(evaluate(vm, exprB))
          .isEqualTo(arrayB(stringB("1"), stringB("1"), stringB("0")));
    }

    private CallB commandCall(String testName, String commands) throws IOException {
      return commandCall(testName, commands, true);
    }

    private CallB commandCall(String testName, String commands, boolean isPure) throws IOException {
      var nativeFuncB = nativeFuncB(
          funcTB(stringTB(), stringTB(), stringTB()), ExecuteCommands.class, isPure);
      return callB(nativeFuncB, stringB(testName), stringB(commands));
    }

    public static class ExecuteCommands {
      public static ValueB func(NativeApi nativeApi, TupleB args) {
        String name = ((StringB) args.get(0)).toJ();
        String commands = ((StringB) args.get(1)).toJ();
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
    try {
      var resultOptional = evaluatorB.evaluate(list(expr));
      assertWithMessage(" ==== Console logs ==== \n" + systemOut().toString() + "\n ==========\n")
          .that(resultOptional.isPresent())
          .isTrue();
      var results = resultOptional.get();
      assertThat(results.size())
          .isEqualTo(1);
      return results.get(0);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void evaluateWithFailure(EvaluatorB evaluatorB, ExprB expr) {
    try {
      var results = evaluatorB.evaluate(list(expr));
      assertThat(results)
          .isEqualTo(Optional.empty());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static IntB returnIntParam(NativeApi nativeApi, TupleB args) {
    return (IntB) args.get(0);
  }

  private static void verifyConstTasksResSource(
      int size, ResultSource expectedSource, TaskReporter reporter) {
    var argCaptor = ArgumentCaptor.forClass(ComputationResult.class);
    verify(reporter, times(size)).report(taskMatcher(), argCaptor.capture());
    var resSources = map(argCaptor.getAllValues(), ComputationResult::source);
    assertThat(resSources)
        .containsExactlyElementsIn(resSourceList(size, expectedSource));
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
    return new CountingSchedulerB(
        taskExecutor(), bytecodeF(), nativeMethodLoader(), environmentInliner());
  }

  private static class CountingSchedulerB extends SchedulerB {
    private final ConcurrentHashMap<Class<?>, AtomicInteger> counters = new ConcurrentHashMap<>();

    public CountingSchedulerB(
        TaskExecutor taskExecutor,
        BytecodeF bytecodeF,
        NativeMethodLoader nativeMethodLoader,
        ReferenceInlinerB referenceInlinerB) {
      super(taskExecutor, bytecodeF, nativeMethodLoader, referenceInlinerB);
    }

    @Override
    protected Job newJob(ExprB exprB, ImmutableList<Job> environment, TraceB trace) {
      counters.computeIfAbsent(exprB.getClass(), k -> new AtomicInteger())
          .incrementAndGet();
      return super.newJob(exprB, environment, trace);
    }

    public ConcurrentHashMap<Class<?>, AtomicInteger> counters() {
      return counters;
    }
  }
}
