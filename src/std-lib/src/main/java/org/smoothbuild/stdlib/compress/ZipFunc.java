package org.smoothbuild.stdlib.compress;

import static okio.Okio.sink;
import static org.smoothbuild.run.eval.FileStruct.fileContent;
import static org.smoothbuild.run.eval.FileStruct.filePath;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import okio.BufferedSource;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.exc.IoBytecodeException;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class ZipFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    ArrayB files = (ArrayB) args.get(0);
    var duplicatesDetector = new HashSet<String>();
    try (var blobBuilder = nativeApi.factory().blobBuilder()) {
      try (var zipOutputStream = new ZipOutputStream(blobBuilder.sink().outputStream())) {
        for (TupleB file : files.elems(TupleB.class)) {
          String path = filePath(file).toJ();
          if (!duplicatesDetector.add(path)) {
            nativeApi.log().error("Cannot zip two files with the same path = " + path);
            return null;
          }
          addZipEntry(zipOutputStream, file);
        }
      } catch (IOException e) {
        throw new IoBytecodeException(e);
      }
      return blobBuilder.build();
    } catch (IOException e) {
      throw new IoBytecodeException(e);
    }
  }

  private static void addZipEntry(ZipOutputStream zipOutputStream, TupleB file)
      throws IOException, BytecodeException {
    var zipEntry = new ZipEntry(filePath(file).toJ());
    zipEntry.setLastModifiedTime(FileTime.fromMillis(0));
    zipOutputStream.putNextEntry(zipEntry);
    try (BufferedSource source = fileContent(file).source()) {
      source.readAll(sink(zipOutputStream));
    }
    zipOutputStream.closeEntry();
  }
}
