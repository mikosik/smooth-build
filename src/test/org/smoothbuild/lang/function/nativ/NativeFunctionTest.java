package org.smoothbuild.lang.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.testing.lang.function.base.FakeSignature.fakeSignature;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.NativeApiImpl;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.exec.FakeNativeApi;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class NativeFunctionTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final TaskGenerator taskGenerator = mock(TaskGenerator.class);
  private final NativeApiImpl nativeApi = new FakeNativeApi();
  private final CodeLocation codeLocation = new FakeCodeLocation();

  private final Signature<SString> signature = fakeSignature("functionName");
  @SuppressWarnings("unchecked")
  private final Invoker<SString> invoker = mock(Invoker.class);

  private final NativeFunction<SString> function = new NativeFunction<>(signature, invoker, true);

  @Test(expected = NullPointerException.class)
  public void nullSignatureIsForbidden() throws Exception {
    new NativeFunction<>(null, invoker, true);
  }

  @Test(expected = NullPointerException.class)
  public void nullInvokerIsForbidden() throws Exception {
    new NativeFunction<>(signature, null, true);
  }

  @Test
  public void invokeIsForwardedToInvoker() throws Exception {
    @SuppressWarnings("unchecked")
    ImmutableMap<String, SValue> args = mock(ImmutableMap.class);
    function.invoke(nativeApi, args);
    thenCalled(invoker).invoke(nativeApi, args);
  }

  @Test
  public void generatedTaskUsesInvokerForCalculatingResult() throws Exception {
    SString result = objectsDb.string("result");

    // given
    given(willReturn(result), invoker).invoke(nativeApi, Empty.stringValueMap());

    // when
    Task<?> task = function.generateTask(taskGenerator, Empty.stringTaskResultMap(), codeLocation);
    SString actual = (SString) task.execute(nativeApi);

    // then
    assertThat(actual).isSameAs(result);
  }
}
