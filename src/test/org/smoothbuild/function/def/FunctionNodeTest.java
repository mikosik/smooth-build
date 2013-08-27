package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionIdFactory;

import com.google.common.collect.ImmutableMap;

public class FunctionNodeTest {
  ExpressionIdFactory idFactory = mock(ExpressionIdFactory.class);
  Function function = mock(Function.class);

  @Test
  public void type() throws Exception {
    when(function.type()).thenReturn(Type.STRING);
    ImmutableMap<String, DefinitionNode> empty = ImmutableMap.<String, DefinitionNode> of();

    assertThat(new FunctionNode(function, empty).type()).isEqualTo(Type.STRING);
  }

  @Test
  public void test() {
    String name1 = "param1";
    Expression expression1 = mock(Expression.class);
    DefinitionNode child1 = subNode(expression1);

    Expression expression2 = mock(Expression.class);
    DefinitionNode child2 = subNode(expression2);
    String name2 = "param2";

    Expression resultExpression = mock(Expression.class);
    ImmutableMap<String, Expression> args = ImmutableMap.of(name1, expression1, name2, expression2);
    when(function.apply(idFactory, args)).thenReturn(resultExpression);

    FunctionNode node = new FunctionNode(function, ImmutableMap.of(name1, child1, name2, child2));

    assertThat(node.expression(idFactory)).isSameAs(resultExpression);
  }

  private FunctionNode subNode(Expression expression1) {
    FunctionNode subNode1 = mock(FunctionNode.class);
    when(subNode1.expression(idFactory)).thenReturn(expression1);
    return subNode1;
  }
}
