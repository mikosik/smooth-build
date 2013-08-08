package org.smoothbuild.registry;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.registry.exc.FunctionAlreadyRegisteredException;
import org.smoothbuild.registry.exc.FunctionImplementationException;
import org.smoothbuild.registry.instantiate.FunctionType;
import org.smoothbuild.registry.instantiate.FunctionTypeFactory;

public class FunctionTypesRegistryTest {
  FunctionTypeFactory functionTypeFactory = mock(FunctionTypeFactory.class);
  FunctionTypesRegistry functionTypesRegistry = new FunctionTypesRegistry(functionTypeFactory);

  @Test
  public void doesNotContainNotAddedType() throws Exception {
    assertThat(functionTypesRegistry.containsType("nameA")).isFalse();
  }

  @Test
  public void throwsExceptionWhenQueriedForNotRegisteredType() throws Exception {
    try {
      functionTypesRegistry.getType("abc");
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void containsAddedType() throws FunctionImplementationException,
      FunctionAlreadyRegisteredException {
    when(functionTypeFactory.create(MyFunction.class)).thenReturn(new FunctionType("nameA", null));
    functionTypesRegistry.register(MyFunction.class);
    assertThat(functionTypesRegistry.containsType("nameA")).isTrue();
  }

  @Test
  public void returnsAddedType() throws FunctionImplementationException,
      FunctionAlreadyRegisteredException {
    FunctionType function = new FunctionType("nameA", null);
    when(functionTypeFactory.create(MyFunction.class)).thenReturn(function);

    functionTypesRegistry.register(MyFunction.class);

    assertThat(functionTypesRegistry.getType("nameA")).isEqualTo(function);
  }

  @Test
  public void cannotRegisterTwiceUnderTheSameName() throws Exception {
    when(functionTypeFactory.create(MyFunction.class)).thenReturn(new FunctionType("nameA", null));
    when(functionTypeFactory.create(MyFunction2.class)).thenReturn(new FunctionType("nameA", null));

    functionTypesRegistry.register(MyFunction.class);
    try {
      functionTypesRegistry.register(MyFunction2.class);
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
}
