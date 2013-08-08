package org.smoothbuild.registry.instantiate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.type.FilesRw;
import org.smoothbuild.registry.exc.FunctionImplementationException;
import org.smoothbuild.registry.exc.IllegalConstructorParamException;
import org.smoothbuild.registry.exc.TooManyConstructorParamsException;
import org.smoothbuild.registry.exc.TooManyConstructorsException;

public class InstantiatorFactoryTest {
  Instantiator instantiator = mock(Instantiator.class);
  InstantiatorFactoryRaw factoryRaw = mock(InstantiatorFactoryRaw.class);
  InstantiatorFactory instantiatorFactory = new InstantiatorFactory(factoryRaw);

  @Test
  public void fileSystemParam() throws FunctionImplementationException {
    Class<? extends FunctionDefinition> klass = MyFileSystemFunction.class;
    Constructor<? extends FunctionDefinition> constructor = getConstructorOf(klass);

    when(factoryRaw.fileSystemInstantiator(constructor)).thenReturn(instantiator);
    assertThat(instantiatorFactory.create(klass)).isSameAs(instantiator);
  }

  public static class MyFileSystemFunction implements FunctionDefinition {
    public MyFileSystemFunction(FileSystem fileSystem) {}

    @Override
    public Params params() {
      return null;
    }

    @Override
    public Object execute() throws FunctionException {
      return null;
    }
  }

  @Test
  public void filesRwParam() throws FunctionImplementationException {
    Class<? extends FunctionDefinition> klass = MyFilesRwFunction.class;
    Constructor<? extends FunctionDefinition> constructor = getConstructorOf(klass);

    when(factoryRaw.filesRwInstantiator(constructor)).thenReturn(instantiator);
    assertThat(instantiatorFactory.create(klass)).isSameAs(instantiator);
  }

  public static class MyFilesRwFunction implements FunctionDefinition {
    public MyFilesRwFunction(FilesRw filesRw) {}

    @Override
    public Params params() {
      return null;
    }

    @Override
    public Object execute() throws FunctionException {
      return null;
    }
  }

  @Test
  public void noArg() throws FunctionImplementationException {
    Class<? extends FunctionDefinition> klass = MyNoArgFunction.class;
    Constructor<? extends FunctionDefinition> constructor = getConstructorOf(klass);

    when(factoryRaw.noArg(constructor)).thenReturn(instantiator);
    assertThat(instantiatorFactory.create(klass)).isSameAs(instantiator);
  }

  public static class MyNoArgFunction implements FunctionDefinition {
    public MyNoArgFunction() {}

    @Override
    public Params params() {
      return null;
    }

    @Override
    public Object execute() throws FunctionException {
      return null;
    }
  }

  @Test
  public void noArgIsCalledForDefaultConstructor() throws FunctionImplementationException {
    Class<? extends FunctionDefinition> klass = MyDefaultConstructorFunction.class;
    Constructor<? extends FunctionDefinition> constructor = getConstructorOf(klass);

    when(factoryRaw.noArg(constructor)).thenReturn(instantiator);
    assertThat(instantiatorFactory.create(klass)).isSameAs(instantiator);
  }

  public static class MyDefaultConstructorFunction implements FunctionDefinition {
    @Override
    public Params params() {
      return null;
    }

    @Override
    public Object execute() throws FunctionException {
      return null;
    }
  }

  @Test
  public void wrongParamTypeInConstructor() throws FunctionImplementationException {
    try {
      instantiatorFactory.create(MyWrongParamTypeInConstructorFunction.class);
      Assert.fail("exception should be thrown");
    } catch (IllegalConstructorParamException e) {
      // expected
    }
  }

  public static class MyWrongParamTypeInConstructorFunction implements FunctionDefinition {
    public MyWrongParamTypeInConstructorFunction(String wrongType) {}

    @Override
    public Params params() {
      return null;
    }

    @Override
    public Object execute() throws FunctionException {
      return null;
    }
  }

  @Test
  public void tooManyConstructors() throws FunctionImplementationException {
    try {
      instantiatorFactory.create(MyTooManyConstructorsFunction.class);
      Assert.fail("exception should be thrown");
    } catch (TooManyConstructorsException e) {
      // expected
    }
  }

  public static class MyTooManyConstructorsFunction implements FunctionDefinition {
    public MyTooManyConstructorsFunction(FilesRw filesRw) {}

    public MyTooManyConstructorsFunction(FileSystem fileSystem) {}

    @Override
    public Params params() {
      return null;
    }

    @Override
    public Object execute() throws FunctionException {
      return null;
    }
  }

  @Test
  public void toManyParamsInConstructor() throws FunctionImplementationException {
    try {
      instantiatorFactory.create(MyTooManyParamsInConstructorFunction.class);
      Assert.fail("exception should be thrown");
    } catch (TooManyConstructorParamsException e) {
      // expected
    }
  }

  public static class MyTooManyParamsInConstructorFunction implements FunctionDefinition {
    public MyTooManyParamsInConstructorFunction(FilesRw a, FilesRw b) {}

    @Override
    public Params params() {
      return null;
    }

    @Override
    public Object execute() throws FunctionException {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private static Constructor<? extends FunctionDefinition> getConstructorOf(Class<? extends FunctionDefinition> klass) {
    return (Constructor<? extends FunctionDefinition>) klass.getConstructors()[0];
  }
}
