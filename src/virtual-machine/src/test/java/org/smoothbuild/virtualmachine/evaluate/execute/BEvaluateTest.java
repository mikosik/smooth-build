package org.smoothbuild.virtualmachine.evaluate.execute;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static java.util.Collections.nCopies;
import static java.util.Collections.synchronizedList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.collect.Result.ok;
import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.common.log.base.Level.FATAL;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.Origin.DISK;
import static org.smoothbuild.common.log.base.Origin.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Scheduler.LABEL;
import static org.smoothbuild.common.schedule.Tasks.argument;
import static org.smoothbuild.common.testing.AwaitHelper.await;
import static org.smoothbuild.common.tuple.Tuples.tuple;
import static org.smoothbuild.virtualmachine.VmConstants.VM_EVALUATE;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.common.log.base.Origin;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Scheduler;
import org.smoothbuild.common.testing.TestReporter;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMethod;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.load.NativeMethodLoader;
import org.smoothbuild.virtualmachine.evaluate.compute.StepEvaluator;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;
import org.smoothbuild.virtualmachine.evaluate.step.Step;
import org.smoothbuild.virtualmachine.testing.VmTestContext;
import org.smoothbuild.virtualmachine.testing.func.nativ.ConcatStrings;

public class BEvaluateTest extends VmTestContext {
  public static final ConcurrentHashMap<String, AtomicInteger> COUNTERS = new ConcurrentHashMap<>();
  public static final ConcurrentHashMap<String, CountDownLatch> COUNTDOWNS =
      new ConcurrentHashMap<>();

  @Nested
  class _laziness {
    @Nested
    class _task_execution {
      @Test
      void learning_test() throws Exception {
        // This test makes sure that it is possible to detect Task creation using a mock.
        var testName = detectEnclosingMethodName();
        var nativeMethodLoader = nativeMethodLoaderThatAlwaysLoadsMemoizeString();
        var invoke = bInvoke(bStringType(), bMethodTuple(), bTuple(bString(testName)));

        evaluate(bEvaluate(nativeMethodLoader), invoke);

        assertThat(MEMOIZED).contains(testName);
      }

      @Test
      void no_task_is_executed_for_lambda_arg_that_is_not_used() throws Exception {
        var testName = detectEnclosingMethodName();
        var nativeMethodLoader = nativeMethodLoaderThatAlwaysLoadsMemoizeString();
        var invoke = bInvoke(bStringType(), bMethodTuple(), bTuple(bString(testName)));
        var lambda = bLambda(list(bStringType()), bInt(7));
        var call = bCall(lambda, invoke);

        evaluate(bEvaluate(nativeMethodLoader), call);

        assertThat(MEMOIZED).doesNotContain(testName);
      }

      @Test
      void no_task_is_executed_for_lambda_arg_that_is_passed_to_lambda_where_it_is_not_used()
          throws Exception {
        var testName = detectEnclosingMethodName();
        var nativeMethodLoader = nativeMethodLoaderThatAlwaysLoadsMemoizeString();
        var invoke = bInvoke(bStringType(), bMethodTuple(), bTuple(bString(testName)));
        var innerLambda = bLambda(list(bStringType()), bInt(7));
        var outerLambda =
            bLambda(list(bStringType()), bCall(innerLambda, bReference(bStringType(), 0)));
        var call = bCall(outerLambda, invoke);

        evaluate(bEvaluate(nativeMethodLoader), call);

        assertThat(MEMOIZED).doesNotContain(testName);
      }

      @Test
      void task_for_lambda_arg_that_is_used_twice_is_executed_only_once() throws Exception {
        var testName = detectEnclosingMethodName();
        var nativeMethodLoader = nativeMethodLoaderThatAlwaysLoadsMemoizeString();
        var invoke = bInvoke(bStringType(), bMethodTuple(), bTuple(bString(testName)));
        var type = bStringType();
        var lambda = bLambda(list(type), bCombine(bReference(type, 0), bReference(type, 0)));
        var call = bCall(lambda, invoke);

        evaluate(bEvaluate(nativeMethodLoader), call);

        assertThat(MEMOIZED.stream().filter(x -> x.equals(testName)).toList()).hasSize(1);
      }

