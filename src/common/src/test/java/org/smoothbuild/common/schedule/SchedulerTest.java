package org.smoothbuild.common.schedule;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.Thread.currentThread;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.Origin.MEMORY;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.common.schedule.Scheduler.LABEL;
import static org.smoothbuild.common.schedule.Tasks.argument;
import static org.smoothbuild.common.schedule.Tasks.task1;
import static org.smoothbuild.common.testing.AwaitHelper.await;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import jakarta.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.awaitility.core.ConditionFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.MutablePromise;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.log.report.SystemOutReporter;
import org.smoothbuild.common.testing.ReportTestWiring;

public class SchedulerTest {
  @Nested
  class _task0 {
    @Nested
    class _normal_task {
      @Test
      void successful_task_execution_sets_result_in_promise() {
        Task0<Integer> task = () -> output(7, newReport());
        assertExecutionStoresResultInPromise(scheduler -> scheduler.submit(task), 7);
      }

      @ParameterizedTest
      @MethodSource("org.smoothbuild.common.schedule.SchedulerTest#executionReports")
      void successful_task_execution_submits_report(Report report) {
        Task0<Integer> task = () -> output(7, report);
        assertExecutionSubmitsReport(scheduler -> scheduler.submit(task), report);
      }

      @Test
      void successful_task_execution_can_return_null() {
        Task0<Object> task = () -> output(null, newReport());
        assertExecutionStoresResultInPromise(scheduler -> scheduler.submit(task), null);
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var atomicInteger = new AtomicInteger(0);
        MutablePromise<Maybe<String>> predecessor = promise();

        var scheduler = newScheduler();
        var result = scheduler.submit(list(predecessor), new GetAtomicInteger(atomicInteger));
        Thread.sleep(1000);
        atomicInteger.set(1);
        predecessor.accept(some(""));
        await().until(() -> result.toMaybe().isSome());

        assertThat(result.get()).isEqualTo(some(1));
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() {
        var exception = new RuntimeException();
        Task0<Integer> task = () -> {
          throw exception;
        };

        var report = reportAboutExceptionThrownByTask(exception);
        assertExecutionSubmitsReport(scheduler -> scheduler.submit(task), report);
      }
    }

    @Nested
    class _injected_task {
      @Test
      void successful_task_execution_sets_result_in_promise() {
        var task = Key.get(ReturnAbc.class);
        assertExecutionStoresResultInPromise(scheduler -> scheduler.submit(task), "abc");
      }

      @Test
      void successful_task_execution_submits_report() {
        var task = Key.get(ReturnAbc.class);
        assertExecutionSubmitsReport(scheduler -> scheduler.submit(task), newReport());
      }

      private static class ReturnAbc implements Task0<String> {
        @Override
        public Output<String> execute() {
          return output("abc", newReport());
        }
      }

      @Test
      void successful_task_execution_can_return_null() {
        var task = Key.get(ReturnNull.class);
        assertExecutionStoresResultInPromise(scheduler -> scheduler.submit(task), null);
      }

      private static class ReturnNull implements Task0<String> {
        @Override
        public Output<String> execute() {
          return output(null, newReport());
        }
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var atomicInteger = new AtomicInteger(0);
        MutablePromise<Maybe<String>> predecessor = promise();

        var scheduler = newScheduler(atomicInteger);
        var result = scheduler.submit(list(predecessor), GetAtomicInteger.class);
        Thread.sleep(1000);
        atomicInteger.set(1);
        predecessor.accept(some(""));
        await().until(() -> result.toMaybe().isSome());

        assertThat(result.get()).isEqualTo(some(1));
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() {
        var exception = new RuntimeException();
        var injector = Guice.createInjector(new AbstractModule() {
          @Override
          protected void configure() {
            bind(RuntimeException.class).toInstance(exception);
          }
        });

        var task = Key.get(ThrowException.class);
        var report = reportAboutExceptionThrownByTask(exception);
        assertExecutionSubmitsReport(injector, scheduler -> scheduler.submit(task), report);
      }

      private static class ThrowException implements Task0<String> {
        private final RuntimeException exception;

        @Inject
        private ThrowException(RuntimeException exception) {
          this.exception = exception;
        }

        @Override
        public Output<String> execute() {
          throw exception;
        }
      }
    }

