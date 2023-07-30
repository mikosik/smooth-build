package org.smoothbuild.vm.bytecode.load;

import static java.lang.ClassLoader.getSystemClassLoader;
import static java.util.function.Function.identity;
import static org.smoothbuild.common.collect.Maps.computeIfAbsent;
import static org.smoothbuild.common.collect.Maps.toMap;
import static org.smoothbuild.common.reflect.ClassLoaders.mapClassLoader;
import static org.smoothbuild.run.eval.FileStruct.fileContent;
import static org.smoothbuild.run.eval.FileStruct.filePath;
import static org.smoothbuild.vm.evaluate.plugin.UnzipBlob.unzipBlob;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.smoothbuild.common.collect.Try;
import org.smoothbuild.common.io.DuplicateFileNameExc;
import org.smoothbuild.common.io.IllegalZipEntryFileNameExc;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.lingala.zip4j.exception.ZipException;

/**
 * This class is thread-safe.
 */
@Singleton
public class JarClassLoaderProv {
  private final BytecodeF bytecodeF;
  private final ClassLoader parentClassLoader;
  private final ConcurrentHashMap<BlobB, Try<ClassLoader>> cache;

  @Inject
  public JarClassLoaderProv(BytecodeF bytecodeF) {
    this(bytecodeF, getSystemClassLoader());
  }

  public JarClassLoaderProv(BytecodeF bytecodeF, ClassLoader parentClassLoader) {
    this.bytecodeF = bytecodeF;
    this.parentClassLoader = parentClassLoader;
    this.cache = new ConcurrentHashMap<>();
  }

  public Try<ClassLoader> classLoaderFor(BlobB jar) throws IOException {
    return computeIfAbsent(cache, jar, j -> newClassLoader(parentClassLoader, j));
  }

  private Try<ClassLoader> newClassLoader(ClassLoader parentClassLoader, BlobB jar)
      throws IOException {
    try {
      var files = unzipBlob(bytecodeF, jar, s -> true);
      var filesMap = toMap(files.elems(TupleB.class), f -> filePath(f).toJ(), identity());
      return Try.result(classLoader(parentClassLoader, filesMap));
    } catch (DuplicateFileNameExc | IllegalZipEntryFileNameExc | ZipException e) {
      return Try.error("Error unpacking jar with native code: " + e.getMessage());
    }
  }

  private ClassLoader classLoader(ClassLoader parentClassLoader, Map<String, TupleB> filesMap) {
    return mapClassLoader(parentClassLoader, path -> {
      TupleB file = filesMap.get(path);
      return file == null ? null : fileContent(file).source().inputStream();
    });
  }
}