      @Test
      void if_else_is_not_evaluated_when_condition_is_true() throws Exception {
        var testName = detectEnclosingMethodName();
        var nativeMethodLoader = nativeMethodLoaderThatAlwaysLoadsMemoizeString();
        var invoke = bInvoke(bStringType(), bMethodTuple(), bTuple(bString(testName)));
        var if_ = bIf(bBool(true), bString("then"), invoke);

        evaluate(bEvaluate(nativeMethodLoader), if_);

        assertThat(MEMOIZED).doesNotContain(testName);
      }

      @Test
      void if_then_is_not_evaluated_when_condition_is_false() throws Exception {
        var testName = detectEnclosingMethodName();
        var nativeMethodLoader = nativeMethodLoaderThatAlwaysLoadsMemoizeString();
        var invoke = bInvoke(bStringType(), bMethodTuple(), bTuple(bString(testName)));
        var if_ = bIf(bBool(false), invoke, bString("else"));

        evaluate(bEvaluate(nativeMethodLoader), if_);

        assertThat(MEMOIZED).doesNotContain(testName);
      }

      @Test
      void switch_handler_that_is_not_taken_is_not_evaluated() throws Exception {
        var testName = detectEnclosingMethodName();
        var nativeMethodLoader = nativeMethodLoaderThatAlwaysLoadsMemoizeString();
        var invoke = bInvoke(bStringType(), bMethodTuple(), bTuple(bString(testName)));
        var lambda = bLambda(list(bStringType()), invoke);
        var choice = bChoice(bChoiceType(), 1, bInt());
        var switch_ = bSwitch(choice, bCombine(lambda, bi2sLambda()));

        evaluate(bEvaluate(nativeMethodLoader), switch_);

        assertThat(MEMOIZED).doesNotContain(testName);
      }
    }

    @Nested
    class _job {
      @Test
      void learning_test() throws Exception {
        // Learning test verifies that job creation is counted also inside lambda body.
        var bInt = bInt(7);
        var lambda = bLambda(bOrder(bInt));
        var call = bCall(lambda);

        var countingBEvaluate = countingBEvaluate();
        assertThat(evaluate(countingBEvaluate, call).get().get()).isEqualTo(bArray(bInt));

        assertThat(countingBEvaluate.counters().get(bInt).intValue()).isEqualTo(1);
      }

      @Test
      void job_for_unused_lambda_arg_is_created_but_not_jobs_for_its_dependencies()
          throws Exception {
        var lambda = bLambda(list(bBoolArrayType()), bInt(7));
        var bBool = bBool();
        var call = bCall(lambda, bOrder(bBool));

        var countingBEvaluate = countingBEvaluate();
        assertThat(evaluate(countingBEvaluate, call).get().get()).isEqualTo(bInt(7));

        assertThat(countingBEvaluate.counters().get(bBool)).isNull();
      }

      @Test
      void lambda_arg_used_twice_not_results_in_its_expression_being_evaluated_twice()
          throws Exception {
        var boolArrayType = bBoolArrayType();
        var argReference = bReference(boolArrayType, 0);
        var lambda = bLambda(list(boolArrayType), bOrder(argReference, argReference));
        var bool = bBool();
        var call = bCall(lambda, bOrder(bool));

        var countingBEvaluate = countingBEvaluate();
        var expected = bArray(bArray(bool), bArray(bool));
        assertThat(evaluate(countingBEvaluate, call).get().get()).isEqualTo(expected);

        assertThat(countingBEvaluate.counters().get(bool).get()).isEqualTo(1);
      }
    }
  }

  @Nested
  class _evaluation {
    @Nested
    class _values {
      @Test
      void array() throws Exception {
        assertThat(evaluate(bArray(bInt(7)))).isEqualTo(bArray(bInt(7)));
      }

      @Test
      void blob() throws Exception {
        assertThat(evaluate(bBlob(7))).isEqualTo(bBlob(7));
      }

      @Test
      void bool() throws Exception {
        assertThat(evaluate(bBool(true))).isEqualTo(bBool(true));
      }