    private static class GetAtomicInteger implements Task0<Integer> {
      private final AtomicInteger atomicInteger;

      @Inject
      public GetAtomicInteger(AtomicInteger atomicInteger) {
        this.atomicInteger = atomicInteger;
      }

      @Override
      public Output<Integer> execute() {
        return output(atomicInteger.get(), newReport());
      }
    }

    private static class SetAtomicBoolean implements Task0<Integer> {
      private final AtomicBoolean atomicBoolean;

      @Inject
      public SetAtomicBoolean(AtomicBoolean atomicBoolean) {
        this.atomicBoolean = atomicBoolean;
      }

      @Override
      public Output<Integer> execute() {
        atomicBoolean.set(true);
        return output(3, newReport());
      }
    }
  }

  @Nested
  class _task1 {
    @Nested
    class _normal_task {
      @Test
      void successful_task_execution_sets_result_in_promise() {
        Task1<Integer, String> task = (i) -> output(i.toString(), newReport());
        var arg1 = argument(7);
        assertExecutionStoresResultInPromise(scheduler -> scheduler.submit(task, arg1), "7");
      }

      @ParameterizedTest
      @MethodSource("org.smoothbuild.common.schedule.SchedulerTest#executionReports")
      void successful_task_execution_submits_report(Report report) {
        Task1<Integer, String> task = (i) -> output(i.toString(), report);
        var arg1 = argument(7);
        assertExecutionSubmitsReport(scheduler -> scheduler.submit(task, arg1), report);
      }

      @Test
      void successful_task_execution_can_return_null() {
        Task1<Integer, String> task = (i) -> output(null, newReport());
        var arg1 = argument(7);
        assertExecutionStoresResultInPromise(scheduler -> scheduler.submit(task, arg1), null);
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var atomicInteger = new AtomicInteger(0);
        MutablePromise<Maybe<String>> predecessor = promise();
        var arg1 = argument(7);

        var scheduler = newScheduler(atomicInteger);
        var task = new GetAtomicInteger(atomicInteger);
        var result = scheduler.submit(list(predecessor), task, arg1);
        Thread.sleep(1000);
        atomicInteger.set(1);
        predecessor.accept(some(""));
        await().until(() -> result.toMaybe().isSome());

        assertThat(result.get()).isEqualTo(some(1));
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() {
        var exception = new RuntimeException();
        Task1<Integer, Integer> task = (a1) -> {
          throw exception;
        };
        var arg1 = argument(1);

        var report = reportAboutExceptionThrownByTask(exception);
        assertExecutionSubmitsReport(scheduler -> scheduler.submit(task, arg1), report);
      }

      @Test
      void task_is_not_executed_when_argument_task_failed_with_error() {
        var executed = new AtomicBoolean(false);
        Promise<Maybe<Integer>> arg1 = promise(none());
        var predecessor = argument(7);

        var scheduler = newScheduler();
        var result = scheduler.submit(list(predecessor), new SetAtomicBoolean(executed), arg1);
        await().until(() -> result.toMaybe().isSome());

        assertThat(executed.get()).isFalse();
        assertThat(result.get()).isEqualTo(none());
      }
    }

    @Nested
    class _injected_task {
      @Test
      void successful_task_execution_sets_result_in_promise() {
        var arg1 = argument("");
        var task = Key.get(ReturnAbc.class);
        assertExecutionStoresResultInPromise(scheduler -> scheduler.submit(task, arg1), "abc");
      }

      @Test
      void successful_task_execution_submits_report() {
        var arg1 = argument("");
        var task = Key.get(ReturnAbc.class);
        var report = newReport();
        assertExecutionSubmitsReport(scheduler -> scheduler.submit(task, arg1), report);
      }

      private static class ReturnAbc implements Task1<String, String> {
        @Override
        public Output<String> execute(String arg1) {
          return output("abc", newReport());
        }
      }

      @Test
      void successful_task_execution_can_return_null() {
        var arg1 = argument("");
        var task = Key.get(ReturnNull.class);
        assertExecutionStoresResultInPromise(scheduler -> scheduler.submit(task, arg1), null);
      }

      private static class ReturnNull implements Task1<String, String> {
        @Override
        public Output<String> execute(String arg1) {
          return output(null, newReport());
        }
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var atomicInteger = new AtomicInteger(0);
        MutablePromise<Maybe<String>> predecessor = promise();
        var arg1 = argument(7);

