package org.smoothbuild.task.base;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.taskresults.TaskResult;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.message.FakeCodeLocation;

public class StringWorkerTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final SString sstring = objectsDb.string("my string");
  private final CodeLocation codeLocation = new FakeCodeLocation();
  private StringWorker worker = new StringWorker(sstring, codeLocation);

  @Test(expected = NullPointerException.class)
  public void null_result_is_forbidden() throws Exception {
    new StringWorker(null, codeLocation);
  }

  @Test
  public void execute_returns_string_passed_to_constructor() {
    given(worker = new StringWorker(sstring, codeLocation));
    when(worker.execute(null, null));
    thenReturned(new TaskResult<>(sstring));
  }
}