      @Test
      void choice() throws Exception {
        var type = bChoiceType(bStringType(), bIntType());
        var choice = bChoice(type, bInt(0), bString("7"));
        assertThat(evaluate(choice)).isEqualTo(choice);
      }

      @Test
      void int_() throws Exception {
        assertThat(evaluate(bInt(8))).isEqualTo(bInt(8));
      }

      @Test
      void string() throws Exception {
        assertThat(evaluate(bString("abc"))).isEqualTo(bString("abc"));
      }

      @Test
      void tuple() throws Exception {
        assertThat(evaluate(bTuple(bInt(7)))).isEqualTo(bTuple(bInt(7)));
      }
    }

    @Nested
    class _operation {
      @Nested
      class _call {
        @Test
        void lambda_without_arguments() throws Exception {
          var lambda = bLambda(bInt(7));
          var call = bCall(lambda);
          assertThat(evaluate(call)).isEqualTo(bInt(7));
        }

        @Test
        void lambda_with_single_argument_passed_inside_combine() throws Exception {
          var lambda = bIntIdLambda();
          var call = bCallWithArguments(lambda, bTuple(bInt(7)));
          assertThat(evaluate(call)).isEqualTo(bInt(7));
        }

        @Test
        void lambda_with_single_argument_passed_inside_tuple() throws Exception {
          var lambda = bIntIdLambda();
          var call = bCallWithArguments(lambda, bCombine(bInt(7)));
          assertThat(evaluate(call)).isEqualTo(bInt(7));
        }

        @Test
        void lambda_with_single_argument_passed_as_expression_that_evaluates_to_tuple()
            throws Exception {
          var lambda = bIntIdLambda();
          var call = bCallWithArguments(lambda, bPick(bOrder(bTuple(bInt(7))), 0));
          assertThat(evaluate(call)).isEqualTo(bInt(7));
        }

        @Test
        void lambda_passed_as_argument() throws Exception {
          var paramLambda = bLambda(bInt(7));
          var paramLambdaType = paramLambda.evaluationType();
          var outerLambda = bLambda(list(paramLambdaType), bCall(bReference(paramLambdaType, 0)));
          var call = bCall(outerLambda, paramLambda);
          assertThat(evaluate(call)).isEqualTo(bInt(7));
        }

        @Test
        void lambda_returned_from_call() throws Exception {
          var innerLambda = bLambda(bInt(7));
          var outerLambda = bLambda(innerLambda);
          var call = bCall(bCall(outerLambda));
          assertThat(evaluate(call)).isEqualTo(bInt(7));
        }

        @Test
        void lambda_returning_param_of_enclosing_lambda() throws Exception {
          var innerLambda = bLambda(bReference(bIntType(), 0));
          var outerLambda = bLambda(list(bIntType()), innerLambda);
          var callToOuter = bCall(outerLambda, bInt(17));
          var callToInnerReturnedByOuter = bCall(callToOuter);
          assertThat(evaluate(callToInnerReturnedByOuter)).isEqualTo(bInt(17));
        }

        @Test
        void lambda_returning_value_from_environment_that_references_another_environment()
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
      void combine() throws Exception {
        var combine = bCombine(bInt(7));
        assertThat(evaluate(combine)).isEqualTo(bTuple(bInt(7)));
      }

      @Test
      void choose() throws Exception {
        var type = bChoiceType(bStringType(), bIntType());
        var choose = bChoose(type, bInt(0), bSelect(bCombine(bString("7")), 0));
        assertThat(evaluate(choose)).isEqualTo(bChoice(type, 0, bString("7")));
      }

      @Test
      void switch_() throws Exception {
        var type = bChoiceType(bStringType(), bIntType());
        var choice = bChoice(type, bInt(0), bString("7"));
        var tupelizeString = bLambda(list(bStringType()), bCombine(bReference(bStringType(), 0)));
        var intToTuple = bLambda(list(bIntType()), bTuple(bString("x")));
        var switch_ = bSwitch(choice, bCombine(tupelizeString, intToTuple));
        assertThat(evaluate(switch_)).isEqualTo(bTuple(bString("7")));
      }

