package org.smoothbuild.acceptance.lang;

import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ImplicitConversionTest extends AcceptanceTestCase {
  @Test
  public void file_is_implicitly_converted_to_blob() throws IOException {
    givenFile("file.txt", "abc");
    givenScript("result = file('//file.txt') | toString;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void file_array_is_implicitly_converted_to_blob_array() throws IOException {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript(
        "result = concatenateBlobArrays([file('//file1.txt')], with=[file('//file2.txt')]);");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith("abc", "def"));
  }

  @Test
  public void nil_is_implicitly_converted_to_string_array() throws IOException {
    givenScript("result = concatenateStringArrays([], with=[]);");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void nil_is_implicitly_converted_to_blob_array() throws IOException {
    givenScript("result = concatenateBlobArrays([], with=[]);");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void nil_is_implicitly_converted_to_file_array() throws IOException {
    givenScript("result = concatenateFileArrays([], with=[]);");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }
}
