package org.smoothbuild.function.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.expr.LiteralExpression.stringExpression;
import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingSignature.testingSignature;

import org.junit.Test;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionId;
import org.smoothbuild.function.expr.ExpressionIdFactory;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.exc.FunctionException;
import org.smoothbuild.problem.ProblemsListener;
import org.smoothbuild.task.Task;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class PluginFunctionTest {
  String name = "functionName";

  Signature signature = testingSignature("functionName");
  PluginInvoker invoker = mock(PluginInvoker.class);
  ExpressionIdFactory idFactory = mock(ExpressionIdFactory.class);

  PluginFunction function = new PluginFunction(signature, invoker);

  // TODO this test is so ugly

  @Test
  public void execute() throws FunctionException {
    ExpressionId expressionId = new ExpressionId("hash");
    Object result = "result string";
    String paramValue = "param value";
    Expression paramExpression = stringExpression(mock(ExpressionId.class), paramValue);
    ImmutableMap<String, Expression> exprMap = ImmutableMap.of("param", paramExpression);
    ImmutableMap<String, Object> argMap = ImmutableMap.of("param", (Object) paramValue);

    when(idFactory.createId(name)).thenReturn(expressionId);
    when(invoker.invoke(expressionId.resultDir(), argMap)).thenReturn(result);

    Object actual = function.apply(idFactory, exprMap).result();
    assertThat(actual).isEqualTo(result);
  }

  @Test
  public void generateTaskReturnsTaskWithNoResultCalculated() throws Exception {
    Task task = function.generateTask(Empty.stringTaskMap());
    assertThat(task.isResultCalculated()).isFalse();
  }

  @Test
  public void generateTaskReturnsTaskWithPassedDependencies() throws Exception {
    ImmutableMap<String, Task> dependencies = Empty.stringTaskMap();
    Task task = function.generateTask(dependencies);
    assertThat(task.dependencies()).isSameAs(dependencies);
  }

  @Test
  public void generatedTaskUsesPluginInvokerForCalculatingResult() throws Exception {
    ProblemsListener problemsListener = mock(ProblemsListener.class);
    Path tempDir = path("tempPath");
    String result = "result";

    // given
    when(invoker.invoke(tempDir, Empty.stringObjectMap())).thenReturn(result);

    // when
    Task task = function.generateTask(Empty.stringTaskMap());
    task.calculateResult(problemsListener, tempDir);

    // then
    assertThat(task.isResultCalculated()).isTrue();
    assertThat(task.result()).isSameAs(result);
  }
}
