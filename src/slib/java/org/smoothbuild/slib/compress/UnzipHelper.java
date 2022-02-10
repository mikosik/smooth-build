package org.smoothbuild.slib.compress;

import static java.util.function.Function.identity;
import static org.smoothbuild.plugin.UnzipBlob.unzipBlob;
import static org.smoothbuild.run.eval.FileStruct.fileContent;
import static org.smoothbuild.run.eval.FileStruct.filePath;
import static org.smoothbuild.slib.java.UnjarFunc.JAR_MANIFEST_PATH;
import static org.smoothbuild.util.collect.Maps.toMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.util.io.DuplicateFileNameExc;
import org.smoothbuild.util.io.IllegalZipEntryFileNameExc;

import com.google.common.collect.ImmutableMap;

import net.lingala.zip4j.exception.ZipException;

public class UnzipHelper {
  private static final Predicate<String> NOT_MANIFEST_PREDICATE = f -> !f.equals(JAR_MANIFEST_PATH);

  public static Map<String, TupleB> filesFromLibJars(NativeApi nativeApi, ArrayB libJars)
      throws IOException {
    return filesFromLibJars(nativeApi, libJars, NOT_MANIFEST_PREDICATE);
  }

  public static HashMap<String, TupleB> filesFromLibJars(NativeApi nativeApi, ArrayB libJars,
      Predicate<String> filter) throws IOException {
    var result = new HashMap<String, TupleB>();
    var jars = libJars.elems(TupleB.class);
    for (int i = 0; i < jars.size(); i++) {
      var jarFile = jars.get(i);
      var classes = filesFromJar(nativeApi, jarFile, filter);
      if (classes == null) {
        return null;
      }
      for (var entry : classes.entrySet()) {
        var path = entry.getKey();
        if (result.put(path, entry.getValue()) != null) {
          nativeApi.log().error(
              "File " + path + " is contained by two different library jar files.");
          return null;
        }
      }
    }
    return result;
  }

  public static Map<String, TupleB> filesFromJar(NativeApi nativeApi, TupleB jarFile)
      throws IOException {
    return filesFromJar(nativeApi, jarFile, NOT_MANIFEST_PREDICATE);
  }

  private static ImmutableMap<String, TupleB> filesFromJar(NativeApi nativeApi,
      TupleB jarFile, Predicate<String> filter) throws IOException {
    var files = unzipToArrayB(nativeApi, fileContent(jarFile), filter);
    if (files == null) {
      return null;
    }
    return toMap(files.elems(TupleB.class), f -> filePath(f).toJ(), identity());
  }

  public static ArrayB unzipToArrayB(
      NativeApi nativeApi, BlobB blob, Predicate<String> includePredicate)
      throws IOException {
    try {
      return unzipBlob(nativeApi, blob, includePredicate);
    } catch (ZipException e) {
      nativeApi.log().error(
          "Cannot read archive. Corrupted data? Internal message: " + e.getMessage());
      return null;
    } catch (DuplicateFileNameExc e) {
      nativeApi.log().error("Archive contains two files with the same path = " + e.getMessage());
      return null;
    } catch (IllegalZipEntryFileNameExc e) {
      nativeApi.log().error(e.getMessage());
      return null;
    }
  }
}
