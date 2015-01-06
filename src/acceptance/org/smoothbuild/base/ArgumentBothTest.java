package org.smoothbuild.base;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.artifactPath;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Inject;

import org.junit.Test;
import org.smoothbuild.base.TestingFunctions.TwoStrings;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.lang.function.def.err.AmbiguousNamelessArgsError;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

import com.google.common.io.CharStreams;

public class ArgumentBothTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  @Test
  public void one_named_one_nameless_and_two_parameters_with_type() throws Exception {
    createInjector(new IntegrationTestModule(TwoStrings.class)).injectMembers(this);
    script(fileSystem, "result : twoStrings(stringA='abc', 'def') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "abc:def");
  }

  @Test
  public void error_message_in_ambiguous_argument_mapping() throws Exception {
    createInjector(new IntegrationTestModule(Many.class)).injectMembers(this);
    script(fileSystem, "result : many(string='abc', 'def') ;");
    buildWorker.run(asList("result"));

    userConsole.messages().assertContainsOnly(AmbiguousNamelessArgsError.class);

    String expected = "ERROR [ line 1 ]: Can't find parameter(s) of proper type " //
        + "in 'many' function for some nameless argument(s):\n" //
        + "List of assignments that were successfully detected so far is following:\n" //
        + "  String: string <- String: string #1 [ line 1 ]\n" //
        + "List of arguments for which no parameter could be found is following:\n" //
        + "  String: <nameless> #2 [ line 1 ]\n"; //
    assertEquals(expected, userConsole.messages().iterator().next().toString());
  }

  public static class Many {
    @SmoothFunction
    public static SString many(NativeApi nativeApi, @Name("file") SFile file,
        @Name("blob") Blob blob, @Name("string") SString string) throws IOException {
      InputStream fileStream = file.content().openInputStream();
      InputStream blobStream = blob.openInputStream();
      String fileString = CharStreams.toString(new InputStreamReader(fileStream));
      String blobString = CharStreams.toString(new InputStreamReader(blobStream));

      return nativeApi.string(fileString + ":" + blobString);
    }
  }
}
