package org.smoothbuild.acceptance.lang;

import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class DefaultValueTest extends AcceptanceTestCase {
  @Test
  public void default_value_for_string_is_empty_string() throws Exception {
    givenScript("result: stringIdentity();");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), hasContent(""));
  }

  @Test
  public void default_value_for_blob_is_empty_stream() throws Exception {
    givenScript("result: blobIdentity();");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), hasContent(""));
  }

  @Test
  public void default_value_for_file_has_empty_path() throws Exception {
    givenScript("result: fileIdentity() | path;");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), hasContent("."));
  }

  @Test
  public void default_value_for_file_has_empty_content() throws Exception {
    givenScript("result: fileIdentity() | content;");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), hasContent(""));
  }

  @Test
  public void default_value_for_array_is_empty_array() throws Exception {
    givenScript("result: stringArrayIdentity();");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), isArrayWith());
  }
}
