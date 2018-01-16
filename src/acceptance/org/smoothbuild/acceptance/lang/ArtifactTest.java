package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.containsString;
import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileArrayMatcher.isFileArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArtifactTest extends AcceptanceTestCase {

  @Test
  public void store_string_artifact() throws Exception {
    givenScript("result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void store_blob_artifact() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("result = file('//file.txt') | content;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void store_file_artifact() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("result = file('//file.txt');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void storing_function_with_underscore_in_name_converts_last_underscore_to_dot()
      throws Exception {
    givenFile("file.txt", "abc");
    givenScript("my_result_file_txt = content(file('//file.txt'));");
    whenSmoothBuild("my_result_file_txt");
    thenFinishedWithSuccess();
    then(artifact("my_result_file.txt"), hasContent("abc"));
  }

  @Test
  public void store_empty_array_of_strings_artifact() throws Exception {
    givenScript("[String] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void store_array_of_strings_artifact() throws Exception {
    givenScript("result = ['abc', 'def'];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith("abc", "def"));
  }

  @Test
  public void store_array_of_nothings_artifact() throws Exception {
    givenScript("result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void store_empty_array_of_blobs_artifact() throws Exception {
    givenScript("[Blob] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void store_array_of_blobs_artifact() throws Exception {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript("result = [content(file('//file1.txt')), content(file('//file2.txt'))];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith("abc", "def"));
  }

  @Test
  public void store_empty_array_of_files_artifact() throws Exception {
    givenScript("[File] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void store_array_of_files_artifact() throws Exception {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript("result = [file('//file1.txt'), file('//file2.txt')];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isFileArrayWith("file1.txt", "abc", "file2.txt", "def"));
  }

  @Test
  public void cannot_store_array_of_files_with_duplicated_paths() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("result = [file('//file.txt'), file('//file.txt')];");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "Can't store array of Files as it contains files with duplicated paths:\n"
            + "  file.txt\n"));
  }

  @Test
  public void info_about_stored_artifacts_is_printed_to_console() throws Exception {
    givenScript("result1 = 'abc';"
        + "      result2 = 'abc';"
        + "      result3 = 'abc';");
    whenSmoothBuild("result2 result3 result1");
    thenFinishedWithSuccess();
    then(output(), containsString("built artifact(s):\n"
        + "result1 -> '.smooth/artifacts/result1'\n"
        + "result2 -> '.smooth/artifacts/result2'\n"
        + "result3 -> '.smooth/artifacts/result3'\n"));
  }
}
