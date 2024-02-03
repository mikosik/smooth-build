package org.smoothbuild.common.io;

import static org.smoothbuild.common.filesystem.base.PathS.detectPathError;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.function.Predicate;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;
import okio.BufferedSource;
import org.smoothbuild.common.function.Consumer2;

public class Unzip {
  public static void unzip(
      BufferedSource source,
      Predicate<String> includePredicate,
      Consumer2<String, InputStream, IOException> entryConsumer)
      throws IOException, ZipException, IllegalZipEntryFileNameException,
          DuplicateFileNameException {
    HashSet<String> fileNames = new HashSet<>();
    try (var zipInputStream = new ZipInputStream(source.inputStream())) {
      LocalFileHeader header;
      while ((header = zipInputStream.getNextEntry()) != null) {
        var fileName = header.getFileName();
        if (!fileName.endsWith("/") && includePredicate.test(fileName)) {
          throwExcIfIllegalFileName(fileName);
          if (!fileNames.add(fileName)) {
            throw new DuplicateFileNameException(
                "Archive contains more than one file with name '" + fileName + "'.");
          }
          entryConsumer.accept(fileName, zipInputStream);
        }
      }
    }
  }

  private static void throwExcIfIllegalFileName(String fileName)
      throws IllegalZipEntryFileNameException {
    String pathError = detectPathError(fileName);
    if (pathError != null) {
      throw new IllegalZipEntryFileNameException(
          "File in archive has illegal name = '" + fileName + "'. " + pathError);
    }
  }
}
