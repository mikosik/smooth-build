package org.smoothbuild.function.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionId;

import com.google.common.collect.ImmutableMap;

public class PluginFunctionExpressionTest {
  ExpressionId id = new ExpressionId("hash");
  @Mock
  PluginInvoker pluginInvoker;
  @Mock
  Expression expressionA;
  @Mock
  Expression expressionB;

  Type type = Type.STRING;
  private final String paramAName = "paramA";
  private final String paramBName = "paramB";

  ImmutableMap<String, Expression> arguments;

  PluginFunctionExpression pluginFunctionExpression;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    arguments = ImmutableMap.of(paramAName, expressionA, paramBName, expressionB);
    pluginFunctionExpression = new PluginFunctionExpression(id, type, pluginInvoker, arguments);
  }

  @Test
  public void id() {
    assertThat(pluginFunctionExpression.id()).isEqualTo(id);
  }

  @Test
  public void type() throws Exception {
    assertThat(pluginFunctionExpression.type()).isEqualTo(type);
  }

  @Test
  public void execute() throws Exception {
    // given
    String result = "abc";
    Object valueA = new Object();
    Object valueB = new Object();

    when(expressionA.result()).thenReturn(valueA);
    when(expressionB.result()).thenReturn(valueB);

    ImmutableMap<String, Object> args = ImmutableMap.of(paramAName, valueA, paramBName, valueB);
    when(pluginInvoker.invoke(id.resultDir(), args)).thenReturn(result);

    // when
    pluginFunctionExpression.calculate();

    // then
    assertThat(pluginFunctionExpression.result()).isEqualTo(result);
  }

  // / TODO suppressed @Test
  public void fetchingResultFailsWhenNoExecuteHasBeenCalled() throws Exception {
    try {
      pluginFunctionExpression.result();
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }
}