        var scheduler = newScheduler(atomicInteger);
        var result = scheduler.submit(list(predecessor), GetAtomicInteger.class, arg1);
        Thread.sleep(1000);
        atomicInteger.set(1);
        predecessor.accept(some(""));
        await().until(() -> result.toMaybe().isSome());

        assertThat(result.get()).isEqualTo(some(1));
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() {
        var arg1 = argument("");
        var exception = new RuntimeException();
        var injector = Guice.createInjector(new AbstractModule() {
          @Override
          protected void configure() {
            bind(RuntimeException.class).toInstance(exception);
          }
        });

        var task = Key.get(ThrowException.class);
        var report = reportAboutExceptionThrownByTask(exception);
        assertExecutionSubmitsReport(injector, scheduler -> scheduler.submit(task, arg1), report);
      }

      private static class ThrowException implements Task1<String, String> {
        private final RuntimeException exception;

        @Inject
        private ThrowException(RuntimeException exception) {
          this.exception = exception;
        }

        @Override
        public Output<String> execute(String arg1) {
          throw exception;
        }
      }

      @Test
      void task_is_not_executed_when_argument_task_failed_with_error() {
        var atomicBoolean = new AtomicBoolean(false);
        Promise<Maybe<Integer>> arg1 = promise(none());
        var predecessor = argument(7);

        var scheduler = newScheduler(atomicBoolean);
        var result = scheduler.submit(list(predecessor), SetAtomicBoolean.class, arg1);
        await().until(() -> result.toMaybe().isSome());

        assertThat(atomicBoolean.get()).isFalse();
        assertThat(result.get()).isEqualTo(none());
      }
    }

    private static class SetAtomicBoolean implements Task1<Integer, Integer> {
      private final AtomicBoolean atomicBoolean;

      @Inject
      public SetAtomicBoolean(AtomicBoolean atomicBoolean) {
        this.atomicBoolean = atomicBoolean;
      }

      @Override
      public Output<Integer> execute(Integer integer) {
        atomicBoolean.set(true);
        return output(3, newReport());
      }
    }

    private static class GetAtomicInteger implements Task1<Integer, Integer> {
      private final AtomicInteger atomicInteger;

      @Inject
      public GetAtomicInteger(AtomicInteger atomicInteger) {
        this.atomicInteger = atomicInteger;
      }

      @Override
      public Output<Integer> execute(Integer arg1) {
        return output(atomicInteger.get(), newReport());
      }
    }
  }

  @Nested
  class _task2 {
    @Nested
    class _normal_task {
      @Test
      void successful_task_execution_sets_result_in_promise() {
        Task2<Integer, Integer, Integer> task = (a1, a2) -> output(a1 + a2, newReport());

        var arg1 = argument(7);
        var arg2 = argument(5);
        assertExecutionStoresResultInPromise(scheduler -> scheduler.submit(task, arg1, arg2), 12);
      }

      @ParameterizedTest
      @MethodSource("org.smoothbuild.common.schedule.SchedulerTest#executionReports")
      void successful_task_execution_submits_report(Report report) {
        Task2<Integer, Integer, Integer> task = (a1, a2) -> output(a1 + a2, report);
        var arg1 = argument(7);
        var arg2 = argument(7);
        assertExecutionSubmitsReport(scheduler -> scheduler.submit(task, arg1, arg2), report);
      }

