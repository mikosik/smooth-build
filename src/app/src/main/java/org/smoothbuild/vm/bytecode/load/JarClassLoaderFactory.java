package org.smoothbuild.vm.bytecode.load;

import static java.lang.ClassLoader.getSystemClassLoader;
import static org.smoothbuild.common.collect.Either.left;
import static org.smoothbuild.common.collect.Either.right;
import static org.smoothbuild.common.collect.Maps.computeIfAbsent;
import static org.smoothbuild.common.reflect.ClassLoaders.mapClassLoader;
import static org.smoothbuild.run.eval.FileStruct.fileContent;
import static org.smoothbuild.run.eval.FileStruct.filePath;
import static org.smoothbuild.vm.evaluate.plugin.UnzipBlob.unzipBlob;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import net.lingala.zip4j.exception.ZipException;
import org.smoothbuild.common.collect.Either;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.io.DuplicateFileNameException;
import org.smoothbuild.common.io.IllegalZipEntryFileNameException;
import org.smoothbuild.vm.bytecode.BytecodeF;
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

  public Either<String, ClassLoader> classLoaderFor(BlobB jar) throws IOException {
    return computeIfAbsent(cache, jar, j -> newClassLoader(parentClassLoader, j));
  }

  private Either<String, ClassLoader> newClassLoader(ClassLoader parentClassLoader, BlobB jar)
      throws IOException {
    try {
      var files = unzipBlob(bytecodeF, jar, s -> true);
      var filesMap = files.elems(TupleB.class).toMap(f -> filePath(f).toJ(), x -> x);
      return right(classLoader(parentClassLoader, filesMap));
    } catch (DuplicateFileNameException | IllegalZipEntryFileNameException | ZipException e) {
      return left("Error unpacking jar with native code: " + e.getMessage());
    }
  }

  private ClassLoader classLoader(ClassLoader parentClassLoader, Map<String, TupleB> filesMap) {
    return mapClassLoader(parentClassLoader, path -> {
      TupleB file = filesMap.get(path);
      return file == null ? null : fileContent(file).source().inputStream();
    });
  }
}
