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
import org.smoothbuild.virtualmachine.bytecode.BytecodeF;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;

/**
 * Factory for creating classLoaders that load classes from jar file provided as BlobB.
 * This class is thread-safe.
 */
@Singleton
public class JarClassLoaderFactory {
  private final BytecodeF bytecodeF;
  private final ClassLoader parentClassLoader;
  private final Function1<BlobB, Either<String, ClassLoader>, BytecodeException> memoizer;

  @Inject
  public JarClassLoaderFactory(BytecodeF bytecodeF) {
    this(bytecodeF, getSystemClassLoader());
  }

  public JarClassLoaderFactory(BytecodeF bytecodeF, ClassLoader parentClassLoader) {
    this.bytecodeF = bytecodeF;
    this.parentClassLoader = parentClassLoader;
    this.memoizer = memoizer(this::newClassLoader);
  }

  public Either<String, ClassLoader> classLoaderFor(BlobB jar) throws BytecodeException {
    return memoizer.apply(jar);
  }

  private Either<String, ClassLoader> newClassLoader(BlobB jar) throws BytecodeException {
    return unzipBlob(bytecodeF, jar, s -> true)
        .mapRight(this::newClassLoader)
        .mapLeft(error -> "Error unpacking jar with native code: " + error);
  }

  private ClassLoader newClassLoader(ArrayB files) throws BytecodeException {
    var filesMap = files.elements(TupleB.class).toMap(f -> filePath(f).toJ(), x -> x);
    return newClassLoader(filesMap);
  }

  private ClassLoader newClassLoader(Map<String, TupleB> filesMap) {
    return mapClassLoader(parentClassLoader, path -> {
      TupleB file = filesMap.get(path);
      try {
        return file == null ? null : fileContent(file).source().inputStream();
      } catch (BytecodeException e) {
        throw e.toIOException();
      }
    });
  }
}