      @Test
      void successful_task_execution_can_return_null() {
        Task2<Integer, Integer, Object> task = (a1, a2) -> output(null, newReport());

        var arg1 = argument(7);
        var arg2 = argument(5);
        assertExecutionStoresResultInPromise(scheduler -> scheduler.submit(task, arg1, arg2), null);
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var atomicInteger = new AtomicInteger(0);
        MutablePromise<Maybe<String>> predecessor = promise();
        var arg1 = argument(7);
        var arg2 = argument(8);

        var scheduler = newScheduler(atomicInteger);
        var task = new GetAtomicInteger(atomicInteger);
        var result = scheduler.submit(list(predecessor), task, arg1, arg2);
        Thread.sleep(1000);
        atomicInteger.set(1);
        predecessor.accept(some(""));
        await().until(() -> result.toMaybe().isSome());

        assertThat(result.get()).isEqualTo(some(1));
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() {
        var exception = new RuntimeException();
        Task2<Integer, Integer, Integer> task = (a1, a2) -> {
          throw exception;
        };
        var arg1 = argument(1);
        var arg2 = argument(2);

        var report = reportAboutExceptionThrownByTask(exception);
        assertExecutionSubmitsReport(scheduler -> scheduler.submit(task, arg1, arg2), report);
      }

      @Test
      void task_is_not_executed_when_argument_task_failed_with_error() {
        var executed = new AtomicBoolean(false);
        var predecessor = argument(6);
        Promise<Maybe<Integer>> arg1 = promise(none());
        var arg2 = argument(7);

        var scheduler = newScheduler();
        var task = new SetAtomicBoolean(executed);
        var result = scheduler.submit(list(predecessor), task, arg1, arg2);
        ConditionFactory result1;
        result1 = await();
        result1.until(() -> result.toMaybe().isSome());

        assertThat(executed.get()).isFalse();
        assertThat(result.get()).isEqualTo(none());
      }
    }

    @Nested
    class _injected_task {
      @Test
      void successful_task_execution_sets_result_in_promise() {
        var arg1 = argument("");
        var arg2 = argument("");
        var task = Key.get(ReturnAbc.class);
        assertExecutionStoresResultInPromise(
            scheduler -> scheduler.submit(task, arg1, arg2), "abc");
      }

      @Test
      void successful_task_execution_submits_report() {
        var arg1 = argument("");
        var arg2 = argument("");
        var task = Key.get(ReturnAbc.class);
        assertExecutionSubmitsReport(scheduler -> scheduler.submit(task, arg1, arg2), newReport());
      }

      private static class ReturnAbc implements Task2<String, String, String> {
        @Override
        public Output<String> execute(String arg1, String arg2) {
          return output("abc", newReport());
        }
      }

      @Test
      void successful_task_execution_can_return_null() {
        var arg1 = argument("");
        var arg2 = argument("");
        var task = Key.get(ReturnNull.class);
        assertExecutionStoresResultInPromise(scheduler -> scheduler.submit(task, arg1, arg2), null);
      }

      private static class ReturnNull implements Task2<String, String, String> {
        @Override
        public Output<String> execute(String arg1, String arg2) {
          return output(null, newReport());
        }
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var atomicInteger = new AtomicInteger(0);
        MutablePromise<Maybe<String>> predecessor = promise();
        var arg1 = argument(7);
        var arg2 = argument(8);

        var scheduler = newScheduler(atomicInteger);
        var result = scheduler.submit(list(predecessor), GetAtomicInteger.class, arg1, arg2);
        Thread.sleep(1000);
        atomicInteger.set(1);
        predecessor.accept(some(""));
        await().until(() -> result.toMaybe().isSome());

        assertThat(result.get()).isEqualTo(some(1));
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() {
        var arg1 = argument("");
        var arg2 = argument("");
        var exception = new RuntimeException();
        var injector = Guice.createInjector(new AbstractModule() {
          @Override
          protected void configure() {
            bind(RuntimeException.class).toInstance(exception);
          }
        });

        var task = Key.get(ThrowException.class);
        var report = reportAboutExceptionThrownByTask(exception);
        assertExecutionSubmitsReport(injector, s -> s.submit(task, arg1, arg2), report);
      }

      private static class ThrowException implements Task2<String, String, String> {
        private final RuntimeException exception;

        @Inject
        private ThrowException(RuntimeException exception) {
          this.exception = exception;
        }

        @Override
        public Output<String> execute(String arg1, String arg2) {
          throw exception;
        }
      }

      @Test
      void task_is_not_executed_when_argument_task_failed_with_error() {
        var executed = new AtomicBoolean(false);
        var predecessor = argument(7);
        Promise<Maybe<Integer>> arg1 = promise(none());
        var arg2 = argument(8);

        var scheduler = newScheduler(executed);
        var result = scheduler.submit(list(predecessor), SetAtomicBoolean.class, arg1, arg2);
        await().until(() -> result.toMaybe().isSome());

        assertThat(executed.get()).isFalse();
        assertThat(result.get()).isEqualTo(none());
      }
    }

