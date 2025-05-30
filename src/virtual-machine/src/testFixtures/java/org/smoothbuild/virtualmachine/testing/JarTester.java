package org.smoothbuild.virtualmachine.testing;

import static okio.Okio.buffer;
import static okio.Okio.sink;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.fileContent;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;

import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import okio.Buffer;
import okio.ByteString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class JarTester {
  public static BBlob jar(BTuple... files) throws Exception {
    return new VmTestContext().bBlob(jarByteString(files));
  }

  public static ByteString jarByteString(BTuple... files) throws Exception {
    Buffer buffer = new Buffer();
    try (JarOutputStream jarOutputStream = new JarOutputStream(buffer.outputStream())) {
      for (BTuple file : files) {
        addEntry(jarOutputStream, file);
      }
    }

    return buffer.readByteString();
  }

  private static void addEntry(JarOutputStream jarOutputStream, BTuple file) throws Exception {
    JarEntry entry = new JarEntry(filePath(file).toJavaString());
    jarOutputStream.putNextEntry(entry);
    try (var source = buffer(fileContent(file).source())) {
      var sink = sink(jarOutputStream);
      source.readAll(sink);
      sink.flush();
    }
    jarOutputStream.closeEntry();
  }
}
