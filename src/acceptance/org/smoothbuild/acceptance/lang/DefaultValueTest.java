package org.smoothbuild.acceptance.lang;

import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class DefaultValueTest extends AcceptanceTestCase {
  @Test
  public void default_value_for_string_is_empty_string() throws Exception {
    givenBuildScript(script("result: stringIdentity();"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent(""));
  }

  @Test
  public void default_value_for_blob_is_empty_stream() throws Exception {
    givenBuildScript(script("result: blobIdentity();"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent(""));
  }

  @Test
  public void default_value_for_file_has_empty_path() throws Exception {
    givenBuildScript(script("result: fileIdentity() | path;"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("."));
  }

  @Test
  public void default_value_for_file_has_empty_content() throws Exception {
    givenBuildScript(script("result: fileIdentity() | content;"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent(""));
  }

  @Test
  public void default_value_for_array_is_empty_array() throws Exception {
    givenBuildScript(script("result: stringArrayIdentity();"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith());
  }
}
