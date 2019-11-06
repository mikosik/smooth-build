package org.smoothbuild.lang.object.db;

import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class ValuesDbTest extends TestingContext {
  private Hash hash;

  @Test
  public void written_string_can_be_read_back() throws Exception {
    given(hash = valuesDb().writeString("abc"));
    when(() -> valuesDb().readString(hash));
    thenReturned("abc");
  }

  @Test
  public void written_empty_sequence_of_hashes_can_be_read_back() throws Exception {
    given(hash = valuesDb().writeHashes());
    when(() -> valuesDb().readHashes(hash));
    thenReturned(list());
  }

  @Test
  public void written_one_element_sequence_of_hashes_can_be_read_back() throws Exception {
    given(hash = valuesDb().writeHashes(Hash.of("abc")));
    when(() -> valuesDb().readHashes(hash));
    thenReturned(list(Hash.of("abc")));
  }

  @Test
  public void written_two_elements_sequence_of_hashes_can_be_read_back() throws Exception {
    given(hash = valuesDb().writeHashes(Hash.of("abc"), Hash.of("def")));
    when(() -> valuesDb().readHashes(hash));
    thenReturned(list(Hash.of("abc"), Hash.of("def")));
  }

  @Test
  public void not_written_sequence_of_hashes_cannot_be_read_back() {
    when(() -> valuesDb().readHashes(Hash.of("abc")));
    thenThrown(IOException.class);
  }

  @Test
  public void corrupted_sequence_of_hashes_cannot_be_read_back() throws Exception {
    given(hash = valuesDb().writeString("12345"));
    when(() -> valuesDb().readHashes(hash));
    thenThrown(IOException.class);
  }

}