    private static class SetAtomicBoolean implements Task2<Integer, Integer, Integer> {
      private final AtomicBoolean atomicBoolean;

      @Inject
      public SetAtomicBoolean(AtomicBoolean atomicBoolean) {
        this.atomicBoolean = atomicBoolean;
      }

      @Override
      public Output<Integer> execute(Integer arg1, Integer arg2) {
        atomicBoolean.set(true);
        return output(3, newReport());
      }
    }

    private static class GetAtomicInteger implements Task2<Integer, Integer, Integer> {
      private final AtomicInteger atomicInteger;

      @Inject
      public GetAtomicInteger(AtomicInteger atomicInteger) {
        this.atomicInteger = atomicInteger;
      }

      @Override
      public Output<Integer> execute(Integer arg1, Integer arg2) {
        return output(atomicInteger.get(), newReport());
      }
    }
  }

  @Nested
  class _taskX {
    @Nested
    class _normal_task {
      @Test
      void successful_task_execution_sets_result_in_promise() {
        TaskX<Integer, String> task = (i) -> output(i.toString(), newReport());
        var args = list(argument(7));
        assertExecutionStoresResultInPromise(scheduler -> scheduler.submit(task, args), "[7]");
      }

      @ParameterizedTest
      @MethodSource("org.smoothbuild.common.schedule.SchedulerTest#executionReports")
      void successful_task_execution_submits_report(Report report) {
        TaskX<Integer, String> task = (i) -> output(i.toString(), report);
        var args = list(argument(7));
        assertExecutionSubmitsReport(scheduler -> scheduler.submit(task, args), report);
      }

      @Test
      void successful_task_execution_can_return_null() {
        TaskX<Integer, String> task = (i) -> output(null, newReport());
        var args = list(argument(7));
        assertExecutionStoresResultInPromise(scheduler -> scheduler.submit(task, args), null);
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var atomicInteger = new AtomicInteger(0);
        MutablePromise<Maybe<String>> predecessor = promise();
        var args = list(argument(7));

        var scheduler = newScheduler();
        var task = new GetAtomicInteger(atomicInteger);
        var result = scheduler.submit(list(predecessor), task, args);
        Thread.sleep(1000);
        atomicInteger.set(1);
        predecessor.accept(some(""));
        await().until(() -> result.toMaybe().isSome());

        assertThat(result.get()).isEqualTo(some(1));
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() {
        var exception = new RuntimeException();
        TaskX<Integer, Integer> task = (a1) -> {
          throw exception;
        };
        var args = list(argument(1));

        var report = reportAboutExceptionThrownByTask(exception);
        assertExecutionSubmitsReport(scheduler -> scheduler.submit(task, args), report);
      }

      @Test
      void task_is_not_executed_when_argument_task_failed_with_error() {
        var executed = new AtomicBoolean(false);
        Promise<Maybe<Integer>> predecessor = argument(6);
        List<Promise<? extends Maybe<? extends Integer>>> args = list(argument(7), promise(none()));

        var scheduler = newScheduler();
        var result = scheduler.submit(list(predecessor), new SetAtomicBoolean(executed), args);
        await().until(() -> result.toMaybe().isSome());

        assertThat(executed.get()).isFalse();
        assertThat(result.get()).isEqualTo(none());
      }
    }

    @Nested
    class _injected_task {
      @Test
      void successful_task_execution_sets_result_in_promise() {
        var list = list(argument(""));
        var task = Key.get(ReturnAbc.class);
        assertExecutionStoresResultInPromise(scheduler -> scheduler.submit(task, list), "abc");
      }

      @Test
      void successful_task_execution_submits_report() {
        var args = list(argument(""));
        var task = Key.get(ReturnAbc.class);
        var report = newReport();
        assertExecutionSubmitsReport(scheduler -> scheduler.submit(task, args), report);
      }

      private static class ReturnAbc implements TaskX<String, String> {
        @Override
        public Output<String> execute(List<String> args) {
          return output("abc", newReport());
        }
      }

      @Test
      void successful_task_execution_can_return_null() {
        var args = list(argument(""));
        var task = Key.get(ReturnNull.class);
        assertExecutionStoresResultInPromise(scheduler -> scheduler.submit(task, args), null);
      }