      @Test
      void if_with_true_condition() throws Exception {
        var if_ = bIf(bBool(true), bInt(1), bInt(2));
        assertThat(evaluate(if_)).isEqualTo(bInt(1));
      }

      @Test
      void if_with_false_condition() throws Exception {
        var if_ = bIf(bBool(false), bInt(1), bInt(2));
        assertThat(evaluate(if_)).isEqualTo(bInt(2));
      }

      @Test
      void invoke() throws Exception {
        var methodTuple = bMethodTuple();
        var invoke = bInvoke(bIntType(), methodTuple, bTuple(bInt(33)));
        var nativeMethodLoader = mock(NativeMethodLoader.class);
        when(nativeMethodLoader.load(eq(new BMethod(methodTuple))))
            .thenReturn(
                ok(BEvaluateTest.class.getMethod("returnIntParam", NativeApi.class, BTuple.class)));
        assertThat(evaluate(bEvaluate(nativeMethodLoader), invoke).get().get()).isEqualTo(bInt(33));
      }

      @Test
      void map() throws Exception {
        var array = bArray(bInt(1), bInt(4));
        var mapper = bLambda(list(bIntType()), bCombine(bReference(bIntType(), 0)));
        var map = bMap(array, mapper);
        assertThat(evaluate(map)).isEqualTo(bArray(bTuple(bInt(1)), bTuple(bInt(4))));
      }

      @Test
      void map_with_empty_array() throws Exception {
        var map = bMap(bArray(bIntType()), bIntIdLambda());
        assertThat(evaluate(map)).isEqualTo(bArray(bIntType()));
      }

      @Test
      void fold() throws Exception {
        var arguments = bCombine(bReference(bStringType(), 0), bReference(bStringType(), 1));
        var body = bInvoke(bStringType(), ConcatStrings.class, true, arguments);
        var folder = bLambda(list(bStringType(), bStringType()), body);

        var array = bArray(bString("a"), bString("b"), bString("c"));
        var initial = bString("0");

        var fold = bytecodeF().fold(array, initial, folder);
        assertThat(evaluate(fold)).isEqualTo(bString("0abc"));
      }

      @Test
      void fold_empty_array() throws Exception {
        var arguments = bCombine(bReference(0), bReference(1));
        var body = bInvoke(bStringType(), ConcatStrings.class, true, arguments);
        var folder = bLambda(list(bStringType(), bStringType()), body);

        var array = bArray(bStringType());
        var initial = bString("0");

        var fold = bytecodeF().fold(array, initial, folder);
        assertThat(evaluate(fold)).isEqualTo(bString("0"));
      }

      @Test
      void order() throws Exception {
        var order = bOrder(bInt(7), bInt(8));
        assertThat(evaluate(order)).isEqualTo(bArray(bInt(7), bInt(8)));
      }

      @Nested
      class _pick {
        @Test
        void pick() throws Exception {
          var tuple = bArray(bInt(10), bInt(11), bInt(12), bInt(13));
          var pick = bPick(tuple, bInt(2));
          assertThat(evaluate(pick)).isEqualTo(bInt(12));
        }

        @Test
        void pick_with_index_outside_of_bounds() throws Exception {
          var pick = bPick(bArray(bInt(10), bInt(11), bInt(12), bInt(13)), bInt(4));
          evaluate(bEvaluate(), pick);
          if (!reporter().reports().anyMatches(this::isResultWithIndexOutOfBoundsError)) {
            fail("Expected report ERROR caused by index out of bounds but got:\n" + reporter());
          }
        }

        public boolean isResultWithIndexOutOfBoundsError(Report report) {
          return taskReportWith(report, ERROR, "Index (4) out of bounds. Array size = 4.");
        }

        @Test
        void pick_with_index_negative() throws Exception {
          var pick = bPick(bArray(bInt(10), bInt(11), bInt(12), bInt(13)), bInt(-1));
          evaluate(bEvaluate(), pick);
          if (!reporter().reports().anyMatches(this::isResultWithNegativeIndexError)) {
            fail("Expected report with ERROR caused by index out of bounds, but got:\n"
                + reporter());
          }
        }

