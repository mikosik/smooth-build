package org.smoothbuild.virtualmachine.bytecode.load;

import static java.lang.ClassLoader.getSystemClassLoader;
import static org.smoothbuild.common.function.Function1.memoizer;
import static org.smoothbuild.common.reflect.ClassLoaders.mapClassLoader;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.fileContent;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;
import static org.smoothbuild.virtualmachine.evaluate.plugin.UnzipBlob.unzipBlob;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;

/**
 * Factory for creating classLoaders that load classes from jar file provided as BlobB.
 * This class is thread-safe.
 */
@Singleton
public class JarClassLoaderFactory {
  private final BytecodeFactory bytecodeFactory;
  private final ClassLoader parentClassLoader;
  private final Function1<BBlob, Either<String, ClassLoader>, BytecodeException> memoizer;

  @Inject
  public JarClassLoaderFactory(BytecodeFactory bytecodeFactory) {
    this(bytecodeFactory, getSystemClassLoader());
  }

  public JarClassLoaderFactory(BytecodeFactory bytecodeFactory, ClassLoader parentClassLoader) {
    this.bytecodeFactory = bytecodeFactory;
    this.parentClassLoader = parentClassLoader;
    this.memoizer = memoizer(this::newClassLoader);
  }

  public Either<String, ClassLoader> classLoaderFor(BBlob jar) throws BytecodeException {
    return memoizer.apply(jar);
  }

  private Either<String, ClassLoader> newClassLoader(BBlob jar) throws BytecodeException {
    return unzipBlob(bytecodeFactory, jar, s -> true)
        .mapRight(this::newClassLoader)
        .mapLeft(error -> "Error unpacking jar with native code: " + error);
  }

  private ClassLoader newClassLoader(BArray files) throws BytecodeException {
    var filesMap = files.elements(BTuple.class).toMap(f -> filePath(f).toJavaString(), x -> x);
    return newClassLoader(filesMap);
  }

  private ClassLoader newClassLoader(Map<String, BTuple> filesMap) {
    return mapClassLoader(parentClassLoader, path -> {
      BTuple file = filesMap.get(path);
      try {
        return file == null ? null : fileContent(file).source().inputStream();
      } catch (BytecodeException e) {
        throw e.toIOException();
      }
    });
  }
}
