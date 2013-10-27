package org.smoothbuild.object;

import static org.smoothbuild.testing.message.ErrorMessageMatchers.containsInstanceOf;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.hash.Hash;
import org.smoothbuild.object.err.NoObjectWithGivenHashError;
import org.smoothbuild.testing.fs.base.FakeFileSystem;

import com.google.common.hash.HashCode;

public class TaskResultDbTest {
  TaskResultDb taskResultDb = new TaskResultDb(new HashedDb(new FakeFileSystem()));
  HashCode taskHash = Hash.string("abc");
  HashCode resultHash = Hash.string("def");

  @Test
  public void task_result_db_does_not_contain_not_added_result() {
    when(taskResultDb.contains(taskHash));
    thenReturned(false);
  }

  @Test
  public void task_result_db_contains_added_result() {
    given(taskResultDb).store(taskHash, resultHash);
    when(taskResultDb.contains(taskHash));
    thenReturned(true);
  }

  @Test
  public void added_value_can_be_read_back() throws Exception {
    given(taskResultDb).store(taskHash, resultHash);
    when(taskResultDb.read(taskHash));
    thenReturned(resultHash);
  }

  @Test
  public void reading_not_added_value_fails() throws Exception {
    when(taskResultDb).read(taskHash);
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }
}
