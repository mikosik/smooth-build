package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.containsString;
import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.io.fs.base.Path;

public class DefaultValueTest extends AcceptanceTestCase {
  @Test
  public void default_value_for_string_is_empty_string() throws Exception {
    givenScript("result = stringIdentity();");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent(""));
  }

  @Test
  public void default_value_for_blob_is_empty_stream() throws Exception {
    givenScript("result = blobIdentity();");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent(""));
  }

  @Test
  public void default_value_for_file_has_root_path() throws Exception {
    givenScript("result = fileIdentity() | path();");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent(Path.root().value()));
  }

  @Test
  public void default_value_for_file_has_empty_content() throws Exception {
    givenScript("result = fileIdentity() | content();");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent(""));
  }

  @Test
  public void default_value_for_string_array_is_empty_array() throws Exception {
    givenScript("result = stringArrayIdentity();");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void default_value_for_nothing_array_is_empty_array() throws Exception {
    givenScript("result = nothingArrayIdentity();");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void default_value_for_nothing_doesnt_exist() throws Exception {
    givenScript("result = nothingIdentity();");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("build.smooth:1: error: Parameter 'nothing' has to be "
        + "assigned explicitly as type 'Nothing' doesn't have default value."));
  }
}
