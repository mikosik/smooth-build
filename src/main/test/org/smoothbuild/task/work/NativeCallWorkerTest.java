package org.smoothbuild.task.work;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;
import static org.testory.Testory.willThrow;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.Invoker;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.message.base.CodeMessage;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.work.err.NullResultError;
import org.smoothbuild.task.work.err.ReflexiveInternalError;
import org.smoothbuild.task.work.err.UnexpectedError;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.task.exec.FakeNativeApi;
import org.smoothbuild.util.Empty;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class NativeCallWorkerTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  @SuppressWarnings("unchecked")
  Invoker<SString> invoker = mock(Invoker.class);
  FakeNativeApi nativeApi = new FakeNativeApi();
  HashCode hash = HashCode.fromInt(33);

  private final Signature<SString> signature = new Signature<>(STRING, name("name"),
      Empty.paramList());
  NativeFunction<?> function1 = new NativeFunction<>(Hash.integer(33), signature, invoker, true);
  NativeFunction<?> function2 = new NativeFunction<>(Hash.integer(33), signature, invoker, true);

  String name1 = "name1";
  String name2 = "name2";
  HashCode hash1 = HashCode.fromInt(1);
  HashCode hash2 = HashCode.fromInt(2);

  ImmutableList<Param> params = ImmutableList.of(param(STRING, name1, false), param(STRING, name2,
      false));

  NativeCallWorker<?> nativeCallWorker = new NativeCallWorker<>(function1,
      ImmutableList.<String>of(), codeLocation(1));

  @Test
  public void calculate_result() throws IllegalAccessException, InvocationTargetException {
    SString argValue = objectsDb.string("subTaskOutput");

    String name = "param";
    NativeCallWorker<?> nativeCallTask = new NativeCallWorker<>(function1, ImmutableList.of(name),
        codeLocation(1));

    SString sstring = objectsDb.string("result");
    given(willReturn(sstring), invoker).invoke(nativeApi, ImmutableMap.<String, SValue>of(name,
        argValue));

    TaskInput taskInput = TaskInput.fromValues(ImmutableList.of(argValue));
    TaskOutput<?> actual = nativeCallTask.execute(taskInput, nativeApi);
    assertThat(actual).isEqualTo(new TaskOutput<>(sstring));
  }

  @Test
  public void null_result_is_logged_when_function_has_non_void_return_type() throws Exception {
    given(willReturn(null), invoker).invoke(nativeApi, Empty.stringValueMap());
    nativeCallWorker.execute(TaskInput.fromValues(Empty.svalueList()), nativeApi);
    nativeApi.loggedMessages().assertContainsOnly(NullResultError.class);
  }

  @Test
  public void null_can_be_returned_when_function_logged_errors() throws Exception {
    ImmutableList<Param> params = ImmutableList.of();
    Signature<SString> signature = new Signature<>(STRING, name("name"), params);
    function1 = new NativeFunction<>(Hash.integer(33), signature, invoker, true);
    nativeCallWorker = new NativeCallWorker<>(function1, ImmutableList.<String>of(), codeLocation(
        1));
    given(new Handler() {
      @Override
      public Object handle(Invocation invocation) throws Throwable {
        NativeApi nativeApi = (NativeApi) invocation.arguments.get(0);
        nativeApi.log(new CodeMessage(ERROR, codeLocation(1), "message"));
        return null;
      }
    }, invoker).invoke(nativeApi, Empty.stringValueMap());

    nativeCallWorker.execute(TaskInput.fromValues(Empty.svalueList()), nativeApi);

    nativeApi.loggedMessages().assertContainsOnly(CodeMessage.class);
  }

  @Test
  public void reflexive_internal_error_is_logged_for_illegal_access_exception() throws Exception {
    assertExceptionIsLoggedAsProblem(new IllegalAccessException(""), ReflexiveInternalError.class);
  }

  @Test
  public void file_system_error_is_logged_for_file_system_exception() throws Exception {
    InvocationTargetException exception = new InvocationTargetException(new FileSystemError(""));
    assertExceptionIsLoggedAsProblem(exception, FileSystemError.class);
  }

  @Test
  public void message_thrown_as_error_message_exception_is_logged() throws Exception {
    InvocationTargetException exception = new InvocationTargetException(new MyError());
    assertExceptionIsLoggedAsProblem(exception, MyError.class);
  }

  @Test
  public void unexpected_error_is_logged_for_unexpected_runtime_exception() throws Exception {
    InvocationTargetException exception = new InvocationTargetException(new RuntimeException());
    assertExceptionIsLoggedAsProblem(exception, UnexpectedError.class);
  }

  @SuppressWarnings("serial")
  private static class MyError extends Message {
    public MyError() {
      super(ERROR, "message");
    }
  }

  private void assertExceptionIsLoggedAsProblem(Throwable thrown,
      Class<? extends Message> expected) throws Exception {
    given(willThrow(thrown), invoker).invoke(nativeApi, Empty.stringValueMap());
    nativeCallWorker.execute(TaskInput.fromValues(Empty.svalueList()), nativeApi);
    nativeApi.loggedMessages().assertContainsOnly(expected);
  }
}
