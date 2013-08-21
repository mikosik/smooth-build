package org.smoothbuild.expression;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smoothbuild.function.Function;
import org.smoothbuild.function.FunctionSignature;
import org.smoothbuild.lang.function.Type;

import com.google.common.collect.ImmutableMap;

public class FunctionExpressionTest {
  ExpressionId id = new ExpressionId("hash");
  @Mock
  Function function;
  @Mock
  Expression expressionA;
  @Mock
  Expression expressionB;

  private final String paramAName = "paramA";
  private final String paramBName = "paramB";

  ImmutableMap<String, Expression> arguments;

  FunctionExpression functionExpression;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
    arguments = ImmutableMap.of(paramAName, expressionA, paramBName, expressionB);
    functionExpression = new FunctionExpression(id, function, arguments);
  }

  @Test
  public void id() {
    assertThat(functionExpression.id()).isEqualTo(id);
  }

  @Test
  public void type() throws Exception {
    FunctionSignature signature = mock(FunctionSignature.class);
    when(signature.type()).thenReturn(Type.STRING);
    when(function.signature()).thenReturn(signature);

    assertThat(functionExpression.type()).isEqualTo(Type.STRING);
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
    when(function.execute(id.resultDir(), args)).thenReturn(result);

    // when
    functionExpression.calculate();

    // then
    assertThat(functionExpression.result()).isEqualTo(result);
  }

  @Test
  public void fetchingResultFailsWhenNoExecuteHasBeenCalled() throws Exception {
    try {
      functionExpression.result();
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }
}
