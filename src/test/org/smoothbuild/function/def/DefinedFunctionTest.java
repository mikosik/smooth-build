package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.testing.function.base.TestSignature.testSignature;

import org.junit.Test;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.TaskGenerator;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class DefinedFunctionTest {
  Signature signature = testSignature();
  DefinitionNode root = mock(DefinitionNode.class);
  CodeLocation codeLocation = codeLocation(1, 2, 4);

  DefinedFunction definedFunction = new DefinedFunction(signature, root);

  @Test(expected = NullPointerException.class)
  public void nullRootIsForbidden() {
    new DefinedFunction(signature, null);
  }

  @Test
  public void generateTaskWithEmptyDependency() throws Exception {
    Task task = mock(Task.class);
    TaskGenerator taskGenerator = mock(TaskGenerator.class);
    when(root.generateTask(taskGenerator)).thenReturn(task);

    Task actual = definedFunction.generateTask(taskGenerator, Empty.stringHashMap(), codeLocation);

    assertThat(actual).isSameAs(task);
  }

  @Test(expected = IllegalArgumentException.class)
  public void generateTaskThrowsExceptionWhenDependenciesAreNotEmpty() throws Exception {
    TaskGenerator taskGenerator = mock(TaskGenerator.class);

    definedFunction.generateTask(taskGenerator, ImmutableMap.of("name", HashCode.fromInt(33)),
        codeLocation);
  }

  @Test
  public void generateTaskForwardsCallToRootDefinitionNode() throws Exception {
    Task task = mock(Task.class);
    TaskGenerator taskGenerator = mock(TaskGenerator.class);
    when(root.generateTask(taskGenerator)).thenReturn(task);

    Task actual = definedFunction.generateTask(taskGenerator);

    assertThat(actual).isSameAs(task);
  }
}
