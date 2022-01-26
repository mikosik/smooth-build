package org.smoothbuild.acceptance.cli;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.BooleanCreators.falseByteString;
import static org.smoothbuild.testing.BooleanCreators.trueByteString;
import static org.smoothbuild.testing.IntCreators.intToByteString;
import static org.smoothbuild.util.collect.Lists.list;

import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArtifactTest extends AcceptanceTestCase {
  @Test
  public void store_bool_artifact() throws Exception {
    createUserModule("""
            result = true;
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactAsByteStrings("result"))
        .isEqualTo(trueByteString());
  }

  @Test
  public void store_int_artifact() throws Exception {
    String code = """
        result = -12345678;
        """;
    createUserModule(code);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactAsByteStrings("result"))
        .isEqualTo(intToByteString(-12345678));
  }

  @Test
  public void store_string_artifact() throws Exception {
    createUserModule("""
            result = "abc";
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactAsString("result"))
        .isEqualTo("abc");
  }

  @Test
  public void store_blob_artifact() throws Exception {
    createUserModule("""
            result = 0x41;
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactAsString("result"))
        .isEqualTo("A");
  }

  @Test
  public void store_file_artifact() throws Exception {
    createUserModule("""
            result = file(0x41, "file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result/file.txt'");
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("file.txt", "A");
  }

  @Test
  public void storing_struct_with_same_fields_as_file_is_not_using_path_as_artifact_name()
      throws Exception {
    createUserModule("""
            NotAFile {
              Blob content,
              String path,
            }
            
            result = notAFile(0x41, "file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
  }

  @Test
  public void store_empty_array_of_bools_artifact() throws Exception {
    createUserModule("""
            [Bool] result = [];
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactStringified("result"))
        .isEqualTo(list());
  }

  @Test
  public void store_array_of_bools_artifact() throws Exception {
    createUserModule("""
            result = [ true, false ];
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactAsByteStrings("result"))
        .isEqualTo(list(trueByteString(), falseByteString()));
  }

  @Test
  public void store_empty_array_of_strings_artifact() throws Exception {
    createUserModule("""
            [String] result = [];
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactStringified("result"))
        .isEqualTo(list());
  }

  @Test
  public void store_array_of_strings_artifact() throws Exception {
    createUserModule("""
            result = [ "abc", "def" ];
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactStringified("result"))
        .isEqualTo(list("abc", "def"));
  }

  @Test
  public void store_empty_array_of_nothings_artifact() throws Exception {
    createUserModule("""
            result = [];
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactStringified("result"))
        .isEqualTo(list());
  }

  @Test
  public void store_empty_array_of_blobs_artifact() throws Exception {
    createUserModule("""
            [Blob] result = [];
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactStringified("result"))
        .isEqualTo(list());
  }

  @Test
  public void store_array_of_blobs_artifact() throws Exception {
    createUserModule("""
            result = [ 0x41, 0x42 ];
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactStringified("result"))
        .isEqualTo(list("A", "B"));
  }

  @Test
  public void store_empty_array_of_files_artifact() throws Exception {
    createUserModule("""
            [File] result = [];
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactStringified("result"))
        .isEqualTo(list());
  }

  @Test
  public void store_array_of_files_artifact() throws Exception {
    createUserModule("""
            result = [ file(0x41, "file1.txt"), file(0x42, "file2.txt") ];
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("result -> '.smooth/artifacts/result'");
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("file1.txt", "A", "file2.txt", "B");
  }

  @Test
  public void cannot_store_array_of_files_with_duplicated_paths() throws Exception {
    createUserModule("""
            myFile = file(0x41, "file.txt");
            result = [ myFile, myFile ];
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("""
        Saving artifact(s)
          result -> ???
           + ERROR: Can't store array of Files as it contains files with duplicated paths:
               'file.txt'
            """);
    assertThat(Files.exists(artifactAbsolutePath("result")))
        .isFalse();
  }

  @Test
  public void info_about_stored_artifacts_is_printed_to_console_in_alphabetical_order()
      throws Exception {
    createUserModule("""
        result1 = "abc";
        result2 = "abc";
        result3 = "abc";
            """);
    runSmoothBuild("result2", "result3", "result1");
    assertFinishedWithSuccess();
    assertSysOutContains("""
        Saving artifact(s)
          result1 -> '.smooth/artifacts/result1'
          result2 -> '.smooth/artifacts/result2'
          result3 -> '.smooth/artifacts/result3'
            """);
  }
}
