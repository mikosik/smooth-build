package org.smoothbuild.vm.java;

import static java.util.function.Function.identity;
import static org.smoothbuild.eval.artifact.FileStruct.fileContent;
import static org.smoothbuild.eval.artifact.FileStruct.filePath;
import static org.smoothbuild.plugin.UnzipBlob.unzipBlob;
import static org.smoothbuild.util.collect.Maps.toMap;
import static org.smoothbuild.util.reflect.ClassLoaders.mapClassLoader;

import java.io.IOException;

import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.util.io.DuplicateFileNameExc;
import org.smoothbuild.util.io.IllegalZipEntryFileNameExc;

import com.google.common.collect.ImmutableMap;

import net.lingala.zip4j.exception.ZipException;

public class ClassLoaderProv {
  private final ClassLoader parentClassLoader;
  private final NativeApi nativeApi;

  public ClassLoaderProv(ClassLoader parentClassLoader, NativeApi nativeApi) {
    this.parentClassLoader = parentClassLoader;
    this.nativeApi = nativeApi;
  }

  public ClassLoader classLoaderForJar(BlobB jar) throws ClassLoaderProvExc, IOException {
    try {
      var files = unzipBlob(nativeApi, jar, s -> true);
      var filesMap = toMap(files.elems(TupleB.class), f -> filePath(f).toJ(), identity());
      return classLoader(filesMap);
    } catch (DuplicateFileNameExc | IllegalZipEntryFileNameExc | ZipException e) {
      throw new ClassLoaderProvExc("Error unpacking jar with native code: " + e.getMessage());
    }
  }

  private ClassLoader classLoader(ImmutableMap<String, TupleB> filesMap) {
    return mapClassLoader(parentClassLoader, path -> {
      TupleB file = filesMap.get(path);
      return file == null ? null : fileContent(file).source().inputStream();
    });
  }
}
