package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.testing.TestingSignature.testingSignature;

import org.junit.Test;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.task.Task;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class DefinedFunctionTest {
  Signature signature = testingSignature();
  DefinitionNode root = mock(DefinitionNode.class);

  DefinedFunction definedFunction = new DefinedFunction(signature, root);

  @Test(expected = NullPointerException.class)
  public void nullRootIsForbidden() {
    new DefinedFunction(signature, null);
  }

  @Test
  public void generateTask() throws Exception {
    Task task = mock(Task.class);
    when(root.generateTask()).thenReturn(task);

    Task actual = definedFunction.generateTask(Empty.stringTaskMap());

    assertThat(actual).isSameAs(task);
  }

  @Test(expected = IllegalArgumentException.class)
  public void generateTaskThrowsExceptionWhenDependenciesAreNotEmpty() throws Exception {
    definedFunction.generateTask(ImmutableMap.of("name", mock(Task.class)));
  }
}