        public boolean isResultWithNegativeIndexError(Report report) {
          return taskReportWith(report, ERROR, "Index (-1) out of bounds. Array size = 4.");
        }
      }

      @Nested
      class _reference {
        @Test
        void var_referencing_lambda_param() throws Exception {
          var lambda = bLambda(list(bIntType()), bReference(bIntType(), 0));
          var callB = bCall(lambda, bInt(7));
          assertThat(evaluate(callB)).isEqualTo(bInt(7));
        }

        @Test
        void var_inside_call_to_inner_lambda_referencing_param_of_enclosing_lambda()
            throws Exception {
          var innerLambda = bLambda(list(), bReference(bIntType(), 0));
          var outerLambda = bLambda(list(bIntType()), bCall(innerLambda));
          assertThat(evaluate(bCall(outerLambda, bInt(7)))).isEqualTo(bInt(7));
        }

        @Test
        void var_inside_inner_lambda_referencing_param_of_enclosing_lambda() throws Exception {
          var innerLambda = bLambda(list(bIntType()), bReference(bIntType(), 1));
          var outerLambda = bLambda(list(bIntType()), innerLambda);
          var callOuter = bCall(outerLambda, bInt(7));
          var callInner = bCall(callOuter, bInt(8));

          assertThat(evaluate(callInner)).isEqualTo(bInt(7));
        }

        @Test
        void var_referencing_with_index_out_of_bounds_causes_fatal() throws Exception {
          var lambda = bLambda(list(bIntType()), bReference(bIntType(), 2));
          var call = bCall(lambda, bInt(7));
          evaluate(bEvaluate(), call);
          var reports = reporter().reports();
          assertReportsContains(
              reports,
              FATAL,
              "Task execution failed with exception:\n"
                  + "org.smoothbuild.virtualmachine.evaluate.execute.ReferenceIndexOutOfBoundsException:"
                  + " Reference index = 2 is out of bounds. Bound variables size = 1.");
        }

        @Test
        void
            reference_with_eval_type_different_than_actual_environment_value_eval_type_causes_fatal()
                throws Exception {
          var lambda = bLambda(list(bBlobType()), bReference(bIntType(), 0));
          var call = bCall(lambda, bBlob());
          evaluate(bEvaluate(), call);
          assertReportsContains(
              reporter().reports(),
              FATAL,
              "Task execution failed with exception:\n"
                  + "java.lang.RuntimeException: environment(0) evaluationType is `Blob` but expected `Int`.");
        }
      }

      @Test
      void select() throws Exception {
        var tuple = bTuple(bInt(7));
        var select = bSelect(tuple, bInt(0));
        assertThat(evaluate(select)).isEqualTo(bInt(7));
      }
    }

    @Nested
    class _errors {
      @Test
      void task_throwing_runtime_exception_causes_fatal() throws Exception {
        var scheduler = scheduler();
        var bEvaluate = bEvaluate(scheduler);
        var expr = throwExceptionCall();
        evaluate(bEvaluate, expr);

        List<Report> reports = reporter().reports();
        if (!reports.anyMatches(this::reportWithFatalCausedByRuntimeException)) {
          fail("Expected FATAL report caused by ArithmeticException but got:\n===\n" + reporter()
              + "\n===\n");
        }
      }

      private boolean reportWithFatalCausedByRuntimeException(Report r) {
        return reportWith(r, FATAL, "Native code thrown exception:\njava.lang.ArithmeticException");
      }

      private BCall throwExceptionCall() throws Exception {
        var lambdaType = bStringLambdaType();
        var invoke = bInvoke(lambdaType, ThrowException.class);
        return bCall(invoke);
      }

      public static class ThrowException {
        public static BValue func(NativeApi nativeApi, BTuple args) {
          throw new ArithmeticException();
        }
      }

