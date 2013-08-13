package org.smoothbuild.registry;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.lang.function.CanonicalName.canonicalName;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.Type;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.registry.exc.FunctionAlreadyRegisteredException;
import org.smoothbuild.registry.exc.FunctionImplementationException;
import org.smoothbuild.registry.instantiate.Function;
import org.smoothbuild.registry.instantiate.FunctionFactory;

public class FunctionsRegistryTest {
  FunctionFactory functionFactory = mock(FunctionFactory.class);
  FunctionsRegistry functionsRegistry = new FunctionsRegistry(functionFactory);

  @Test
  public void doesNotContainNotAddedType() throws Exception {
    assertThat(functionsRegistry.containsType("nameA")).isFalse();
  }

  @Test
  public void throwsExceptionWhenQueriedForNotRegisteredType() throws Exception {
    try {
      functionsRegistry.getType("abc");
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void containsAddedType() throws FunctionImplementationException,
      FunctionAlreadyRegisteredException {
    String name = "nameA";
    when(functionFactory.create(MyFunction.class)).thenReturn(function(name));
    functionsRegistry.register(MyFunction.class);
    assertThat(functionsRegistry.containsType(name)).isTrue();
  }

  @Test
  public void returnsAddedType() throws FunctionImplementationException,
      FunctionAlreadyRegisteredException {
    String name = "nameA";
    Function function = function(name);
    when(functionFactory.create(MyFunction.class)).thenReturn(function);

    functionsRegistry.register(MyFunction.class);

    assertThat(functionsRegistry.getType(name)).isEqualTo(function);
  }

  @Test
  public void cannotRegisterTwiceUnderTheSameName() throws Exception {
    String name = "nameA";
    when(functionFactory.create(MyFunction.class)).thenReturn(function(name));
    when(functionFactory.create(MyFunction2.class)).thenReturn(function(name));

    functionsRegistry.register(MyFunction.class);
    try {
      functionsRegistry.register(MyFunction2.class);
      Assert.fail("exception should be thrown");
    } catch (FunctionAlreadyRegisteredException e) {
      // expected
    }
  }

  public static class MyFunction implements FunctionDefinition {
    @Override
    public Params params() {
      return null;
    }

    @Override
    public Object execute() throws FunctionException {
      return null;
    }
  }

  public static class MyFunction2 implements FunctionDefinition {
    @Override
    public Params params() {
      return null;
    }

    @Override
    public Object execute() throws FunctionException {
      return null;
    }
  }

  private static Function function(String name) {
    return new Function(canonicalName(name), Type.STRING, null);
  }
}
