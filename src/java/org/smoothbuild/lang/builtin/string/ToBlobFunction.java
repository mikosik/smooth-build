package org.smoothbuild.lang.builtin.string;

import static org.smoothbuild.command.SmoothContants.CHARSET;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.smoothbuild.io.cache.value.build.BlobBuilder;
import org.smoothbuild.io.fs.base.exc.FileSystemException;
import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SString;

public class ToBlobFunction {
  public interface Parameters {
    @Required
    public SString string();
  }

  @SmoothFunction(name = "toBlob")
  public static SBlob execute(PluginApi pluginApi, Parameters params) {
    return stringToBlob(pluginApi, params.string());
  }

  public static SBlob stringToBlob(PluginApi pluginApi, SString string) {
    BlobBuilder builder = pluginApi.blobBuilder();
    try (OutputStreamWriter writer = new OutputStreamWriter(builder.openOutputStream(), CHARSET)) {
      writer.write(string.value());
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
    return builder.build();
  }
}
