package org.smoothbuild.virtualmachine.testing;

import static okio.Okio.sink;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.fileContent;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;

import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;

public class JarTester {
  public static BBlob jar(BTuple... files) throws Exception {
    return new TestingVirtualMachine().bBlob(jarByteString(files));
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
    try (BufferedSource source = fileContent(file).source()) {
      var sink = sink(jarOutputStream);
      source.readAll(sink);
      sink.flush();
    }
    jarOutputStream.closeEntry();
  }
}
