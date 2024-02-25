package org.smoothbuild.common.io;

import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.filesystem.base.PathS.detectPathError;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.function.Predicate;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;
import okio.BufferedSource;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.function.Consumer2;

public class Unzip {
  public static <T extends Throwable> Maybe<String> unzip(
      BufferedSource source,
      Predicate<String> includePredicate,
      Consumer2<String, InputStream, T> entryConsumer)
      throws IOException, T {
    HashSet<String> fileNames = new HashSet<>();
    try (var zipInputStream = new ZipInputStream(source.inputStream())) {
      LocalFileHeader header;
      while ((header = zipInputStream.getNextEntry()) != null) {
        var fileName = header.getFileName();
        if (!fileName.endsWith("/") && includePredicate.test(fileName)) {
          String pathError = detectPathError(fileName);
          if (pathError != null) {
            return maybe("File in archive has illegal name = '" + fileName + "'. " + pathError);
          }
          if (!fileNames.add(fileName)) {
            var message = "Archive contains more than one file with name '" + fileName + "'.";
            return maybe(message);
          }
          entryConsumer.accept(fileName, zipInputStream);
        }
      }
    } catch (ZipException e) {
      return maybe("Cannot read archive. Corrupted data? Internal message: " + e.getMessage());
    }
    return none();
  }
}
