package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.util.Streams.inputStreamToString;

import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.io.cache.value.build.BlobBuilder;
import org.smoothbuild.io.cache.value.build.FileBuilder;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.io.cache.value.FakeValueDb;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.lang.type.FileArrayMatchers;
import org.smoothbuild.testing.message.FakeCodeLocation;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;

public class PluginApiImplTest {
  String content = "content";
  Path path1 = path("my/path/file1.txt");
  Path path2 = path("my/path/file2.txt");
  Task task = task();

  FakeFileSystem fileSystem = new FakeFileSystem();
  FakeValueDb valueDb = new FakeValueDb(fileSystem);

  PluginApiImpl pluginApi = new PluginApiImpl(fileSystem, valueDb, task);

  @Test
  public void file_array_builder_stores_files_in_value_db() throws Exception {
    BlobBuilder blobBuilder = pluginApi.blobBuilder();
    StreamTester.writeAndClose(blobBuilder.openOutputStream(), content);
    SBlob blob = blobBuilder.build();

    FileBuilder fileBuilder = pluginApi.fileBuilder();
    fileBuilder.setPath(path1);
    fileBuilder.setContent(blob);
    SFile file = fileBuilder.build();

    ArrayBuilder<SFile> builder = pluginApi.arrayBuilder(FILE_ARRAY);
    builder.add(file);
    HashCode hash = builder.build().hash();

    SArray<SFile> fileArray = valueDb.read(FILE_ARRAY, hash);
    MatcherAssert.assertThat(fileArray, FileArrayMatchers.containsFileContaining(path1, content));
    assertThat(Iterables.size(fileArray)).isEqualTo(1);
  }

  @Test
  public void string_array_builder_stores_files_in_value_db() throws Exception {
    String jdkString1 = "my string 1";
    String jdkString2 = "my string 2";

    SString string1 = pluginApi.string(jdkString1);
    SString string2 = pluginApi.string(jdkString2);

    ArrayBuilder<SString> builder = pluginApi.arrayBuilder(STRING_ARRAY);
    builder.add(string1);
    builder.add(string2);
    SArray<SString> stringArray = builder.build();

    SArray<SString> stringArrayRead = valueDb.read(STRING_ARRAY, stringArray.hash());
    List<String> strings = Lists.newArrayList();
    for (SString string : stringArrayRead) {
      strings.add(string.value());
    }

    assertThat(strings).containsOnly(jdkString1, jdkString2);
  }

  @Test
  public void file_builder_stores_file_in_value_db() throws Exception {
    BlobBuilder blobBuilder = pluginApi.blobBuilder();
    StreamTester.writeAndClose(blobBuilder.openOutputStream(), content);
    SBlob blob = blobBuilder.build();

    FileBuilder fileBuilder = pluginApi.fileBuilder();
    fileBuilder.setPath(path1);
    fileBuilder.setContent(blob);
    HashCode hash = fileBuilder.build().hash();

    SFile file = valueDb.read(FILE, hash);
    assertThat(file.path()).isEqualTo(path1);
    assertThat(inputStreamToString(file.openInputStream())).isEqualTo(content);
  }

  @Test
  public void string_stores_its_content_in_value_db() throws Exception {
    String jdkString = "my string";
    SString string = pluginApi.string(jdkString);

    SString stringRead = valueDb.read(STRING, string.hash());

    assertThat(stringRead.value()).isEqualTo(jdkString);
  }

  @Test
  public void fileSystem() throws Exception {
    assertThat(pluginApi.projectFileSystem()).isSameAs(fileSystem);
  }

  @Test
  public void reportedErrors() throws Exception {
    Message errorMessage = new Message(ERROR, "message");
    pluginApi.report(errorMessage);
    assertThat(pluginApi.messageGroup()).containsOnly(errorMessage);
  }

  private static Task task() {
    Task task = mock(Task.class);
    Mockito.when(task.name()).thenReturn("name");
    Mockito.when(task.codeLocation()).thenReturn(new FakeCodeLocation());
    return task;
  }
}
