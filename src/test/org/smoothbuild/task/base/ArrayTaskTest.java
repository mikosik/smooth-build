package org.smoothbuild.task.base;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.base.FakeResult;
import org.smoothbuild.testing.task.exec.FakePluginApi;

public class ArrayTaskTest {
  FakePluginApi pluginApi = new FakePluginApi();
  CodeLocation codeLocation = new FakeCodeLocation();
  SString string1 = pluginApi.objectDb().writeString("string1");
  SString string2 = pluginApi.objectDb().writeString("string2");

  Result result1 = new FakeResult(string1);
  Result result2 = new FakeResult(string2);

  ArrayTask arrayTask;

  @Test
  public void execute() {
    given(arrayTask = new ArrayTask(STRING_ARRAY, newArrayList(result1, result2), codeLocation));
    when(arrayTask.execute(pluginApi));
    thenReturned(contains(string1, string2));
  }
}