      @Test
      void step_evaluator_that_throws_exception_is_detected() throws Exception {
        var expr = bOrder();
        var runtimeException = new RuntimeException();
        var scheduler = scheduler();
        var stepEvaluator = new StepEvaluator(null, null, null, scheduler, bytecodeF()) {
          @Override
          public Output<BValue> evaluateStep(Step task, BTuple input) {
            throw runtimeException;
          }
        };
        var bEvaluate = bEvaluate(stepEvaluator);

        evaluate(bEvaluate, expr);
        var fatal = fatal("Task execution failed with exception:", runtimeException);
        assertThat(reporter().reports()).contains(report(LABEL, list(fatal)));
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
      @Test
      void report_invoke_as_invoke_task() throws Exception {
        var invoke = bReturnAbcInvoke();
        assertTaskReport(invoke, "invoke", trace(), EXECUTION);
      }

      @Test
      void report_combine_as_combine_task() throws Exception {
        var combine = bCombine(bInt(17));
        assertTaskReport(combine, "combine", trace(), EXECUTION);
      }

      @Test
      void report_order_as_order_task() throws Exception {
        var order = bOrder(bInt(17));
        assertTaskReport(order, "order", trace(), EXECUTION);
      }

      @Test
      void report_pick_as_pick_task() throws Exception {
        var pick = bPick(bArray(bInt(17)), bInt(0));
        assertTaskReport(pick, "pick", trace(), EXECUTION);
      }

      @Test
      void report_select_as_select_task() throws Exception {
        var select = bSelect(bTuple(bInt(17)), bInt(0));
        assertTaskReport(select, "select", trace(), EXECUTION);
      }
    }

    @Nested
    class _with_traces {
      @Test
      void order_inside_lambda_body() throws Exception {
        var order = bOrder(bInt(17));
        var lambda = bLambda(order);
        var lambdaAsExpr = bCall(bLambda(lambda));
        var call = bCall(lambdaAsExpr);
        var callLocation = location(alias().append("path"), 3);
        var bExprAttributes = new BExprAttributes(
            map(lambda.hash(), "lambda.hash()"), map(call.hash(), callLocation));
        assertTaskReport(
            call, bExprAttributes, "order", trace("lambda.hash()", callLocation), EXECUTION);
      }

      @Test
      void order_inside_lambda_body_that_is_called_from_other_lambda_body() throws Exception {
        var order = bOrder(bInt(17));
        var lambda2 = bLambda(order);
        var call2 = bCall(lambda2);
        var lambda1 = bLambda(call2);
        var call1 = bCall(lambda1);
        var call1Location = location(alias().append("path"), 1);
        var call2Location = location(alias().append("path"), 2);

        var bExprAttributes = new BExprAttributes(
            map(lambda1.hash(), "lambda1", lambda2.hash(), "lambda2"),
            map(call1.hash(), call1Location, call2.hash(), call2Location));

        assertTaskReport(
            call1,
            bExprAttributes,
            "order",
            trace("lambda2", call2Location, "lambda1", call1Location),
            EXECUTION);
      }
    }

    private void assertTaskReport(BExpr expr, String operationName, Trace trace, Origin origin) {
      evaluate(bEvaluate(), expr);
      var taskReport = report(VM_EVALUATE.append(":" + operationName), trace, origin, list());
      assertThat(reporter().reports()).contains(taskReport);
    }

    private void assertTaskReport(
        BExpr expr,
        BExprAttributes bExprAttributes,
        String operationName,
        Trace trace,
        Origin origin) {
      evaluate(bEvaluate(), expr, bExprAttributes);
      var taskReport = report(VM_EVALUATE.append(":" + operationName), trace, origin, list());
      assertThat(reporter().reports()).contains(taskReport);
    }
  }

