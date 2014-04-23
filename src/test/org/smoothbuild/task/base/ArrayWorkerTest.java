package org.smoothbuild.task.base;

import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.taskresults.TaskResult;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.exec.FakeNativeApi;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;

public class ArrayWorkerTest {
  private final FakeNativeApi nativeApi = new FakeNativeApi();
  private final CodeLocation codeLocation = new FakeCodeLocation();
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();

  private final SString string1 = nativeApi.string("string1");
  private final SString string2 = nativeApi.string("string2");

  ArrayWorker<?> arrayWorker;

  @Test
  public void execute() {
    given(arrayWorker = new ArrayWorker<>(STRING_ARRAY, codeLocation));
    when(arrayWorker).execute(ImmutableList.of(string1, string2), nativeApi);
    thenReturned(new TaskResult<>(objectsDb.array(STRING_ARRAY, string1, string2), Empty
        .messageList()));
  }
}
