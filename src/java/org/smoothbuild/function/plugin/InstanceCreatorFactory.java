package org.smoothbuild.function.plugin;

import java.lang.reflect.Constructor;

import javax.inject.Inject;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.plugin.FileListImpl;
import org.smoothbuild.function.plugin.exc.CreatingInstanceFailedException;
import org.smoothbuild.plugin.Path;

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
        FileListImpl constructorParam = new FileListImpl(fileSystem, resultDir);
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
