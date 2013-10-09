package org.smoothbuild.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.testing.function.base.TestSignature.testSignature;

import org.junit.Test;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.task.Task;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class NativeFunctionTest {
  Sandbox sandbox = mock(Sandbox.class);
  String name = "functionName";
  CodeLocation codeLocation = codeLocation(1, 2, 4);

  Signature signature = testSignature("functionName");
  HashCode hash = HashCode.fromInt(33);
  Invoker invoker = mock(Invoker.class);

  NativeFunction function = new NativeFunction(signature, hash, invoker);

  @Test(expected = NullPointerException.class)
  public void nullInvokerIsForbidden() throws Exception {
    new NativeFunction(signature, hash, null);
  }

  @Test
  public void generateTaskReturnsTaskWithNoResultCalculated() throws Exception {
    Task task = function.generateTask(Empty.stringTaskMap(), codeLocation);
    assertThat(task.isResultCalculated()).isFalse();
  }

  @Test
  public void generateTaskReturnsTaskWithPassedDependencies() throws Exception {
    ImmutableMap<String, Task> dependencies = Empty.stringTaskMap();
    Task task = function.generateTask(dependencies, codeLocation);
    assertThat(task.dependencies()).isSameAs(dependencies.values());
  }

  @Test
  public void generatedTaskUsesInvokerForCalculatingResult() throws Exception {
    String result = "result";

    // given
    when(invoker.invoke(sandbox, Empty.stringObjectMap())).thenReturn(result);

    // when
    Task task = function.generateTask(Empty.stringTaskMap(), codeLocation);
    task.execute(sandbox);

    // then
    assertThat(task.isResultCalculated()).isTrue();
    assertThat(task.result()).isSameAs(result);
  }
}
