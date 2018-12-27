package org.smoothbuild.acceptance.assign;

import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public abstract class AbstractAssignmentTestCase extends AcceptanceTestCase {
  @Test
  public void string_is_assignable_to_string() throws IOException {
    givenScript(createTestScript("String", "'abc'"));
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void file_is_assignable_to_blob() throws IOException {
    givenFile("file.txt", "abc");
    givenScript(createTestScript("Blob", "file('//file.txt')"));
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void blob_is_not_assignable_to_string() throws IOException {
    givenFile("file.txt", "abc");
    givenScript(createTestScript("String", "file('//file.txt').content"));
    whenSmoothList();
    thenFinishedWithError();
    thenAssignmentError("String", "Blob");
  }

  @Test
  public void file_array_is_assignable_to_blob_array() throws IOException {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript(createTestScript("[Blob]", "[file('//file1.txt'), file('//file2.txt')]"));
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith("abc", "def"));
  }

  @Test
  public void empty_array_is_assignable_to_string_array() throws IOException {
    givenScript(createTestScript("[String]", "[]"));
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void empty_array_is_assignable_to_blob_array() throws IOException {
    givenScript(createTestScript("[Blob]", "[]"));
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void empty_array_is_assignable_to_file_array() throws IOException {
    givenScript(createTestScript("[File]", "[]"));
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void empty_array_is_assignable_to_string_array_array() throws IOException {
    givenScript(createTestScript("[[String]]", "[]"));
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void empty_array_is_assignable_to_string_array_array_array() throws IOException {
    givenScript(createTestScript("[[[String]]]", "[]"));
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void array_with_empty_array_is_assignable_to_string_array_array()
      throws Exception {
    givenScript(createTestScript("[[String]]", "[[]]"));
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith(new Object[] { new Object[] {} }));
  }

  @Test
  public void array_with_empty_array_is_assignable_to_string_array_array_array()
      throws Exception {
    givenScript(createTestScript("[[[String]]]", "[[]]"));
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith(new Object[] { new Object[] {} }));
  }

  @Test
  public void array_with_array_with_empty_array_is_assignable_to_string_array_array_array()
      throws Exception {
    givenScript(createTestScript("[[[String]]]", "[[[]]]"));
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith(new Object[] { new Object[] { new Object[] {} } }));
  }

  @Test
  public void file_array_array_is_assignable_to_blob_array_array() throws IOException {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript(createTestScript("[[Blob]]", "[[file('//file1.txt'), file('//file2.txt')]]"));
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith(new Object[] { new Object[] { "abc", "def" } }));
  }

  @Test
  public void string_array_cannot_be_converted_to_string_array_array() throws IOException {
    givenScript(createTestScript("[[String]]", "['abc']"));
    whenSmoothBuild("result");
    thenFinishedWithError();
  }

  @Test
  public void file_array_cannot_be_converted_to_blob_array_array() throws IOException {
    givenFile("file1.txt", "abc");
    givenScript(createTestScript("[[Blob]]", "[file('//file1.txt')]"));
    whenSmoothBuild("result");
    thenFinishedWithError();
  }

  protected abstract String createTestScript(String type, String value);

  protected abstract void thenAssignmentError(String target, String source);
}
