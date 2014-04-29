package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.NativeApiImpl;
import org.smoothbuild.task.work.TaskWorker;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.task.exec.FakeNativeApi;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class NativeFunctionTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final NativeApiImpl nativeApi = new FakeNativeApi();
  private SString sstring;
  private TaskWorker<SString> worker;

  private final Signature<SString> signature = new Signature<>(STRING, name("name"), Empty
      .paramList());
  @SuppressWarnings("unchecked")
  private final Invoker<SString> invoker = mock(Invoker.class);

  private final NativeFunction<SString> function = new NativeFunction<>(signature, invoker, true);
  private ImmutableMap<String, SValue> args;

  @Test(expected = NullPointerException.class)
  public void null_signature_is_forbidden() throws Exception {
    new NativeFunction<>(null, invoker, true);
  }

  @Test(expected = NullPointerException.class)
  public void null_invoker_is_forbidden() throws Exception {
    new NativeFunction<>(signature, null, true);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void invoke_is_forwarded_to_invoker() throws Exception {
    given(args = mock(ImmutableMap.class));
    when(function).invoke(nativeApi, args);
    thenCalled(invoker).invoke(nativeApi, args);
  }

  @Test
  public void task_worker_uses_invoker_for_calculating_result() throws Exception {
    given(sstring = objectsDb.string("result"));
    given(willReturn(sstring), invoker).invoke(nativeApi, Empty.stringValueMap());
    given(worker = function.createWorker(Empty.stringExprMap(), codeLocation(1)));
    when(worker).execute(TaskInput.fromTaskReturnValues(Empty.taskList()), nativeApi);
    thenReturned(new TaskOutput<>(sstring));
  }
}
