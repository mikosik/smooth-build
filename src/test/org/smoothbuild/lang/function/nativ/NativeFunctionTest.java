package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.testing.lang.function.base.FakeSignature.fakeSignature;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.db.taskresults.TaskResult;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskWorker;
import org.smoothbuild.task.exec.NativeApiImpl;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.exec.FakeNativeApi;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class NativeFunctionTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final NativeApiImpl nativeApi = new FakeNativeApi();
  private final CodeLocation codeLocation = new FakeCodeLocation();
  private SString sstring;
  private TaskWorker<SString> task;

  private final Signature<SString> signature = fakeSignature("functionName");
  @SuppressWarnings("unchecked")
  private final Invoker<SString> invoker = mock(Invoker.class);

  private final NativeFunction<SString> function = new NativeFunction<>(signature, invoker, true);

  @Test(expected = NullPointerException.class)
  public void null_signature_is_forbidden() throws Exception {
    new NativeFunction<>(null, invoker, true);
  }

  @Test(expected = NullPointerException.class)
  public void null_invoker_is_forbidden() throws Exception {
    new NativeFunction<>(signature, null, true);
  }

  @Test
  public void invoke_is_forwarded_to_invoker() throws Exception {
    @SuppressWarnings("unchecked")
    ImmutableMap<String, SValue> args = mock(ImmutableMap.class);
    function.invoke(nativeApi, args);
    thenCalled(invoker).invoke(nativeApi, args);
  }

  @Test
  public void generated_task_uses_invoker_for_calculating_result() throws Exception {
    given(sstring = objectsDb.string("result"));
    given(willReturn(sstring), invoker).invoke(nativeApi, Empty.stringValueMap());
    given(task = function.createWorker(ImmutableMap.<String, Expr<?>> of(), codeLocation));
    when(task).execute(ImmutableList.<SValue> of(), nativeApi);
    thenReturned(new TaskResult<>(sstring));
  }
}
