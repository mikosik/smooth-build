package org.smoothbuild.common.testing;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import org.smoothbuild.common.collect.Maybe;

public class GoldenFilesTestCase {
  private final Path testRootDir;
  private final String testName;
  private final StringBuilder fetchedContent;

  public GoldenFilesTestCase(Path testRootDir, String testName) {
    this.testRootDir = testRootDir;
    this.testName = testName;
    this.fetchedContent = new StringBuilder();
  }

  @Override
  public String toString() {
    return testName;
  }

  public String readFile(String fileName) throws IOException {
    var content = Files.readString(testRootDir.resolve(fileName));
    saveFetchedFileContent(fileName, content);
    return content;
  }

  private void saveFetchedFileContent(String fileName, String content) {
    fetchedContent.append("==== file '%s' ====\n".formatted(fileName));
    fetchedContent.append(content);
    fetchedContent.append("\n");
  }

  public Maybe<String> readFileMaybe(String fileName) throws IOException {
    var path = testRootDir.resolve(fileName);
    Maybe<String> maybe = Files.exists(path) ? some(Files.readString(path)) : none();
    saveFetchedFileContent(fileName, maybe);
    return maybe;
  }

  private void saveFetchedFileContent(String fileName, Maybe<String> maybeContent) {
    if (maybeContent.isNone()) {
      fetchedContent.append("==== No golden file for '%s' ====\n".formatted(fileName));
    } else {
      saveFetchedFileContent(fileName, maybeContent.get());
    }
  }

  public void assertWithGoldenFiles(Map<String, String> actualFiles) throws IOException {
    var inputs = """
        ==== test dir ====
        %s
        %s
        """
        .formatted(testName, fetchedContent.toString());
    for (Entry<String, String> entry : actualFiles.entrySet()) {
      var fileName = entry.getKey();
      var actualContent = entry.getValue();
      assertWithMessage(inputs).that(actualContent).isEqualTo(readFile(fileName));
    }
  }

  public void overwriteGoldenFiles(Map<String, String> actualFiles) throws IOException {
    for (Entry<String, String> entry : actualFiles.entrySet()) {
      var fileName = entry.getKey();
      var content = entry.getValue();
      Files.writeString(testRootDir.resolve(fileName), content);
    }
  }
}
