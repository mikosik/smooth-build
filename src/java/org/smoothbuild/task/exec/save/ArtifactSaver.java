package org.smoothbuild.task.exec.save;

import static org.smoothbuild.io.cache.CacheModule.RESULTS_DIR;
import static org.smoothbuild.io.cache.CacheModule.VALUE_DB_DIR;
import static org.smoothbuild.io.cache.hash.HashCodes.toPath;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;

import javax.inject.Inject;

import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.Hashed;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageType;
import org.smoothbuild.message.listen.ErrorMessageException;

public class ArtifactSaver {
  private final FileSystem smoothFileSystem;

  @Inject
  public ArtifactSaver(@SmoothDir FileSystem smoothFileSystem) {
    this.smoothFileSystem = smoothFileSystem;
  }

  public void save(Name name, SValue value) {
    Path artifactPath = RESULTS_DIR.append(path(name.value()));

    if (value.type() == FILE) {
      storeFile(artifactPath, (SFile) value);
    } else if (value.type() == FILE_ARRAY) {
      @SuppressWarnings("unchecked")
      SArray<SFile> fileArray = (SArray<SFile>) value;
      storeFileArray(artifactPath, fileArray);
    } else if (value.type() == STRING) {
      storeString(artifactPath, (SString) value);
    } else if (value.type() == STRING_ARRAY) {
      @SuppressWarnings("unchecked")
      SArray<SString> stringArray = (SArray<SString>) value;
      storeStringArray(artifactPath, stringArray);
    } else {
      throw new ErrorMessageException(new Message(MessageType.FATAL,
          "Bug in smooth binary.\nUnknown value type " + value.getClass().getName()));
    }
  }

  private void storeFile(Path artifactPath, SFile file) {
    Path targetPath = targetPath(file.content());
    smoothFileSystem.delete(artifactPath);
    smoothFileSystem.createLink(artifactPath, targetPath);
  }

  private void storeFileArray(Path artifactPath, SArray<SFile> fileArray) {
    smoothFileSystem.delete(artifactPath);
    for (SFile file : fileArray) {
      Path linkPath = artifactPath.append(file.path());
      Path targetPath = targetPath(file.content());
      smoothFileSystem.createLink(linkPath, targetPath);
    }
  }

  private void storeString(Path artifactPath, SString string) {
    Path targetPath = targetPath(string);
    smoothFileSystem.delete(artifactPath);
    smoothFileSystem.createLink(artifactPath, targetPath);
  }

  private void storeStringArray(Path artifactPath, SArray<SString> stringArray) {
    smoothFileSystem.delete(artifactPath);
    int i = 0;
    for (SString string : stringArray) {
      Path filePath = path(Integer.valueOf(i).toString());
      Path linkPath = artifactPath.append(filePath);
      Path targetPath = targetPath(string);
      smoothFileSystem.createLink(linkPath, targetPath);
      i++;
    }
  }

  private static Path targetPath(Hashed hashed) {
    return VALUE_DB_DIR.append(toPath(hashed.hash()));
  }
}
