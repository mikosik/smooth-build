package org.smoothbuild.common.task;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.Thread.currentThread;
import static org.awaitility.Awaitility.await;
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
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.base.ResultSource.MEMORY;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Argument.argument;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.common.task.TaskExecutor.EXECUTOR_LABEL;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import jakarta.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
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
import org.smoothbuild.common.log.report.Trace;

public class TaskExecutorTest {
  @Nested
  class _task0 {
    @Nested
    class _normal_task {
      @Test
      void successful_task_execution_sets_result_in_promise() {
        Task0<Integer> task = () -> output(7, newReport());
        assertExecutionStoresResultInPromise(taskExecutor -> taskExecutor.submit(task), 7);
      }

      @ParameterizedTest
      @MethodSource("org.smoothbuild.common.task.TaskExecutorTest#executionReports")
      void successful_task_execution_submits_report(Report report) {
        Task0<Integer> task = () -> output(7, report);
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task), report);
      }

      @Test
      void successful_task_execution_can_return_null() {
        Task0<Object> task = () -> output(null, newReport());
        assertExecutionStoresResultInPromise(taskExecutor -> taskExecutor.submit(task), null);
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var atomicInteger = new AtomicInteger(0);
        MutablePromise<Maybe<String>> predecessor = promise();

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), new GetAtomicInteger(atomicInteger));
        Thread.sleep(1000);
        atomicInteger.set(1);
        predecessor.accept(some(""));
        await().until(() -> result.toMaybe().isSome());

        assertThat(result.get()).isEqualTo(some(1));
      }

      @Test
      void task_is_not_executed_when_predecessor_fails_with_error() {
        var predecessor = promise(none());
        var executed = new AtomicBoolean(false);

        var taskExecutor = newTaskExecutor(executed);
        var result = taskExecutor.submit(list(predecessor), new SetAtomicBoolean(executed));
        await().until(() -> result.toMaybe().isSome());

        assertThat(result.get()).isEqualTo(none());
        assertThat(executed.get()).isFalse();
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() {
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
      void successful_task_execution_sets_result_in_promise() {
        var task = Key.get(ReturnAbc.class);
        assertExecutionStoresResultInPromise(taskExecutor -> taskExecutor.submit(task), "abc");
      }

      @Test
      void successful_task_execution_submits_report() {
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
      void successful_task_execution_can_return_null() {
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
        var atomicInteger = new AtomicInteger(0);
        MutablePromise<Maybe<String>> predecessor = promise();

        var taskExecutor = newTaskExecutor(atomicInteger);
        var result = taskExecutor.submit(list(predecessor), GetAtomicInteger.class);
        Thread.sleep(1000);
        atomicInteger.set(1);
        predecessor.accept(some(""));
        await().until(() -> result.toMaybe().isSome());

        assertThat(result.get()).isEqualTo(some(1));
      }

      @Test
      void task_is_not_executed_when_predecessor_fails_with_error() {
        var predecessor = promise(none());
        var executed = new AtomicBoolean(false);
        var taskExecutor = newTaskExecutor(executed);
        var result = taskExecutor.submit(list(predecessor), SetAtomicBoolean.class);
        await().until(() -> result.toMaybe().isSome());

        assertThat(executed.get()).isFalse();
        assertThat(result.get()).isEqualTo(none());
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
        Task1<String, Integer> task = (i) -> output(i.toString(), newReport());
        var arg1 = argument(7);
        assertExecutionStoresResultInPromise(taskExecutor -> taskExecutor.submit(task, arg1), "7");
      }

      @ParameterizedTest
      @MethodSource("org.smoothbuild.common.task.TaskExecutorTest#executionReports")
      void successful_task_execution_submits_report(Report report) {
        Task1<String, Integer> task = (i) -> output(i.toString(), report);
        var arg1 = argument(7);
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task, arg1), report);
      }

      @Test
      void successful_task_execution_can_return_null() {
        Task1<String, Integer> task = (i) -> output(null, newReport());
        var arg1 = argument(7);
        assertExecutionStoresResultInPromise(taskExecutor -> taskExecutor.submit(task, arg1), null);
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var atomicInteger = new AtomicInteger(0);
        MutablePromise<Maybe<String>> predecessor = promise();
        var arg1 = argument(7);

        var taskExecutor = newTaskExecutor(atomicInteger);
        var task = new GetAtomicInteger(atomicInteger);
        var result = taskExecutor.submit(list(predecessor), task, arg1);
        Thread.sleep(1000);
        atomicInteger.set(1);
        predecessor.accept(some(""));
        await().until(() -> result.toMaybe().isSome());

        assertThat(result.get()).isEqualTo(some(1));
      }

      @Test
      void task_is_not_executed_when_predecessor_fails_with_error() {
        var executed = new AtomicBoolean(false);
        var predecessor = promise(none());
        var arg1 = argument(7);

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), new SetAtomicBoolean(executed), arg1);
        await().until(() -> result.toMaybe().isSome());

        assertThat(executed.get()).isFalse();
        assertThat(result.get()).isEqualTo(none());
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() {
        var exception = new RuntimeException();
        Task1<Integer, Integer> task = (a1) -> {
          throw exception;
        };
        var arg1 = argument(1);

        var report = reportAboutExceptionThrownByTask(exception);
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task, arg1), report);
      }

      @Test
      void task_is_not_executed_when_argument_task_failed_with_error() {
        var executed = new AtomicBoolean(false);
        Promise<Maybe<Integer>> arg1 = promise(none());
        var predecessor = argument(7);

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), new SetAtomicBoolean(executed), arg1);
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
        assertExecutionStoresResultInPromise(
            taskExecutor -> taskExecutor.submit(task, arg1), "abc");
      }

      @Test
      void successful_task_execution_submits_report() {
        var arg1 = argument("");
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
      void successful_task_execution_can_return_null() {
        var arg1 = argument("");
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
        var atomicInteger = new AtomicInteger(0);
        MutablePromise<Maybe<String>> predecessor = promise();
        var arg1 = argument(7);

        var taskExecutor = newTaskExecutor(atomicInteger);
        var result = taskExecutor.submit(list(predecessor), GetAtomicInteger.class, arg1);
        Thread.sleep(1000);
        atomicInteger.set(1);
        predecessor.accept(some(""));
        await().until(() -> result.toMaybe().isSome());

        assertThat(result.get()).isEqualTo(some(1));
      }

      @Test
      void task_is_not_executed_when_predecessor_fails_with_error() {
        var atomicBoolean = new AtomicBoolean(false);
        Promise<Maybe<Integer>> predecessor = promise(none());
        var arg1 = argument(7);

        var taskExecutor = newTaskExecutor(atomicBoolean);
        var result = taskExecutor.submit(list(predecessor), SetAtomicBoolean.class, arg1);
        await().until(() -> result.toMaybe().isSome());

        assertThat(atomicBoolean.get()).isFalse();
        assertThat(result.get()).isEqualTo(none());
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
      void task_is_not_executed_when_argument_task_failed_with_error() {
        var atomicBoolean = new AtomicBoolean(false);
        Promise<Maybe<Integer>> arg1 = promise(none());
        var predecessor = argument(7);

        var taskExecutor = newTaskExecutor(atomicBoolean);
        var result = taskExecutor.submit(list(predecessor), SetAtomicBoolean.class, arg1);
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
        assertExecutionStoresResultInPromise(
            taskExecutor -> taskExecutor.submit(task, arg1, arg2), 12);
      }

      @ParameterizedTest
      @MethodSource("org.smoothbuild.common.task.TaskExecutorTest#executionReports")
      void successful_task_execution_submits_report(Report report) {
        Task2<Integer, Integer, Integer> task = (a1, a2) -> output(a1 + a2, report);
        var arg1 = argument(7);
        var arg2 = argument(7);
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task, arg1, arg2), report);
      }

      @Test
      void successful_task_execution_can_return_null() {
        Task2<Object, Integer, Integer> task = (a1, a2) -> output(null, newReport());

        var arg1 = argument(7);
        var arg2 = argument(5);
        assertExecutionStoresResultInPromise(
            taskExecutor -> taskExecutor.submit(task, arg1, arg2), null);
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var atomicInteger = new AtomicInteger(0);
        MutablePromise<Maybe<String>> predecessor = promise();
        var arg1 = argument(7);
        var arg2 = argument(8);

        var taskExecutor = newTaskExecutor(atomicInteger);
        var task = new GetAtomicInteger(atomicInteger);
        var result = taskExecutor.submit(list(predecessor), task, arg1, arg2);
        Thread.sleep(1000);
        atomicInteger.set(1);
        predecessor.accept(some(""));
        await().until(() -> result.toMaybe().isSome());

        assertThat(result.get()).isEqualTo(some(1));
      }

      @Test
      void task_is_not_executed_when_predecessor_fails_with_error() {
        var executed = new AtomicBoolean(false);
        Promise<Maybe<Integer>> predecessor = promise(none());
        var arg1 = argument(6);
        var arg2 = argument(7);

        var taskExecutor = newTaskExecutor();
        var task = new SetAtomicBoolean(executed);
        var result = taskExecutor.submit(list(predecessor), task, arg1, arg2);
        await().until(() -> result.toMaybe().isSome());

        assertThat(executed.get()).isFalse();
        assertThat(result.get()).isEqualTo(none());
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
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task, arg1, arg2), report);
      }

      @Test
      void task_is_not_executed_when_argument_task_failed_with_error() {
        var executed = new AtomicBoolean(false);
        var predecessor = argument(6);
        Promise<Maybe<Integer>> arg1 = promise(none());
        var arg2 = argument(7);

        var taskExecutor = newTaskExecutor();
        var task = new SetAtomicBoolean(executed);
        var result = taskExecutor.submit(list(predecessor), task, arg1, arg2);
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
        var arg2 = argument("");
        var task = Key.get(ReturnAbc.class);
        assertExecutionStoresResultInPromise(
            taskExecutor -> taskExecutor.submit(task, arg1, arg2), "abc");
      }

      @Test
      void successful_task_execution_submits_report() {
        var arg1 = argument("");
        var arg2 = argument("");
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
      void successful_task_execution_can_return_null() {
        var arg1 = argument("");
        var arg2 = argument("");
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
        var atomicInteger = new AtomicInteger(0);
        MutablePromise<Maybe<String>> predecessor = promise();
        var arg1 = argument(7);
        var arg2 = argument(8);

        var taskExecutor = newTaskExecutor(atomicInteger);
        var result = taskExecutor.submit(list(predecessor), GetAtomicInteger.class, arg1, arg2);
        Thread.sleep(1000);
        atomicInteger.set(1);
        predecessor.accept(some(""));
        await().until(() -> result.toMaybe().isSome());

        assertThat(result.get()).isEqualTo(some(1));
      }

      @Test
      void task_is_not_executed_when_predecessor_fails_with_error() {
        var executed = new AtomicBoolean(false);
        Promise<Maybe<Integer>> predecessor = promise(none());
        var arg1 = argument(7);
        var arg2 = argument(8);

        var taskExecutor = newTaskExecutor(executed);
        var result = taskExecutor.submit(list(predecessor), SetAtomicBoolean.class, arg1, arg2);
        await().until(() -> result.toMaybe().isSome());

        assertThat(executed.get()).isFalse();
        assertThat(result.get()).isEqualTo(none());
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

        var taskExecutor = newTaskExecutor(executed);
        var result = taskExecutor.submit(list(predecessor), SetAtomicBoolean.class, arg1, arg2);
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
        TaskX<String, Integer> task = (i) -> output(i.toString(), newReport());
        var args = list(argument(7));
        assertExecutionStoresResultInPromise(
            taskExecutor -> taskExecutor.submit(task, args), "[7]");
      }

      @ParameterizedTest
      @MethodSource("org.smoothbuild.common.task.TaskExecutorTest#executionReports")
      void successful_task_execution_submits_report(Report report) {
        TaskX<String, Integer> task = (i) -> output(i.toString(), report);
        var args = list(argument(7));
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task, args), report);
      }

      @Test
      void successful_task_execution_can_return_null() {
        TaskX<String, Integer> task = (i) -> output(null, newReport());
        var args = list(argument(7));
        assertExecutionStoresResultInPromise(taskExecutor -> taskExecutor.submit(task, args), null);
      }

      @Test
      void task_is_executed_after_its_predecessors() throws Exception {
        var atomicInteger = new AtomicInteger(0);
        MutablePromise<Maybe<String>> predecessor = promise();
        var args = list(argument(7));

        var taskExecutor = newTaskExecutor();
        var task = new GetAtomicInteger(atomicInteger);
        var result = taskExecutor.submit(list(predecessor), task, args);
        Thread.sleep(1000);
        atomicInteger.set(1);
        predecessor.accept(some(""));
        await().until(() -> result.toMaybe().isSome());

        assertThat(result.get()).isEqualTo(some(1));
      }

      @Test
      void task_is_not_executed_when_predecessor_fails_with_error() {
        var executed = new AtomicBoolean(false);
        Promise<Maybe<Integer>> predecessor = promise(none());
        var args = list(argument(7));

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), new SetAtomicBoolean(executed), args);
        await().until(() -> result.toMaybe().isSome());

        assertThat(executed.get()).isFalse();
        assertThat(result.get()).isEqualTo(none());
      }

      @Test
      void execution_of_task_that_thrown_exception_submits_fatal_report() {
        var exception = new RuntimeException();
        TaskX<Integer, Integer> task = (a1) -> {
          throw exception;
        };
        var args = list(argument(1));

        var report = reportAboutExceptionThrownByTask(exception);
        assertExecutionSubmitsReport(taskExecutor -> taskExecutor.submit(task, args), report);
      }

      @Test
      void task_is_not_executed_when_argument_task_failed_with_error() {
        var executed = new AtomicBoolean(false);
        Promise<Maybe<Integer>> predecessor = argument(6);
        List<Promise<? extends Maybe<? extends Integer>>> args = list(argument(7), promise(none()));

        var taskExecutor = newTaskExecutor();
        var result = taskExecutor.submit(list(predecessor), new SetAtomicBoolean(executed), args);
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
        assertExecutionStoresResultInPromise(
            taskExecutor -> taskExecutor.submit(task, list), "abc");
      }

      @Test
      void successful_task_execution_submits_report() {
        var args = list(argument(""));
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
      void successful_task_execution_can_return_null() {
        var args = list(argument(""));
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
        var atomicInteger = new AtomicInteger(0);
        MutablePromise<Maybe<String>> predecessor = promise();
        var args = list(argument(7));

        var taskExecutor = newTaskExecutor(atomicInteger);
        var result = taskExecutor.submit(list(predecessor), GetAtomicInteger.class, args);
        Thread.sleep(1000);
        atomicInteger.set(1);
        predecessor.accept(some(""));
        await().until(() -> result.toMaybe().isSome());

        assertThat(result.get()).isEqualTo(some(1));
      }

      @Test
      void task_is_not_executed_when_predecessor_fails_with_error() {
        var executed = new AtomicBoolean(false);
        Promise<Maybe<Integer>> predecessor = promise(none());
        List<Promise<? extends Maybe<Integer>>> args = list(argument(7));

        var taskExecutor = newTaskExecutor(executed);
        var result = taskExecutor.submit(list(predecessor), SetAtomicBoolean.class, args);
        await().until(() -> result.toMaybe().isSome());

        assertThat(executed.get()).isFalse();
        assertThat(result.get()).isEqualTo(none());
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
      void task_is_not_executed_when_argument_task_failed_with_error() {
        var executed = new AtomicBoolean(false);
        Promise<Maybe<Integer>> predecessor = argument(6);
        List<Promise<? extends Maybe<Integer>>> args = list(argument(7), promise(none()));

        var taskExecutor = newTaskExecutor(executed);
        var result = taskExecutor.submit(list(predecessor), SetAtomicBoolean.class, args);
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
  class _thread_separation {
    @Test
    void task0_is_executed_in_thread_different_from_one_that_submitted_task() {
      var thread = new AtomicReference<Thread>();
      var taskExecutor = newTaskExecutor();
      var result = taskExecutor.submit(() -> {
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
      var taskExecutor = newTaskExecutor();

      var arg = taskExecutor.submit(() -> {
        argThread.set(currentThread());
        return output(7, newReport());
      });
      var result = taskExecutor.submit(
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
      var taskExecutor = newTaskExecutor();

      var arg1 = taskExecutor.submit(() -> {
        arg1Thread.set(currentThread());
        return output(7, newReport());
      });
      var arg2 = taskExecutor.submit(() -> {
        arg1Thread.set(currentThread());
        return output(3, newReport());
      });
      var result = taskExecutor.submit(
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
      Function<TaskExecutor, Promise<Maybe<R>>> scheduleFunction, R expectedValue) {
    var taskExecutor = newTaskExecutor();

    var result = scheduleFunction.apply(taskExecutor);
    await().until(() -> result.toMaybe().isSome());

    assertThat(result.get()).isEqualTo(some(expectedValue));
  }

  private static <R> void assertExecutionSubmitsReport(
      Function<TaskExecutor, Promise<R>> scheduleFunction, Report report) {
    assertExecutionSubmitsReport(Guice.createInjector(), scheduleFunction, report);
  }

  private static <R> void assertExecutionSubmitsReport(
      Injector injector, Function<TaskExecutor, Promise<R>> scheduleFunction, Report report) {
    var reporter = mock(Reporter.class);
    var taskExecutor = new TaskExecutor(injector, reporter, 4);

    var result = scheduleFunction.apply(taskExecutor);
    await().until(() -> result.toMaybe().isSome());

    verify(reporter).submit(report);
  }

  private static TaskExecutor newTaskExecutor() {
    return newTaskExecutor(Guice.createInjector());
  }

  private static TaskExecutor newTaskExecutor(Injector injector) {
    return new TaskExecutor(injector, mock(Reporter.class), 4);
  }

  private static TaskExecutor newTaskExecutor(AtomicInteger atomicInteger) {
    var injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(AtomicInteger.class).toInstance(atomicInteger);
      }
    });
    return new TaskExecutor(injector, new SystemOutReporter(), 4);
  }

  private static TaskExecutor newTaskExecutor(AtomicBoolean atomicBoolean) {
    var injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(AtomicBoolean.class).toInstance(atomicBoolean);
      }
    });
    return new TaskExecutor(injector, new SystemOutReporter(), 4);
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
    return report(EXECUTOR_LABEL, new Trace(), EXECUTION, list(fatal));
  }

  private static Report newReport() {
    return report(label("my-label"), new Trace(), EXECUTION, list());
  }

  private static Report newReportWithError() {
    return report(label("my-label"), new Trace(), EXECUTION, list(error("message")));
  }
}
