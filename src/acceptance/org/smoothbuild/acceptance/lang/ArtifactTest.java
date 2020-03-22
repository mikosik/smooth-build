package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.equalTo;
import static org.smoothbuild.acceptance.FileArrayMatcher.isFileArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.smoothbuild.testing.BooleanCreators.falseByteString;
import static org.smoothbuild.testing.BooleanCreators.trueByteString;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.then;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArtifactTest extends AcceptanceTestCase {
  @Test
  public void store_bool_artifact() throws Exception {
    givenScript(
        "  result = true();  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsByteStrings("result"), equalTo(trueByteString()));
  }

  @Test
  public void store_string_artifact() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void store_blob_artifact() throws Exception {
    givenScript(
        "  result = toBlob('abc');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void store_file_artifact() throws Exception {
    givenScript(
        "  result = file(toBlob('abc'), 'file.txt');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void storing_function_with_underscore_in_name_converts_last_underscore_to_dot()
      throws Exception {
    givenScript(
        "  my_result_file_txt = toBlob('abc');  ");
    whenSmoothBuild("my_result_file_txt");
    thenFinishedWithSuccess();
    then(artifact("my_result_file.txt"), hasContent("abc"));
  }

  @Test
  public void store_empty_array_of_bools_artifact() throws Exception {
    givenScript(
        "  [Bool] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list()));
  }

  @Test
  public void store_array_of_bools_artifact() throws Exception {
    givenScript(
        "  result = [ true(), false() ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsByteStrings("result"), equalTo(list(trueByteString(), falseByteString())));
  }

  @Test
  public void store_empty_array_of_strings_artifact() throws Exception {
    givenScript(
        "  [String] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list()));
  }

  @Test
  public void store_array_of_strings_artifact() throws Exception {
    givenScript(
        "  result = [ 'abc', 'def' ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("abc", "def")));
  }

  @Test
  public void store_array_of_nothings_artifact() throws Exception {
    givenScript(
        "  result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list()));
  }

  @Test
  public void store_empty_array_of_blobs_artifact() throws Exception {
    givenScript(
        "  [Blob] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list()));
  }

  @Test
  public void store_array_of_blobs_artifact() throws Exception {
    givenScript(
        "  result = [ toBlob('abc'), toBlob('def') ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list("abc", "def")));
  }

  @Test
  public void store_empty_array_of_files_artifact() throws Exception {
    givenScript(
        "  [File] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactArray("result"), equalTo(list()));
  }

  @Test
  public void store_array_of_files_artifact() throws Exception {
    givenScript(
        "  result = [ file(toBlob('abc'), 'file1.txt'), file(toBlob('def'), 'file2.txt') ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isFileArrayWith("file1.txt", "abc", "file2.txt", "def"));
  }

  @Test
  public void cannot_store_array_of_files_with_duplicated_paths() throws Exception {
    givenScript(
        "  myFile = file(toBlob('abc'), 'file.txt');  ",
        "  result = [ myFile, myFile ];               ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains(
        "Can't store array of Files as it contains files with duplicated paths:",
        "  file.txt",
        "");
  }

  @Test
  public void info_about_stored_artifacts_is_printed_to_console_in_alphabetical_order()
      throws Exception {
    givenScript(
        "  result1 = 'abc';  ",
        "  result2 = 'abc';  ",
        "  result3 = 'abc';  ");
    whenSmoothBuild("result2 result3 result1");
    thenFinishedWithSuccess();
    thenOutputContains(
        "built artifact(s):",
        "result1 -> '.smooth/artifacts/result1'",
        "result2 -> '.smooth/artifacts/result2'",
        "result3 -> '.smooth/artifacts/result3'",
        "");
  }
}
