package org.smoothbuild.util.io;

import static org.smoothbuild.io.fs.base.PathS.detectPathError;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.function.Predicate;

import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.util.function.ThrowingBiConsumer;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;

public class Unzip {
  public static void unzip(BlobB blob, Predicate<String> includePredicate,
      ThrowingBiConsumer<String, InputStream, IOException> entryConsumer)
      throws IOException, ZipException, IllegalZipEntryFileNameExc, DuplicateFileNameExc {
    HashSet<String> fileNames = new HashSet<>();
    try (var s = blob.source(); var zipInputStream = new ZipInputStream(s.inputStream())) {
      LocalFileHeader header;
      while ((header = zipInputStream.getNextEntry()) != null) {
        var fileName = header.getFileName();
        if (!fileName.endsWith("/") && includePredicate.test(fileName)) {
          throwExcIfIllegalFileName(fileName);
          if (!fileNames.add(fileName)) {
            throw new DuplicateFileNameExc(
                "Archive contains more than one file with name '" + fileName + "'.");
          }
          entryConsumer.accept(fileName, zipInputStream);
        }
      }
    }
  }

  private static void throwExcIfIllegalFileName(String fileName) throws IllegalZipEntryFileNameExc {
    String pathError = detectPathError(fileName);
    if (pathError != null) {
      throw new IllegalZipEntryFileNameExc(
          "File in archive has illegal name = '" + fileName + "'. " + pathError);
    }
  }
}
