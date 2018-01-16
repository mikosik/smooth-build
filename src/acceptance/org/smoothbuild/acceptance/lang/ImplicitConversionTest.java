package org.smoothbuild.acceptance.lang;

import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.lang.nativ.ReportError;

public class ImplicitConversionTest extends AcceptanceTestCase {
  @Test
  public void nothing_can_be_assigned_to_nothing_without_compiler_error() throws IOException {
    givenNativeJar(ReportError.class);
    givenScript("Nothing reportError(String message);"
        + "      String ignoreNothing(Nothing ignore) = 'ignore-nothing-message';"
        + "      result = ignoreNothing(reportError('ignored-error'));");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("ignore-nothing-message"));
  }

  @Test
  public void nothing_can_be_assigned_to_string_without_compiler_error() throws IOException {
    givenNativeJar(ReportError.class);
    givenScript("Nothing reportError(String message);"
        + "      String ignoreString(String ignore) = 'ignore-string-message';"
        + "      result = ignoreString(reportError('ignored-error'));");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("ignore-string-message"));
  }

  @Test
  public void nothing_can_be_assigned_to_blob_without_compiler_error() throws IOException {
    givenNativeJar(ReportError.class);
    givenScript("Nothing reportError(String message);"
        + "      String ignoreBlob(Blob ignore) = 'ignore-blob-message';"
        + "      result = ignoreBlob(reportError('ignored-error'));");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("ignore-blob-message"));
  }

  @Test
  public void nothing_can_be_assigned_to_file_without_compiler_error() throws IOException {
    givenNativeJar(ReportError.class);
    givenScript("Nothing reportError(String message);"
        + "      String ignoreFile(File ignore) = 'ignore-file-message';"
        + "      result = ignoreFile(reportError('ignored-error'));");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("ignore-file-message"));
  }

  @Test
  public void nothing_can_be_assigned_to_nothing_array_without_compiler_error() throws IOException {
    givenNativeJar(ReportError.class);
    givenScript("Nothing reportError(String message);"
        + "      String ignoreNothingArray([Nothing] ignore) = 'ignore-nothing-array-message';"
        + "      result = ignoreNothingArray(reportError('ignored-error'));");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("ignore-nothing-array-message"));
  }

  @Test
  public void nothing_can_be_assigned_to_string_array_without_compiler_error() throws IOException {
    givenNativeJar(ReportError.class);
    givenScript("Nothing reportError(String message);"
        + "      String ignoreStringArray([String] ignore) = 'ignore-string-array-message';"
        + "      result = ignoreStringArray(reportError('ignored-error'));");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("ignore-string-array-message"));
  }

  @Test
  public void file_is_implicitly_converted_to_blob() throws IOException {
    givenFile("file.txt", "abc");
    givenScript("File result = file('//file.txt');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void file_array_is_implicitly_converted_to_blob_array() throws IOException {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript("[Blob] result = [file('//file1.txt'), file('//file2.txt')];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith("abc", "def"));
  }

  @Test
  public void empty_array_is_implicitly_converted_to_string_array() throws IOException {
    givenScript("[String] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void empty_array_is_implicitly_converted_to_blob_array() throws IOException {
    givenScript("[Blob] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void empty_array_is_implicitly_converted_to_file_array() throws IOException {
    givenScript("[File] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }
}