      private static class ReturnNull implements TaskX<String, String> {
        @Override
        public Output<String> execute(List<String> args) {
          return output(null, newReport());
        }
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var atomicInteger = new AtomicInteger(0);
        MutablePromise<Maybe<String>> predecessor = promise();
        var args = list(argument(7));

        var scheduler = newScheduler(atomicInteger);
        var result = scheduler.submit(list(predecessor), GetAtomicInteger.class, args);
        Thread.sleep(1000);
        atomicInteger.set(1);
        predecessor.accept(some(""));
        await().until(() -> result.toMaybe().isSome());

        assertThat(result.get()).isEqualTo(some(1));
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() {
        var args = list(argument(""));
        var exception = new RuntimeException();
        var injector = Guice.createInjector(new AbstractModule() {
          @Override
          protected void configure() {
            bind(RuntimeException.class).toInstance(exception);
          }
        });

        var task = Key.get(ThrowException.class);
        var report = reportAboutExceptionThrownByTask(exception);
        assertExecutionSubmitsReport(injector, scheduler -> scheduler.submit(task, args), report);
      }

      private static class ThrowException implements TaskX<String, String> {
        private final RuntimeException exception;

        @Inject
        private ThrowException(RuntimeException exception) {
          this.exception = exception;
        }

        @Override
        public Output<String> execute(List<String> args) {
          throw exception;
        }
      }

      @Test
      void task_is_not_executed_when_argument_task_failed_with_error() {
        var executed = new AtomicBoolean(false);
        Promise<Maybe<Integer>> predecessor = argument(6);
        List<Promise<? extends Maybe<Integer>>> args = list(argument(7), promise(none()));

        var scheduler = newScheduler(executed);
        var result = scheduler.submit(list(predecessor), SetAtomicBoolean.class, args);
        await().until(() -> result.toMaybe().isSome());

        assertThat(executed.get()).isFalse();
        assertThat(result.get()).isEqualTo(none());
      }
    }

    private static class SetAtomicBoolean implements TaskX<Integer, Integer> {
      private final AtomicBoolean atomicBoolean;

      @Inject
      public SetAtomicBoolean(AtomicBoolean atomicBoolean) {
        this.atomicBoolean = atomicBoolean;
      }

      @Override
      public Output<Integer> execute(List<Integer> arg1) {
        atomicBoolean.set(true);
        return output(3, newReport());
      }
    }

    private static class GetAtomicInteger implements TaskX<Integer, Integer> {
      private final AtomicInteger atomicInteger;

      @Inject
      public GetAtomicInteger(AtomicInteger atomicInteger) {
        this.atomicInteger = atomicInteger;
      }

      @Override
      public Output<Integer> execute(List<Integer> args) {
        return output(atomicInteger.get(), newReport());
      }
    }
  }

  @Nested
  class _join {
    @Test
    void result_is_not_available_until_all_promises_from_list_are_not_available() {
      var scheduler = newScheduler();
      MutablePromise<Maybe<String>> arg1 = promise();

      var result = scheduler.join(list(arg1));

      assertThat(result.toMaybe().isNone()).isTrue();
    }

    @Test
    void result_contains_arguments_joined_into_list() {
      var scheduler = newScheduler();
      var arg1 = promise(some("abc"));
      var arg2 = promise(some("def"));

      var result = scheduler.join(list(arg1, arg2));
      await().until(() -> result.toMaybe().isSome());

      assertThat(result.get()).isEqualTo(some(list("abc", "def")));
    }

    @Test
    void result_is_none_when_one_of_arguments_is_none() {
      var scheduler = newScheduler();
      var arg1 = promise(some("abc"));
      var arg2 = promise(none());

      var result = scheduler.join(list(arg1, arg2));
      await().until(() -> result.toMaybe().isSome());

      assertThat(result.get()).isEqualTo(none());
    }
  }

  @Nested
  class _parallel_task {
    @Test
    void parallel_task_executes_encapsulated_task_for_each_list_element() {
      var scheduler = newScheduler();
      var task = scheduler.newParallelTask(task1(label("p"), s -> s + "!"));
      var result = scheduler.submit(task, argument(list("a", "b", "c")));
      await().until(() -> result.toMaybe().isSome());

      assertThat(result.get()).isEqualTo(some(list("a!", "b!", "c!")));
    }
  }

