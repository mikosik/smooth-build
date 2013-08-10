package org.smoothbuild.registry.instantiate;

import java.lang.reflect.Constructor;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.internal.FilesImpl;
import org.smoothbuild.lang.type.Path;
import org.smoothbuild.registry.exc.CreatingInstanceFailedException;

public class InstantiatorFactoryRaw {
  private final FileSystem fileSystem;
  private final ConstructorInvoker constructorInvoker;

  public InstantiatorFactoryRaw(FileSystem fileSystem, ConstructorInvoker constructorInvoker) {
    this.fileSystem = fileSystem;
    this.constructorInvoker = constructorInvoker;
  }

  public Instantiator noArgInstantiator(final Constructor<? extends FunctionDefinition> constructor) {
    return new Instantiator() {
      @Override
      public FunctionDefinition newInstance(Path resultDir) throws CreatingInstanceFailedException {
        return constructorInvoker.invoke(constructor);
      }
    };
  }

  public Instantiator filesPassingInstantiator(
      final Constructor<? extends FunctionDefinition> constructor) {
    return new Instantiator() {
      @Override
      public FunctionDefinition newInstance(Path resultDir) throws CreatingInstanceFailedException {
        return constructorInvoker.invoke(constructor, new FilesImpl(fileSystem, resultDir));
      }
    };
  }

  public Instantiator fileSystemPassingInstantiator(
      final Constructor<? extends FunctionDefinition> constructor) {
    return new Instantiator() {
      @Override
      public FunctionDefinition newInstance(Path resultDir) throws CreatingInstanceFailedException {
        return constructorInvoker.invoke(constructor, fileSystem);
      }
    };
  }
}
