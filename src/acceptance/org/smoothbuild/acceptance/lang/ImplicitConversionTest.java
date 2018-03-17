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
    givenScript("File result = file('//file.txt');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void file_array_is_implicitly_converted_to_blob_array() throws IOException {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript("[Blob] result = [file('//file1.txt'), file('//file2.txt')];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith("abc", "def"));
  }

  @Test
  public void empty_array_is_implicitly_converted_to_string_array() throws IOException {
    givenScript("[String] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void empty_array_is_implicitly_converted_to_blob_array() throws IOException {
    givenScript("[Blob] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void empty_array_is_implicitly_converted_to_file_array() throws IOException {
    givenScript("[File] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void empty_array_is_implicitly_converted_to_string_array_array() throws IOException {
    givenScript("[[String]] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void empty_array_is_implicitly_converted_to_string_array_array_array() throws IOException {
    givenScript("[[[String]]] result = [];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void array_with_empty_array_is_implicitly_converted_to_string_array_array()
      throws Exception {
    givenScript("[[String]] result = [[]];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith(new Object[] { new Object[] {} }));
  }

  @Test
  public void array_with_empty_array_is_implicitly_converted_to_string_array_array_array()
      throws Exception {
    givenScript("[[[String]]] result = [[]];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith(new Object[] { new Object[] {} }));
  }

  @Test
  public void array_with_array_with_empty_array_is_implicitly_converted_to_string_array_array_array()
      throws Exception {
    givenScript("[[[String]]] result = [[[]]];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith(new Object[] { new Object[] { new Object[] {} } }));
  }

  @Test
  public void file_array_array_is_implicitly_converted_to_blob_array_array() throws IOException {
    givenFile("file1.txt", "abc");
    givenFile("file2.txt", "def");
    givenScript("[[Blob]] result = [[file('//file1.txt'), file('//file2.txt')]];");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith(new Object[] { new Object[] { "abc", "def" } }));
  }

  @Test
  public void string_array_cannot_be_converted_to_string_array_array() throws IOException {
    givenScript("[[String]] result = ['abc'];");
    whenSmoothBuild("result");
    thenFinishedWithError();
  }

  @Test
  public void file_array_cannot_be_converted_to_blob_array_array() throws IOException {
    givenFile("file1.txt", "abc");
    givenScript("[[Blob]] result = [file('//file1.txt')];");
    whenSmoothBuild("result");
    thenFinishedWithError();
  }
}
