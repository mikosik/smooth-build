package org.smoothbuild.registry.instantiate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smoothbuild.lang.function.Type;

import com.google.common.collect.ImmutableMap;

public class FunctionExpressionTest {
  ExpressionId id = new ExpressionId("hash");
  ImmutableMap<String, Expression> arguments = ImmutableMap.of();
  @Mock
  Function function;

  FunctionExpression functionExpression;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    functionExpression = new FunctionExpression(id, function, arguments);
  }

  @Test
  public void id() {
    assertThat(functionExpression.id()).isEqualTo(id);
  }

  @Test
  public void type() throws Exception {
    when(function.type()).thenReturn(Type.STRING);
    Type actual = functionExpression.type();
    assertThat(actual).isEqualTo(Type.STRING);
  }

  @Test
  public void execute() throws Exception {
    String result = "abc";
    when(function.execute(id.resultDir(), arguments)).thenReturn(result);

    functionExpression.calculate();

    assertThat(functionExpression.result()).isEqualTo(result);
  }
}
