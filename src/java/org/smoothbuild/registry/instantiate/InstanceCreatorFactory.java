package org.smoothbuild.registry.instantiate;

import java.lang.reflect.Constructor;

import javax.inject.Inject;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.internal.FilesImpl;
import org.smoothbuild.lang.type.Path;
import org.smoothbuild.registry.exc.CreatingInstanceFailedException;

public class InstanceCreatorFactory {
  private final FileSystem fileSystem;
  private final ReflexiveInvoker reflexiveInvoker;

  @Inject
  public InstanceCreatorFactory(FileSystem fileSystem, ReflexiveInvoker reflexiveInvoker) {
    this.fileSystem = fileSystem;
    this.reflexiveInvoker = reflexiveInvoker;
  }

  public InstanceCreator noArgInstanceCreator(final Constructor<?> constructor) {
    return new InstanceCreator() {
      public Object createInstance(Path resultDir) throws CreatingInstanceFailedException {
        return reflexiveInvoker.invokeConstructor(constructor);
      }
    };
  }

  public InstanceCreator filesPassingCreator(final Constructor<?> constructor) {
    return new InstanceCreator() {
      public Object createInstance(Path resultDir) throws CreatingInstanceFailedException {
        FilesImpl constructorParam = new FilesImpl(fileSystem, resultDir);
        return reflexiveInvoker.invokeConstructor(constructor, constructorParam);
      }
    };
  }

  public InstanceCreator fileSystemPassingCreator(final Constructor<?> constructor) {
    return new InstanceCreator() {
      public Object createInstance(Path resultDir) throws CreatingInstanceFailedException {
        return reflexiveInvoker.invokeConstructor(constructor, fileSystem);
      }
    };
  }
}