  @Nested
  class _thread_separation {
    @Test
    void task0_is_executed_in_thread_different_from_one_that_submitted_task() {
      var thread = new AtomicReference<Thread>();
      var scheduler = newScheduler();
      var result = scheduler.submit(() -> {
        thread.set(currentThread());
        return output(7, newReport());
      });
      await().until(() -> result.toMaybe().isSome());

      assertThat(thread.get()).isNotSameInstanceAs(currentThread());
    }

    @Test
    void task1_is_executed_in_thread_different_from_one_that_executed_arg_task() {
      var argThread = new AtomicReference<Thread>();
      var thread = new AtomicReference<Thread>();
      var scheduler = newScheduler();

      var arg = scheduler.submit(() -> {
        argThread.set(currentThread());
        return output(7, newReport());
      });
      var result = scheduler.submit(
          (i) -> {
            thread.set(currentThread());
            return output(i + 1, newReport());
          },
          arg);
      await().until(() -> result.toMaybe().isSome());

      assertThat(argThread.get()).isNotSameInstanceAs(thread.get());
    }

    @Test
    void task2_is_executed_in_thread_different_from_those_that_executed_arg_tasks() {
      var arg1Thread = new AtomicReference<Thread>();
      var arg2Thread = new AtomicReference<Thread>();
      var thread = new AtomicReference<Thread>();
      var scheduler = newScheduler();

      var arg1 = scheduler.submit(() -> {
        arg1Thread.set(currentThread());
        return output(7, newReport());
      });
      var arg2 = scheduler.submit(() -> {
        arg1Thread.set(currentThread());
        return output(3, newReport());
      });
      var result = scheduler.submit(
          (Integer a1, Integer a2) -> {
            thread.set(currentThread());
            return output(a1 + a2, newReport());
          },
          arg1,
          arg2);
      await().until(() -> result.toMaybe().isSome());

      assertThat(result.get()).isEqualTo(some(10));
      assertThat(arg1Thread.get()).isNotSameInstanceAs(thread.get());
      assertThat(arg2Thread.get()).isNotSameInstanceAs(thread.get());
    }
  }

  private static <R> void assertExecutionStoresResultInPromise(
      Function<Scheduler, Promise<Maybe<R>>> scheduleFunction, R expectedValue) {
    var scheduler = newScheduler();

    var result = scheduleFunction.apply(scheduler);
    await().until(() -> result.toMaybe().isSome());

    assertThat(result.get()).isEqualTo(some(expectedValue));
  }

  private static <R> void assertExecutionSubmitsReport(
      Function<Scheduler, Promise<R>> scheduleFunction, Report report) {
    assertExecutionSubmitsReport(Guice.createInjector(), scheduleFunction, report);
  }

  private static <R> void assertExecutionSubmitsReport(
      Injector injector, Function<Scheduler, Promise<R>> scheduleFunction, Report report) {
    var reporter = mock(Reporter.class);
    var scheduler = new Scheduler(injector, reporter);

    var result = scheduleFunction.apply(scheduler);
    await().until(() -> result.toMaybe().isSome());

    verify(reporter).submit(report);
  }

  private static Scheduler newScheduler() {
    return new Scheduler(Guice.createInjector(new ReportTestWiring()), new SystemOutReporter());
  }

  private static Scheduler newScheduler(AtomicInteger atomicInteger) {
    var injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(AtomicInteger.class).toInstance(atomicInteger);
      }
    });
    return new Scheduler(injector, new SystemOutReporter());
  }

  private static Scheduler newScheduler(AtomicBoolean atomicBoolean) {
    var injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(AtomicBoolean.class).toInstance(atomicBoolean);
      }
    });
    return new Scheduler(injector, new SystemOutReporter());
  }

  public static List<Arguments> executionReports() {
    return list(arguments(
        newReport(),
        report(label("myLabel"), MEMORY, list(info("message"))),
        report(label("myLabel"), list(info("message"))),
        newReportWithError()));
  }

  private static Report reportAboutExceptionThrownByTask(RuntimeException exception) {
    var fatal = fatal("Task execution failed with exception:", exception);
    return report(LABEL, list(fatal));
  }

  private static Report newReport() {
    return report(label("myLabel"), list());
  }

  private static Report newReportWithError() {
    return report(label("myLabel"), list(error("message")));
  }
}
