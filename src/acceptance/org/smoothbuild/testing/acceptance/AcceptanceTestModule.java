package org.smoothbuild.testing.acceptance;

import static org.smoothbuild.lang.function.nativ.NativeFunctionFactory.nativeFunctions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.smoothbuild.builtin.android.AidlFunction;
import org.smoothbuild.builtin.blob.ConcatenateBlobsFunction;
import org.smoothbuild.builtin.blob.ToFileFunction;
import org.smoothbuild.builtin.blob.ToStringFunction;
import org.smoothbuild.builtin.compress.UnzipFunction;
import org.smoothbuild.builtin.compress.ZipFunction;
import org.smoothbuild.builtin.convert.FileArrayToBlobArrayFunction;
import org.smoothbuild.builtin.convert.FileToBlobFunction;
import org.smoothbuild.builtin.convert.NilToBlobArrayFunction;
import org.smoothbuild.builtin.convert.NilToFileArrayFunction;
import org.smoothbuild.builtin.convert.NilToStringArrayFunction;
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
import org.smoothbuild.builtin.string.ConcatenateStringsFunction;
import org.smoothbuild.builtin.string.ToBlobFunction;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.objects.ObjectsDbModule;
import org.smoothbuild.db.taskoutputs.TaskOutputsDbModule;
import org.smoothbuild.io.util.SmoothJar;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.function.nativ.err.NativeFunctionImplementationException;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.lang.module.ModuleBuilder;
import org.smoothbuild.parse.Builtin;
import org.smoothbuild.testing.io.fs.base.FakeFileSystemModule;
import org.smoothbuild.testing.message.FakeUserConsoleModule;

import com.google.common.hash.HashCode;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class AcceptanceTestModule extends AbstractModule {
  private final Class<?> nativeFunctions;

  public AcceptanceTestModule() {
    this.nativeFunctions = null;
  }

  public AcceptanceTestModule(Class<?> nativeFunctions) {
    this.nativeFunctions = nativeFunctions;

  }

  @Override
  protected void configure() {
    install(new TaskOutputsDbModule());
    install(new ObjectsDbModule());
    install(new FakeFileSystemModule());
    install(new FakeUserConsoleModule());
  }

  @Provides
  @SmoothJar
  public HashCode provideSmoothJarHash() {
    return HashCode.fromInt(1);
  }

  @Provides
  @Singleton
  @Builtin
  public Module provideBuiltinModule() throws NativeFunctionImplementationException {
    List<Class<?>> functions = new ArrayList<>();

    functions.add(FileToBlobFunction.class);
    functions.add(FileArrayToBlobArrayFunction.class);
    functions.add(NilToStringArrayFunction.class);
    functions.add(NilToBlobArrayFunction.class);
    functions.add(NilToFileArrayFunction.class);

    functions.add(AidlFunction.class);
    functions.add(ConcatenateBlobsFunction.class);
    functions.add(ConcatenateFilesFunction.class);
    functions.add(ConcatenateStringsFunction.class);
    functions.add(ContentFunction.class);
    functions.add(FileFunction.class);
    functions.add(FilesFunction.class);
    functions.add(FilterFunction.class);
    functions.add(JarjarFunction.class);
    functions.add(JavacFunction.class);
    functions.add(JarFunction.class);
    functions.add(JunitFunction.class);
    functions.add(PathFunction.class);
    functions.add(ToBlobFunction.class);
    functions.add(ToFileFunction.class);
    functions.add(ToStringFunction.class);
    functions.add(UnjarFunction.class);
    functions.add(UnzipFunction.class);
    functions.add(ZipFunction.class);
    if (nativeFunctions != null) {
      functions.add(nativeFunctions);
    }

    return createNativeModule(functions.toArray(new Class[] {}));
  }

  public static Module createNativeModule(Class<?>... classes)
      throws NativeFunctionImplementationException {
    HashCode jarHash = Hash.integer(33);
    ModuleBuilder builder = new ModuleBuilder();
    for (Class<?> clazz : classes) {
      for (NativeFunction function : nativeFunctions(clazz, jarHash)) {
        builder.addFunction(function);
      }
    }
    return builder.build();
  }
}
