package org.smoothbuild.vm.bytecode.load;

import static java.lang.ClassLoader.getSystemClassLoader;
import static org.smoothbuild.common.collect.Maps.computeIfAbsent;
import static org.smoothbuild.common.reflect.ClassLoaders.mapClassLoader;
import static org.smoothbuild.run.eval.FileStruct.fileContent;
import static org.smoothbuild.run.eval.FileStruct.filePath;
import static org.smoothbuild.vm.evaluate.plugin.UnzipBlob.unzipBlob;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;

/**
 * This class is thread-safe.
 */
@Singleton
public class JarClassLoaderFactory {
  private final BytecodeF bytecodeF;
  private final ClassLoader parentClassLoader;
  private final ConcurrentHashMap<BlobB, Either<String, ClassLoader>> cache;

  @Inject
  public JarClassLoaderFactory(BytecodeF bytecodeF) {
    this(bytecodeF, getSystemClassLoader());
  }

  public JarClassLoaderFactory(BytecodeF bytecodeF, ClassLoader parentClassLoader) {
    this.bytecodeF = bytecodeF;
    this.parentClassLoader = parentClassLoader;
    this.cache = new ConcurrentHashMap<>();
  }

  public Either<String, ClassLoader> classLoaderFor(BlobB jar) throws BytecodeException {
    return computeIfAbsent(cache, jar, this::newClassLoader);
  }

  private Either<String, ClassLoader> newClassLoader(BlobB jar) throws BytecodeException {
    return unzipBlob(bytecodeF, jar, s -> true)
        .mapRight(this::newClassLoader)
        .mapLeft(error -> "Error unpacking jar with native code: " + error);
  }

  private ClassLoader newClassLoader(ArrayB files) throws BytecodeException {
    var filesMap = files.elems(TupleB.class).toMap(f -> filePath(f).toJ(), x -> x);
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
