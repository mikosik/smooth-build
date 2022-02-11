package org.smoothbuild.vm.java;

import static java.lang.ClassLoader.getSystemClassLoader;
import static java.util.function.Function.identity;
import static org.smoothbuild.plugin.UnzipBlob.unzipBlob;
import static org.smoothbuild.run.eval.FileStruct.fileContent;
import static org.smoothbuild.run.eval.FileStruct.filePath;
import static org.smoothbuild.util.collect.Maps.toMap;
import static org.smoothbuild.util.reflect.ClassLoaders.mapClassLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.util.io.DuplicateFileNameExc;
import org.smoothbuild.util.io.IllegalZipEntryFileNameExc;

import net.lingala.zip4j.exception.ZipException;

/**
 * This class is thread-safe.
 */
@Singleton
public class ClassLoaderProv {
  private final BytecodeF bytecodeF;
  private final HashMap<BlobB, ClassLoader> classLoaderCache;
  private final ClassLoader parentClassLoader;

  @Inject
  public ClassLoaderProv(BytecodeF bytecodeF) {
    this(bytecodeF, getSystemClassLoader());
  }

  public ClassLoaderProv(BytecodeF bytecodeF, ClassLoader parentClassLoader) {
    this.bytecodeF = bytecodeF;
    this.classLoaderCache = new HashMap<>();
    this.parentClassLoader = parentClassLoader;
  }

  public synchronized ClassLoader classLoaderFor(BlobB jar) throws ClassLoaderProvExc, IOException {
    var classLoader = classLoaderCache.get(jar);
    if (classLoader == null) {
      classLoader = newClassLoader(parentClassLoader, jar);
      classLoaderCache.put(jar, classLoader);
    }
    return classLoader;
  }

  private ClassLoader newClassLoader(ClassLoader parentClassLoader, BlobB jar)
      throws IOException, ClassLoaderProvExc {
    try {
      var files = unzipBlob(bytecodeF, jar, s -> true);
      var filesMap = toMap(files.elems(TupleB.class), f -> filePath(f).toJ(), identity());
      return classLoader(parentClassLoader, filesMap);
    } catch (DuplicateFileNameExc | IllegalZipEntryFileNameExc | ZipException e) {
      throw new ClassLoaderProvExc("Error unpacking jar with native code: " + e.getMessage());
    }
  }

  private ClassLoader classLoader(ClassLoader parentClassLoader, Map<String, TupleB> filesMap) {
    return mapClassLoader(parentClassLoader, path -> {
      TupleB file = filesMap.get(path);
      return file == null ? null : fileContent(file).source().inputStream();
    });
  }
}
