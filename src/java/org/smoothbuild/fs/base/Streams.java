package org.smoothbuild.fs.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.fs.base.exc.FileSystemException;

import com.google.common.io.ByteStreams;

public class Streams {
  public static void copy(InputStream from, OutputStream to) {
    try (InputStream input = from; OutputStream output = to) {
      ByteStreams.copy(input, output);
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }
}