  @Nested
  class _parallelism {
    @Test
    void tasks_are_executed_in_parallel() throws Exception {
      var testName = detectEnclosingMethodName();
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
    void execution_waits_and_reuses_computation_with_equal_hash_that_is_being_executed()
        throws Exception {
      var testName = detectEnclosingMethodName();
      var counterName = testName + "1";
      COUNTERS.put(counterName, new AtomicInteger());
      var bExpr = bOrder(
          invokeExecuteCommands(testName, "INC1"),
          invokeExecuteCommands(testName, "INC1"),
          invokeExecuteCommands(testName, "INC1"),
          invokeExecuteCommands(testName, "INC1"));
      var reporter = reporter();
      var scheduler = scheduler();
      var bEvaluate = bEvaluate(scheduler);
      assertThat(evaluate(bEvaluate, bExpr).get().get())
          .isEqualTo(bArray(bString("1"), bString("1"), bString("1"), bString("1")));

      verifyConstTasksOrigin(4, DISK, reporter);
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

  private BValue evaluate(BExpr expr) {
    return evaluate(bEvaluate(), expr).get().get();
  }

  private Promise<Maybe<BValue>> evaluate(BEvaluate bEvaluate, BExpr expr) {
    return evaluate(bEvaluate, expr, new BExprAttributes());
  }

  private Promise<Maybe<BValue>> evaluate(
      BEvaluate bEvaluate, BExpr expr, BExprAttributes bExprAttributes) {
    var result = scheduler().submit(bEvaluate, argument(tuple(expr, bExprAttributes)));
    await().until(() -> result.toMaybe().isSome());
    return result;
  }

  public static BInt returnIntParam(NativeApi nativeApi, BTuple args) throws Exception {
    return (BInt) args.get(0);
  }

  private static NativeMethodLoader nativeMethodLoaderThatAlwaysLoadsMemoizeString()
      throws Exception {
    var nativeMethodLoader = mock(NativeMethodLoader.class);
    when(nativeMethodLoader.load(any()))
        .thenReturn(
            ok(BEvaluateTest.class.getMethod("memoizeString", NativeApi.class, BTuple.class)));
    return nativeMethodLoader;
  }

  private static final java.util.List<String> MEMOIZED = synchronizedList(new ArrayList<>());

  public static BValue memoizeString(NativeApi nativeApi, BTuple args) throws Exception {
    MEMOIZED.add(((BString) args.get(0)).toJavaString());
    return nativeApi.factory().string("result");
  }

  private static void verifyConstTasksOrigin(
      int size, Origin expectedSource, TestReporter reporter) {
    var sources = reporter
        .reports()
        .filter(r -> r.label().equals(VM_EVALUATE.append(":invoke")))
        .map(Report::origin);
    assertThat(sources).containsExactlyElementsIn(resSourceList(size, expectedSource));
  }

  private static ArrayList<Origin> resSourceList(int size, Origin expectedSource) {
    var expected = new ArrayList<>(nCopies(size, expectedSource));
    expected.set(0, EXECUTION);
    return expected;
  }

  private CountingBEvaluate countingBEvaluate() {
    return new CountingBEvaluate(scheduler(), stepEvaluator(), bytecodeF(), bReferenceInliner());
  }

  private static class CountingBEvaluate extends BEvaluate {
    private final ConcurrentHashMap<BExpr, AtomicInteger> counters = new ConcurrentHashMap<>();

    public CountingBEvaluate(
        Scheduler scheduler,
        StepEvaluator stepEvaluator,
        BytecodeFactory bytecodeFactory,
        BReferenceInliner bReferenceInliner) {
      super(scheduler, stepEvaluator, bytecodeFactory, bReferenceInliner);
    }

    @Override
    protected Job newJob(BExpr expr, List<Job> environment, Trace trace) {
      counters.computeIfAbsent(expr, k -> new AtomicInteger()).incrementAndGet();
      return super.newJob(expr, environment, trace);
    }

    public ConcurrentHashMap<BExpr, AtomicInteger> counters() {
      return counters;
    }
  }

  private static void assertReportsContains(
      List<Report> reports, Level level, String messagePrefix) {
    var contains = reports.stream().anyMatch(r -> r.logs()
        .anyMatches(l -> l.level() == level && l.message().startsWith(messagePrefix)));
    assertWithMessage(reports.toString()).that(contains).isTrue();
  }

  private static String detectEnclosingMethodName() {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    if (stackTrace.length > 2) {
      return stackTrace[2].getMethodName();
    } else {
      throw new RuntimeException("Cannot detect enclosing method name.");
    }
  }
}
