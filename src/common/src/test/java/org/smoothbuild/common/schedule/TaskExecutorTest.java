package org.smoothbuild.common.schedule;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.base.ResultSource.MEMORY;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.common.schedule.TaskExecutor.EXECUTE_LABEL;
import static org.smoothbuild.common.testing.TestingThread.sleepMillis;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import jakarta.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.concurrent.PromisedValue;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.log.report.Trace;

public class TaskExecutorTest {
  @Nested
  class _task0 {
    @Nested
    class _constant_task {
      @Test
      void successful_task_execution_sets_result_in_promise() throws Exception {
        var constant = "abc";
        var label = label("my-label");
        var task = new ConstantTask<>(constant, label);

        assertExecutionStoresResultInPromise(taskExecutor -> taskExecutor.submit(task), constant);
      }

      @Test
      void successful_task_execution_submits_report() throws Exception {
        var constant = "abc";
        var label = label("my-label");
        var task = new ConstantTask<>(constant, label);

        var report = report(label, new Trace(), EXECUTION, list());
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task), report);
      }

      @Test
      void successful_task_execution_can_return_null() throws Exception {
        var label = label("my-label");
        var task = new ConstantTask<>(null, label);

        assertExecutionStoresResultInPromise(taskExecutor -> taskExecutor.submit(task), null);
      }
    }

    @Nested
    class _normal_task {
      @Test
      void successful_task_execution_sets_result_in_promise() throws Exception {
        Task0<Integer> task = () -> output(7, newReport());
        assertExecutionStoresResultInPromise(taskExecutor -> taskExecutor.submit(task), 7);
      }

      @ParameterizedTest
      @MethodSource("org.smoothbuild.common.schedule.TaskExecutorTest#executionReports")
      void successful_task_execution_submits_report(Report report) throws Exception {
        Task0<Integer> task = () -> output(7, report);
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task), report);
      }

      @Test
      void successful_task_execution_can_return_null() throws Exception {
        Task0<Object> task = () -> output(null, newReport());
        assertExecutionStoresResultInPromise(taskExecutor -> taskExecutor.submit(task), null);
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var predecessor = new PromisedValue<String>();
        Task0<String> task = () -> output("abc", newReport());

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(task, list(predecessor));
        predecessor.accept("");
        taskExecutor.waitUntilIdle();

        assertThat(result.get()).isEqualTo("abc");
      }

      @Test
      void task_is_not_executed_when_predecessor_fails_with_error() throws Exception {
        var predecessor = new PromisedValue<String>();
        Task0<String> task = () -> output("abc", newReport());

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(task, list(predecessor));
        taskExecutor.waitUntilIdle();

        assertThat(result.toMaybe()).isEqualTo(none());
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() throws Exception {
        var exception = new RuntimeException();
        Task0<Integer> task = () -> {
          throw exception;
        };

        var report = reportAboutExceptionThrownByTask(exception);
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task), report);
      }
    }

    @Nested
    class _injected_task {
      @Test
      void successful_task_execution_sets_result_in_promise() throws Exception {
        var task = Key.get(ReturnAbc.class);
        assertExecutionStoresResultInPromise(taskExecutor -> taskExecutor.submit(task), "abc");
      }

      @Test
      void successful_task_execution_submits_report() throws Exception {
        var task = Key.get(ReturnAbc.class);
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task), newReport());
      }

      private static class ReturnAbc implements Task0<String> {
        @Override
        public Output<String> execute() {
          return output("abc", newReport());
        }
      }

      @Test
      void successful_task_execution_can_return_null() throws Exception {
        var task = Key.get(ReturnNull.class);
        assertExecutionStoresResultInPromise(taskExecutor -> taskExecutor.submit(task), null);
      }

      private static class ReturnNull implements Task0<String> {
        @Override
        public Output<String> execute() {
          return output(null, newReport());
        }
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var predecessor = new PromisedValue<String>();

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), Key.get(ReturnAbc.class));
        predecessor.accept("");
        taskExecutor.waitUntilIdle();

        assertThat(result.get()).isEqualTo("abc");
      }

      @Test
      void task_is_not_executed_when_predecessor_fails_with_error() throws Exception {
        var predecessor = new PromisedValue<String>();

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), Key.get(ReturnAbc.class));
        taskExecutor.waitUntilIdle();

        assertThat(result.toMaybe()).isEqualTo(none());
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() throws Exception {
        var exception = new RuntimeException();
        var injector = Guice.createInjector(new AbstractModule() {
          @Override
          protected void configure() {
            bind(RuntimeException.class).toInstance(exception);
          }
        });

        var task = Key.get(ThrowException.class);
        var report = reportAboutExceptionThrownByTask(exception);
        assertExecutionSubmitsReport(injector, taskExecutor -> taskExecutor.submit(task), report);
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
  }

  @Nested
  class _task1 {
    @Nested
    class _normal_task {
      @Test
      void successful_task_execution_sets_result_in_promise() throws Exception {
        Task1<String, Integer> task = (i) -> output(i.toString(), newReport());
        var arg1 = new PromisedValue<>(7);
        assertExecutionStoresResultInPromise(taskExecutor -> taskExecutor.submit(task, arg1), "7");
      }

      @ParameterizedTest
      @MethodSource("org.smoothbuild.common.schedule.TaskExecutorTest#executionReports")
      void successful_task_execution_submits_report(Report report) throws Exception {
        Task1<String, Integer> task = (i) -> output(i.toString(), report);
        var arg1 = new PromisedValue<>(7);
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task, arg1), report);
      }

      @Test
      void successful_task_execution_can_return_null() throws Exception {
        Task1<String, Integer> task = (i) -> output(null, newReport());
        var arg1 = new PromisedValue<>(7);
        assertExecutionStoresResultInPromise(taskExecutor -> taskExecutor.submit(task, arg1), null);
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var predecessor = new PromisedValue<String>();
        var arg1 = new PromisedValue<String>();
        Task1<String, String> task = (a1) -> output("abc", newReport());

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), task, arg1);
        arg1.accept("");
        predecessor.accept("");
        taskExecutor.waitUntilIdle();

        assertThat(result.get()).isEqualTo("abc");
      }

      @Test
      void task_is_not_executed_when_predecessor_fails_with_error() throws Exception {
        var predecessor = new PromisedValue<String>();
        var arg1 = new PromisedValue<String>();
        Task1<String, String> task = (a1) -> output("abc", newReport());

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), task, arg1);
        arg1.accept("");
        taskExecutor.waitUntilIdle();

        assertThat(result.toMaybe()).isEqualTo(none());
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() throws Exception {
        var exception = new RuntimeException();
        Task1<Integer, Integer> task = (a1) -> {
          throw exception;
        };
        Promise<Integer> arg1 = new PromisedValue<>(1);

        var report = reportAboutExceptionThrownByTask(exception);
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task, arg1), report);
      }

      @Test
      void task_is_not_executed_when_argument_task_failed_with_error() throws Exception {
        Task0<Integer> argTask = () -> output(7, newReportWithError());
        var executed = new AtomicBoolean(false);
        Task1<String, Integer> task = (i) -> {
          executed.set(true);
          return output("", newReport());
        };

        var taskExecutor = newTaskExecutor();
        var argResult = taskExecutor.submit(argTask);
        taskExecutor.submit(task, argResult);
        taskExecutor.waitUntilIdle();

        assertThat(executed.get()).isFalse();
      }
    }

    @Nested
    class _injected_task {
      @Test
      void successful_task_execution_sets_result_in_promise() throws Exception {
        var arg1 = new PromisedValue<>("");
        var task = Key.get(ReturnAbc.class);
        assertExecutionStoresResultInPromise(
            taskExecutor -> taskExecutor.submit(task, arg1), "abc");
      }

      @Test
      void successful_task_execution_submits_report() throws Exception {
        var arg1 = new PromisedValue<>("");
        var task = Key.get(ReturnAbc.class);
        var report = newReport();
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task, arg1), report);
      }

      private static class ReturnAbc implements Task1<String, String> {
        @Override
        public Output<String> execute(String arg1) {
          return output("abc", newReport());
        }
      }

      @Test
      void successful_task_execution_can_return_null() throws Exception {
        var arg1 = new PromisedValue<>("");
        var task = Key.get(ReturnNull.class);
        assertExecutionStoresResultInPromise(taskExecutor -> taskExecutor.submit(task, arg1), null);
      }

      private static class ReturnNull implements Task1<String, String> {
        @Override
        public Output<String> execute(String arg1) {
          return output(null, newReport());
        }
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var predecessor = new PromisedValue<String>();
        var arg1 = new PromisedValue<String>();

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), Key.get(ReturnAbc.class), arg1);
        arg1.accept("");
        predecessor.accept("");
        taskExecutor.waitUntilIdle();

        assertThat(result.get()).isEqualTo("abc");
      }

      @Test
      void task_is_not_executed_when_predecessor_fails_with_error() throws Exception {
        var predecessor = new PromisedValue<String>();
        var arg1 = new PromisedValue<String>();

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), Key.get(ReturnAbc.class), arg1);
        arg1.accept("");
        taskExecutor.waitUntilIdle();

        assertThat(result.toMaybe()).isEqualTo(none());
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() throws Exception {
        var arg1 = new PromisedValue<>("");
        var exception = new RuntimeException();
        var injector = Guice.createInjector(new AbstractModule() {
          @Override
          protected void configure() {
            bind(RuntimeException.class).toInstance(exception);
          }
        });

        var task = Key.get(ThrowException.class);
        var report = reportAboutExceptionThrownByTask(exception);
        assertExecutionSubmitsReport(
            injector, taskExecutor -> taskExecutor.submit(task, arg1), report);
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
      void task_is_not_executed_when_argument_task_failed_with_error() throws Exception {
        Task0<String> argTask = () -> output("", newReportWithError());
        var executed = new AtomicBoolean(false);
        var taskKey = Key.get(new TypeLiteral<Task1<String, String>>() {});
        var injector = Guice.createInjector(new AbstractModule() {
          @Override
          protected void configure() {
            bind(taskKey).toInstance((i) -> {
              executed.set(true);
              return output("", newReport());
            });
          }
        });

        var taskExecutor = newTaskExecutor(injector);
        var argResult = taskExecutor.submit(argTask);
        taskExecutor.submit(taskKey, argResult);
        taskExecutor.waitUntilIdle();

        assertThat(executed.get()).isFalse();
      }
    }
  }

  @Nested
  class _task2 {
    @Nested
    class _normal_task {
      @Test
      void successful_task_execution_sets_result_in_promise() throws Exception {
        Task2<Integer, Integer, Integer> task = (a1, a2) -> output(a1 + a2, newReport());

        var arg1 = new PromisedValue<>(7);
        var arg2 = new PromisedValue<>(5);
        assertExecutionStoresResultInPromise(
            taskExecutor -> taskExecutor.submit(task, arg1, arg2), 12);
      }

      @ParameterizedTest
      @MethodSource("org.smoothbuild.common.schedule.TaskExecutorTest#executionReports")
      void successful_task_execution_submits_report(Report report) throws Exception {
        Task2<Integer, Integer, Integer> task = (a1, a2) -> output(a1 + a2, report);
        var arg1 = new PromisedValue<>(7);
        var arg2 = new PromisedValue<>(7);
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task, arg1, arg2), report);
      }

      @Test
      void successful_task_execution_can_return_null() throws Exception {
        Task2<Object, Integer, Integer> task = (a1, a2) -> output(null, newReport());

        var arg1 = new PromisedValue<>(7);
        var arg2 = new PromisedValue<>(5);
        assertExecutionStoresResultInPromise(
            taskExecutor -> taskExecutor.submit(task, arg1, arg2), null);
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var predecessor = new PromisedValue<String>();
        var arg1 = new PromisedValue<String>();
        var arg2 = new PromisedValue<String>();
        Task2<String, String, String> task = (a1, a2) -> output("abc", newReport());

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), task, arg1, arg2);
        arg1.accept("");
        arg2.accept("");
        predecessor.accept("");
        taskExecutor.waitUntilIdle();

        assertThat(result.get()).isEqualTo("abc");
      }

      @Test
      void task_is_not_executed_when_predecessor_fails_with_error() throws Exception {
        var predecessor = new PromisedValue<String>();
        var arg1 = new PromisedValue<String>();
        var arg2 = new PromisedValue<String>();
        Task2<String, String, String> task = (a1, a2) -> output("abc", newReport());

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), task, arg1, arg2);
        arg1.accept("");
        arg2.accept("");
        taskExecutor.waitUntilIdle();

        assertThat(result.toMaybe()).isEqualTo(none());
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() throws Exception {
        var exception = new RuntimeException();
        Task2<Integer, Integer, Integer> task = (a1, a2) -> {
          throw exception;
        };
        Promise<Integer> arg1 = new PromisedValue<>(1);
        Promise<Integer> arg2 = new PromisedValue<>(2);

        var report = reportAboutExceptionThrownByTask(exception);
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task, arg1, arg2), report);
      }

      @Test
      void task_is_not_executed_when_argument_task_failed_with_error() throws Exception {
        var executed = new AtomicBoolean(false);
        Task2<Integer, Integer, Integer> task2 = (a1, a2) -> {
          executed.set(true);
          return output(0, newReport());
        };

        var taskExecutor = newTaskExecutor();
        var arg1Result = taskExecutor.submit(() -> output(7, newReportWithError()));
        var arg2Result = taskExecutor.submit(() -> output(7, newReport()));
        taskExecutor.submit(task2, arg1Result, arg2Result);
        taskExecutor.waitUntilIdle();

        assertThat(executed.get()).isFalse();
      }
    }

    @Nested
    class _injected_task {
      @Test
      void successful_task_execution_sets_result_in_promise() throws Exception {
        var arg1 = new PromisedValue<>("");
        var arg2 = new PromisedValue<>("");
        var task = Key.get(ReturnAbc.class);
        assertExecutionStoresResultInPromise(
            taskExecutor -> taskExecutor.submit(task, arg1, arg2), "abc");
      }

      @Test
      void successful_task_execution_submits_report() throws Exception {
        var arg1 = new PromisedValue<>("");
        var arg2 = new PromisedValue<>("");
        var task = Key.get(ReturnAbc.class);
        assertExecutionSubmitsReport(
            taskExecutor -> taskExecutor.submit(task, arg1, arg2), newReport());
      }

      private static class ReturnAbc implements Task2<String, String, String> {
        @Override
        public Output<String> execute(String arg1, String arg2) {
          return output("abc", newReport());
        }
      }

      @Test
      void successful_task_execution_can_return_null() throws Exception {
        var arg1 = new PromisedValue<>("");
        var arg2 = new PromisedValue<>("");
        var task = Key.get(ReturnNull.class);
        assertExecutionStoresResultInPromise(
            taskExecutor -> taskExecutor.submit(task, arg1, arg2), null);
      }

      private static class ReturnNull implements Task2<String, String, String> {
        @Override
        public Output<String> execute(String arg1, String arg2) {
          return output(null, newReport());
        }
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var predecessor = new PromisedValue<String>();
        var arg1 = new PromisedValue<String>();
        var arg2 = new PromisedValue<String>();

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), Key.get(ReturnAbc.class), arg1, arg2);
        arg1.accept("");
        arg2.accept("");
        predecessor.accept("");
        taskExecutor.waitUntilIdle();

        assertThat(result.get()).isEqualTo("abc");
      }

      @Test
      void task_is_not_executed_when_predecessor_fails_with_error() throws Exception {
        var predecessor = new PromisedValue<String>();
        var arg1 = new PromisedValue<String>();
        var arg2 = new PromisedValue<String>();

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), Key.get(ReturnAbc.class), arg1, arg2);
        arg1.accept("");
        arg2.accept("");
        taskExecutor.waitUntilIdle();

        assertThat(result.toMaybe()).isEqualTo(none());
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() throws Exception {
        var arg1 = new PromisedValue<>("");
        var arg2 = new PromisedValue<>("");
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
      void task_is_not_executed_when_argument_task_failed_with_error() throws Exception {
        Task0<String> arg1Task = () -> output("", newReportWithError());
        Task0<String> arg2Task = () -> output("", newReport());
        var executed = new AtomicBoolean(false);
        var taskKey = Key.get(new TypeLiteral<Task2<String, String, String>>() {});
        var injector = Guice.createInjector(new AbstractModule() {
          @Override
          protected void configure() {
            bind(taskKey).toInstance((a1, a2) -> {
              executed.set(true);
              return output("", newReport());
            });
          }
        });

        var taskExecutor = newTaskExecutor(injector);
        var argResult1 = taskExecutor.submit(arg1Task);
        var argResult2 = taskExecutor.submit(arg2Task);
        taskExecutor.submit(taskKey, argResult1, argResult2);
        taskExecutor.waitUntilIdle();

        assertThat(executed.get()).isFalse();
      }
    }
  }

  @Nested
  class _taskX {
    @Nested
    class _normal_task {
      @Test
      void successful_task_execution_sets_result_in_promise() throws Exception {
        TaskX<String, Integer> task = (i) -> output(i.toString(), newReport());
        var args = list(new PromisedValue<>(7));
        assertExecutionStoresResultInPromise(
            taskExecutor -> taskExecutor.submit(task, args), "[7]");
      }

      @ParameterizedTest
      @MethodSource("org.smoothbuild.common.schedule.TaskExecutorTest#executionReports")
      void successful_task_execution_submits_report(Report report) throws Exception {
        TaskX<String, Integer> task = (i) -> output(i.toString(), report);
        var args = list(new PromisedValue<>(7));
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task, args), report);
      }

      @Test
      void successful_task_execution_can_return_null() throws Exception {
        TaskX<String, Integer> task = (i) -> output(null, newReport());
        var args = list(new PromisedValue<>(7));
        assertExecutionStoresResultInPromise(taskExecutor -> taskExecutor.submit(task, args), null);
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var predecessor = new PromisedValue<String>();
        var args = list(new PromisedValue<String>());
        TaskX<String, String> task = (a1) -> output("abc", newReport());

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), task, args);
        args.get(0).accept("");
        predecessor.accept("");
        taskExecutor.waitUntilIdle();

        assertThat(result.get()).isEqualTo("abc");
      }

      @Test
      void task_is_not_executed_when_predecessor_fails_with_error() throws Exception {
        var predecessor = new PromisedValue<String>();
        var args = list(new PromisedValue<String>());
        TaskX<String, String> task = (a1) -> output("abc", newReport());

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), task, args);
        args.get(0).accept("");
        taskExecutor.waitUntilIdle();

        assertThat(result.toMaybe()).isEqualTo(none());
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() throws Exception {
        var exception = new RuntimeException();
        TaskX<Integer, Integer> task = (a1) -> {
          throw exception;
        };
        var args = list(new PromisedValue<>(1));

        var report = reportAboutExceptionThrownByTask(exception);
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task, args), report);
      }

      @Test
      void task_is_not_executed_when_argument_task_failed_with_error() throws Exception {
        Task0<Integer> argTask = () -> output(7, newReportWithError());
        var executed = new AtomicBoolean(false);
        TaskX<String, Integer> task = (i) -> {
          executed.set(true);
          return output("", newReport());
        };

        var taskExecutor = newTaskExecutor();
        var argResult = taskExecutor.submit(argTask);
        taskExecutor.submit(task, list(argResult));
        taskExecutor.waitUntilIdle();

        assertThat(executed.get()).isFalse();
      }
    }

    @Nested
    class _injected_task {
      @Test
      void successful_task_execution_sets_result_in_promise() throws Exception {
        var list = list(new PromisedValue<>(""));
        var task = Key.get(ReturnAbc.class);
        assertExecutionStoresResultInPromise(
            taskExecutor -> taskExecutor.submit(task, list), "abc");
      }

      @Test
      void successful_task_execution_submits_report() throws Exception {
        var args = list(new PromisedValue<>(""));
        var task = Key.get(ReturnAbc.class);
        var report = newReport();
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task, args), report);
      }

      private static class ReturnAbc implements TaskX<String, String> {
        @Override
        public Output<String> execute(List<String> args) {
          return output("abc", newReport());
        }
      }

      @Test
      void successful_task_execution_can_return_null() throws Exception {
        var args = list(new PromisedValue<>(""));
        var task = Key.get(ReturnNull.class);
        assertExecutionStoresResultInPromise(taskExecutor -> taskExecutor.submit(task, args), null);
      }

      private static class ReturnNull implements TaskX<String, String> {
        @Override
        public Output<String> execute(List<String> args) {
          return output(null, newReport());
        }
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var predecessor = new PromisedValue<String>();
        var args = list(new PromisedValue<String>());

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), Key.get(ReturnAbc.class), args);
        args.get(0).accept("");
        predecessor.accept("");
        taskExecutor.waitUntilIdle();

        assertThat(result.get()).isEqualTo("abc");
      }

      @Test
      void task_is_not_executed_when_predecessor_fails_with_error() throws Exception {
        var predecessor = new PromisedValue<String>();
        var args = list(new PromisedValue<String>());

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), Key.get(ReturnAbc.class), args);
        args.get(0).accept("");
        taskExecutor.waitUntilIdle();

        assertThat(result.toMaybe()).isEqualTo(none());
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() throws Exception {
        var args = list(new PromisedValue<>(""));
        var exception = new RuntimeException();
        var injector = Guice.createInjector(new AbstractModule() {
          @Override
          protected void configure() {
            bind(RuntimeException.class).toInstance(exception);
          }
        });

        var task = Key.get(ThrowException.class);
        var report = reportAboutExceptionThrownByTask(exception);
        assertExecutionSubmitsReport(
            injector, taskExecutor -> taskExecutor.submit(task, args), report);
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
      void task_is_not_executed_when_argument_task_failed_with_error() throws Exception {
        Task0<String> argTask = () -> output("", newReportWithError());
        var executed = new AtomicBoolean(false);
        var taskKey = Key.get(new TypeLiteral<TaskX<String, String>>() {});
        var injector = Guice.createInjector(new AbstractModule() {
          @Override
          protected void configure() {
            bind(taskKey).toInstance((i) -> {
              executed.set(true);
              return output("", newReport());
            });
          }
        });

        var taskExecutor = newTaskExecutor(injector);
        var argResult = taskExecutor.submit(argTask);
        taskExecutor.submit(taskKey, list(argResult));
        taskExecutor.waitUntilIdle();

        assertThat(executed.get()).isFalse();
      }
    }
  }

  @Nested
  class _thread_separation {
    @Test
    void task0_is_executed_in_thread_different_from_one_that_submitted_task() throws Exception {
      var thread = new AtomicReference<Thread>();
      var taskExecutor = newTaskExecutor();
      taskExecutor.submit(() -> {
        thread.set(Thread.currentThread());
        return output(7, newReport());
      });
      taskExecutor.waitUntilIdle();

      assertThat(thread.get()).isNotSameInstanceAs(Thread.currentThread());
    }

    @Test
    void task1_is_executed_in_thread_different_from_one_that_executed_arg_task() throws Exception {
      var argThread = new AtomicReference<Thread>();
      var thread = new AtomicReference<Thread>();
      var taskExecutor = newTaskExecutor();

      var resultPromise = taskExecutor.submit(() -> {
        argThread.set(Thread.currentThread());
        return output(7, newReport());
      });
      taskExecutor.submit(
          (i) -> {
            thread.set(Thread.currentThread());
            return output(i + 1, newReport());
          },
          resultPromise);
      taskExecutor.waitUntilIdle();

      assertThat(argThread.get()).isNotSameInstanceAs(thread.get());
    }

    @Test
    void task2_is_executed_in_thread_different_from_those_that_executed_arg_tasks()
        throws Exception {
      var arg1Thread = new AtomicReference<Thread>();
      var arg2Thread = new AtomicReference<Thread>();
      var thread = new AtomicReference<Thread>();
      var taskExecutor = newTaskExecutor();

      var arg1Result = taskExecutor.submit(() -> {
        arg1Thread.set(Thread.currentThread());
        return output(7, newReport());
      });
      var arg2Result = taskExecutor.submit(() -> {
        arg1Thread.set(Thread.currentThread());
        return output(3, newReport());
      });
      var result = taskExecutor.submit(
          (Integer a1, Integer a2) -> {
            thread.set(Thread.currentThread());
            return output(a1 + a2, newReport());
          },
          arg1Result,
          arg2Result);
      taskExecutor.waitUntilIdle();

      assertThat(result.get()).isEqualTo(10);
      assertThat(arg1Thread.get()).isNotSameInstanceAs(thread.get());
      assertThat(arg2Thread.get()).isNotSameInstanceAs(thread.get());
    }
  }

  @Nested
  class _completion {
    @Test
    void wait_until_idle_waits_for_all_task_to_complete() throws Exception {
      var completed = new AtomicBoolean(false);
      var taskExecutor = newTaskExecutor();

      taskExecutor.submit(cloningTask(100, completed, taskExecutor));
      taskExecutor.waitUntilIdle();

      assertThat(completed.get()).isTrue();
    }

    private static Task0<String> cloningTask(
        int count, AtomicBoolean completed, TaskExecutor taskExecutor) {
      return () -> {
        sleepMillis(1);
        if (0 < count) {
          taskExecutor.submit(cloningTask(count - 1, completed, taskExecutor));
        } else {
          completed.set(true);
        }
        return output("", newReport());
      };
    }
  }

  private static <R> void assertExecutionStoresResultInPromise(
      Function<TaskExecutor, Promise<R>> scheduleFunction, R expectedValue)
      throws InterruptedException {
    var taskExecutor = newTaskExecutor();

    var result = scheduleFunction.apply(taskExecutor);
    taskExecutor.waitUntilIdle();

    assertThat(result.get()).isEqualTo(expectedValue);
  }

  private static <R> void assertExecutionSubmitsReport(
      Function<TaskExecutor, Promise<R>> scheduleFunction, Report report)
      throws InterruptedException {
    assertExecutionSubmitsReport(Guice.createInjector(), scheduleFunction, report);
  }

  private static <R> void assertExecutionSubmitsReport(
      Injector injector, Function<TaskExecutor, Promise<R>> scheduleFunction, Report report)
      throws InterruptedException {
    var reporter = mock(Reporter.class);
    var taskExecutor = new TaskExecutor(injector, reporter, 4);

    scheduleFunction.apply(taskExecutor);
    taskExecutor.waitUntilIdle();

    verify(reporter).submit(report);
  }

  private static TaskExecutor newTaskExecutor() {
    return newTaskExecutor(Guice.createInjector());
  }

  private static TaskExecutor newTaskExecutor(Injector injector) {
    return new TaskExecutor(injector, mock(Reporter.class), 4);
  }

  public static List<Arguments> executionReports() {
    return list(arguments(
        newReport(),
        report(label("my-label"), new Trace(), MEMORY, list(info("message"))),
        report(label("my-label"), new Trace(), EXECUTION, list(info("message"))),
        newReportWithError()));
  }

  private static Report reportAboutExceptionThrownByTask(RuntimeException exception) {
    var fatal = fatal("Task execution failed with exception:", exception);
    return report(EXECUTE_LABEL, new Trace(), EXECUTION, list(fatal));
  }

  private static Report newReport() {
    return report(label("my-label"), new Trace(), EXECUTION, list());
  }

  private static Report newReportWithError() {
    return report(label("my-label"), new Trace(), EXECUTION, list(error("message")));
  }
}
