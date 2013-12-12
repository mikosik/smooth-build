package org.smoothbuild.lang.builtin.string;

import static org.smoothbuild.command.SmoothContants.CHARSET;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.smoothbuild.io.cache.value.build.BlobBuilder;
import org.smoothbuild.io.fs.base.exc.FileSystemException;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.task.exec.PluginApiImpl;

public class ToBlobFunction {
  public static final Charset US_ASCII = Charset.forName("US-ASCII");

  public interface Parameters {
    @Required
    public SString string();
  }

  @SmoothFunction(name = "toBlob")
  public static SBlob execute(PluginApiImpl pluginApi, Parameters params) {
    BlobBuilder builder = pluginApi.blobBuilder();
    try (OutputStreamWriter writer = new OutputStreamWriter(builder.openOutputStream(), CHARSET)) {
      writer.write(params.string().value());
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
    return builder.build();
  }
}
