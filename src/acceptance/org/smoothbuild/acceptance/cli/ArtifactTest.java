package org.smoothbuild.acceptance.cli;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.BooleanCreators.falseByteString;
import static org.smoothbuild.testing.BooleanCreators.trueByteString;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArtifactTest extends AcceptanceTestCase {
  @Test
  public void store_bool_artifact() throws Exception {
    givenScript(
        "  result = true();  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactAsByteStrings("result"))
        .isEqualTo(trueByteString());
  }

  @Test
  public void store_string_artifact() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactContent("result"))
        .isEqualTo("abc");
  }

  @Test
  public void store_blob_artifact() throws Exception {
    givenScript(
        "  result = toBlob('abc');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactContent("result"))
        .isEqualTo("abc");
  }

  @Test
  public void store_file_artifact() throws Exception {
    givenScript(
        "  result = file(toBlob('abc'), 'file.txt');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenSysOutContains("result -> '.smooth/artifacts/result/file.txt'");
    assertThat(artifactDir("result"))
        .containsExactly("file.txt", "abc");
  }

  @Test
  public void store_empty_array_of_bools_artifact() throws Exception {
    givenScript(
        "  [Bool] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactArray("result"))
        .isEqualTo(list());
  }

  @Test
  public void store_array_of_bools_artifact() throws Exception {
    givenScript(
        "  result = [ true(), false() ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactAsByteStrings("result"))
        .isEqualTo(list(trueByteString(), falseByteString()));
  }

  @Test
  public void store_empty_array_of_strings_artifact() throws Exception {
    givenScript(
        "  [String] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactArray("result"))
        .isEqualTo(list());
  }

  @Test
  public void store_array_of_strings_artifact() throws Exception {
    givenScript(
        "  result = [ 'abc', 'def' ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactArray("result"))
        .isEqualTo(list("abc", "def"));
  }

  @Test
  public void store_empty_array_of_nothings_artifact() throws Exception {
    givenScript(
        "  result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactArray("result"))
        .isEqualTo(list());
  }

  @Test
  public void store_empty_array_of_blobs_artifact() throws Exception {
    givenScript(
        "  [Blob] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactArray("result"))
        .isEqualTo(list());
  }

  @Test
  public void store_array_of_blobs_artifact() throws Exception {
    givenScript(
        "  result = [ toBlob('abc'), toBlob('def') ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactArray("result"))
        .isEqualTo(list("abc", "def"));
  }

  @Test
  public void store_empty_array_of_files_artifact() throws Exception {
    givenScript(
        "  [File] result = [];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactArray("result"))
        .isEqualTo(list());
  }

  @Test
  public void store_array_of_files_artifact() throws Exception {
    givenScript(
        "  result = [ file(toBlob('abc'), 'file1.txt'), file(toBlob('def'), 'file2.txt') ];  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactDir("result"))
        .containsExactly("file1.txt", "abc", "file2.txt", "def");
  }

  @Test
  public void cannot_store_array_of_files_with_duplicated_paths() throws Exception {
    givenScript(
        "  myFile = file(toBlob('abc'), 'file.txt');  ",
        "  result = [ myFile, myFile ];               ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContains(
        "Saving artifact(s)",
        "  result -> ???",
        "   + ERROR: Can't store array of Files as it contains files with duplicated paths:",
        "       'file.txt'",
        "");
    assertThat(artifact("result").exists())
        .isFalse();
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
    thenSysOutContains(
        "Saving artifact(s)",
        "  result1 -> '.smooth/artifacts/result1'",
        "  result2 -> '.smooth/artifacts/result2'",
        "  result3 -> '.smooth/artifacts/result3'",
        "");
  }
}
