package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.testing.TestingSignature.testingSignature;

import java.util.Map;

import org.junit.Test;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionIdFactory;

import com.google.common.collect.Maps;

public class DefinedFunctionsCreatorTest {
  Signature signature = testingSignature("name");
  DefinitionNode root = mock(DefinitionNode.class);

  DefinedFunction function = new DefinedFunction(signature, root);

  @Test(expected = NullPointerException.class)
  public void nullRootIsForbidden() {
    new DefinedFunction(signature, null);
  }

  @Test(expected = NullPointerException.class)
  public void nullSignatureIsForbidden() {
    new DefinedFunction(null, root);
  }

  @Test
  public void apply() throws Exception {
    ExpressionIdFactory idFactory = mock(ExpressionIdFactory.class);
    Map<String, Expression> arguments = Maps.newHashMap();
    Expression expression = mock(Expression.class);

    when(root.expression(idFactory)).thenReturn(expression);

    assertThat(function.apply(idFactory, arguments)).isSameAs(expression);
  }
}
