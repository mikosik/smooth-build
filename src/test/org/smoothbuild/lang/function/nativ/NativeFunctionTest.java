package org.smoothbuild.lang.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smoothbuild.testing.lang.function.base.FakeSignature.fakeSignature;

import org.junit.Test;
import org.smoothbuild.io.cache.task.TaskDb;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.SandboxImpl;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.lang.type.FakeString;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.exec.FakeSandbox;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class NativeFunctionTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  SandboxImpl sandbox = new FakeSandbox();
  String name = "functionName";
  CodeLocation codeLocation = new FakeCodeLocation();

  TaskDb taskDb = mock(TaskDb.class);
  Signature signature = fakeSignature("functionName");
  Invoker invoker = mock(Invoker.class);

  NativeFunction function = new NativeFunction(signature, invoker, true);

  @Test(expected = NullPointerException.class)
  public void nullSignatureIsForbidden() throws Exception {
    new NativeFunction(null, invoker, true);
  }

  @Test(expected = NullPointerException.class)
  public void nullInvokerIsForbidden() throws Exception {
    new NativeFunction(signature, null, true);
  }

  @Test
  public void invokeIsForwardedToInvoker() throws Exception {
    @SuppressWarnings("unchecked")
    ImmutableMap<String, Value> args = mock(ImmutableMap.class);
    function.invoke(sandbox, args);
    verify(invoker).invoke(sandbox, args);
  }

  @Test
  public void generatedTaskUsesInvokerForCalculatingResult() throws Exception {
    SString result = new FakeString("result");

    // given
    when(invoker.invoke(sandbox, Empty.stringValueMap())).thenReturn(result);

    // when
    Task task = function.generateTask(taskGenerator, Empty.stringTaskResultMap(), codeLocation);
    SString actual = (SString) task.execute(sandbox);

    // then
    assertThat(actual).isSameAs(result);
  }
}
