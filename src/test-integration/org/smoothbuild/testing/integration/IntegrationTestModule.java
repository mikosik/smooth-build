package org.smoothbuild.testing.integration;

import static org.smoothbuild.lang.function.nativ.NativeFunctionFactory.createNativeFunctions;

import javax.inject.Singleton;

import org.smoothbuild.builtin.android.AidlFunction;
import org.smoothbuild.builtin.blob.ConcatenateBlobsFunction;
import org.smoothbuild.builtin.blob.ToFileFunction;
import org.smoothbuild.builtin.blob.ToStringFunction;
import org.smoothbuild.builtin.compress.UnzipFunction;
import org.smoothbuild.builtin.compress.ZipFunction;
import org.smoothbuild.builtin.file.ConcatenateFilesFunction;
import org.smoothbuild.builtin.file.ContentFunction;
import org.smoothbuild.builtin.file.FileFunction;
import org.smoothbuild.builtin.file.FilesFunction;
import org.smoothbuild.builtin.file.FilterFunction;
import org.smoothbuild.builtin.file.PathFunction;
import org.smoothbuild.builtin.java.JarFunction;
import org.smoothbuild.builtin.java.JarjarFunction;
import org.smoothbuild.builtin.java.UnjarFunction;
import org.smoothbuild.builtin.java.javac.JavacFunction;
import org.smoothbuild.builtin.java.junit.JunitFunction;
import org.smoothbuild.builtin.string.ToBlobFunction;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.objects.ObjectsDbModule;
import org.smoothbuild.db.taskoutputs.TaskOutputsDbModule;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.function.nativ.err.NativeImplementationException;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.lang.module.ModuleBuilder;
import org.smoothbuild.parse.Builtin;
import org.smoothbuild.testing.io.fs.base.FakeFileSystemModule;
import org.smoothbuild.testing.message.FakeUserConsoleModule;

import com.google.common.hash.HashCode;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class IntegrationTestModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new TaskOutputsDbModule());
    install(new ObjectsDbModule());
    install(new FakeFileSystemModule());
    install(new FakeUserConsoleModule());
  }

  @Provides
  @Singleton
  @Builtin
  public Module provideBuiltinModule(ModuleBuilder builder) throws NativeImplementationException {
    // @formatter:off
    return createNativeModule(
        AidlFunction.class,
        ConcatenateBlobsFunction.class,
        ConcatenateFilesFunction.class,
        ContentFunction.class,
        FileFunction.class,
        FilesFunction.class,
        FilterFunction.class,
        JarjarFunction.class,
        JavacFunction.class,
        JarFunction.class,
        JunitFunction.class,
        PathFunction.class,
        ToBlobFunction.class,
        ToFileFunction.class,
        ToStringFunction.class,
        UnjarFunction.class,
        UnzipFunction.class,
        ZipFunction.class);
    // @formatter:on
  }

  public static Module createNativeModule(Class<?>... classes) throws NativeImplementationException {
    HashCode jarHash = Hash.integer(33);
    ModuleBuilder builder = new ModuleBuilder();
    for (Class<?> clazz : classes) {
      for (NativeFunction<?> function : createNativeFunctions(jarHash, clazz)) {
        builder.addFunction(function);
      }
    }
    return builder.build();
  }
}
