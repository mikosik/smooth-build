package org.smoothbuild.acceptance.cli;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.BooleanCreators.falseByteString;
import static org.smoothbuild.testing.BooleanCreators.trueByteString;
import static org.smoothbuild.util.Lists.list;

import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArtifactTest extends AcceptanceTestCase {
  @Test
  public void store_bool_artifact() throws Exception {
    createUserModule(
        "  result = true();  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactAsByteStrings("result"))
        .isEqualTo(trueByteString());
  }

  @Test
  public void store_string_artifact() throws Exception {
    createUserModule(
        "  result = 'abc';  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void store_blob_artifact() throws Exception {
    createUserModule(
        "  result = toBlob('abc');  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactFileContentAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void store_file_artifact() throws Exception {
    createUserModule(
        "  result = file(toBlob('abc'), 'file.txt');  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result/file.txt'");
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("file.txt", "abc");
  }

  @Test
  public void store_empty_array_of_bools_artifact() throws Exception {
    createUserModule(
        "  [Bool] result = [];  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list());
  }

  @Test
  public void store_array_of_bools_artifact() throws Exception {
    createUserModule(
        "  result = [ true(), false() ];  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactAsByteStrings("result"))
        .isEqualTo(list(trueByteString(), falseByteString()));
  }

  @Test
  public void store_empty_array_of_strings_artifact() throws Exception {
    createUserModule(
        "  [String] result = [];  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list());
  }

  @Test
  public void store_array_of_strings_artifact() throws Exception {
    createUserModule(
        "  result = [ 'abc', 'def' ];  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list("abc", "def"));
  }

  @Test
  public void store_empty_array_of_nothings_artifact() throws Exception {
    createUserModule(
        "  result = [];  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list());
  }

  @Test
  public void store_empty_array_of_blobs_artifact() throws Exception {
    createUserModule(
        "  [Blob] result = [];  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list());
  }

  @Test
  public void store_array_of_blobs_artifact() throws Exception {
    createUserModule(
        "  result = [ toBlob('abc'), toBlob('def') ];  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list("abc", "def"));
  }

  @Test
  public void store_empty_array_of_files_artifact() throws Exception {
    createUserModule(
        "  [File] result = [];  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(stringifiedArtifact("result"))
        .isEqualTo(list());
  }

  @Test
  public void store_array_of_files_artifact() throws Exception {
    createUserModule(
        "  result = [ file(toBlob('abc'), 'file1.txt'), file(toBlob('def'), 'file2.txt') ];  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("file1.txt", "abc", "file2.txt", "def");
  }

  @Test
  public void cannot_store_array_of_files_with_duplicated_paths() throws Exception {
    createUserModule(
        "  myFile = file(toBlob('abc'), 'file.txt');  ",
        "  result = [ myFile, myFile ];               ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(
        "Saving artifact(s)",
        "  result -> ???",
        "   + ERROR: Can't store array of Files as it contains files with duplicated paths:",
        "       'file.txt'",
        "");
    assertThat(Files.exists(artifactAbsolutePath("result")))
        .isFalse();
  }

  @Test
  public void info_about_stored_artifacts_is_printed_to_console_in_alphabetical_order()
      throws Exception {
    createUserModule(
        "  result1 = 'abc';  ",
        "  result2 = 'abc';  ",
        "  result3 = 'abc';  ");
    runSmoothBuild("result2", "result3", "result1");
    assertFinishedWithSuccess();
    assertSysOutContains(
        "Saving artifact(s)",
        "  result1 -> '.smooth/artifacts/result1'",
        "  result2 -> '.smooth/artifacts/result2'",
        "  result3 -> '.smooth/artifacts/result3'",
        "");
  }
}
