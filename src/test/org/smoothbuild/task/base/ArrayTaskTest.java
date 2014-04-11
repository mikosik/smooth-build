package org.smoothbuild.task.base;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.base.FakeResult;
import org.smoothbuild.testing.task.exec.FakePluginApi;

import com.google.common.collect.ImmutableList;

public class ArrayTaskTest {
  FakePluginApi pluginApi = new FakePluginApi();
  CodeLocation codeLocation = new FakeCodeLocation();
  SString string1 = pluginApi.valueDb().writeString("string1");
  SString string2 = pluginApi.valueDb().writeString("string2");

  Result<SString> result1 = new FakeResult<>(string1);
  Result<SString> result2 = new FakeResult<>(string2);

  ArrayTask<?> arrayTask;

  @Test
  public void execute() {
    List<Result<SString>> newArrayList = ImmutableList.of(result1, result2);
    given(arrayTask = new ArrayTask<>(STRING_ARRAY, newArrayList, codeLocation));
    when(arrayTask.execute(pluginApi));
    thenReturned(contains(string1, string2));
  }
}
