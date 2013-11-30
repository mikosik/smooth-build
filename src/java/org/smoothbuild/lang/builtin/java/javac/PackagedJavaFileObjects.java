package org.smoothbuild.lang.builtin.java.javac;

import static org.smoothbuild.lang.builtin.java.util.JavaNaming.isClassFilePredicate;

import javax.tools.JavaFileObject;

import org.smoothbuild.lang.builtin.java.Unjarer;
import org.smoothbuild.lang.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.message.listen.ErrorMessageException;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class PackagedJavaFileObjects {
  public static Multimap<String, JavaFileObject> packagedJavaFileObjects(PluginApi pluginApi,
      Iterable<SBlob> libraryJars) {
    Unjarer unjarer = new Unjarer(pluginApi);
    Multimap<String, JavaFileObject> result = HashMultimap.create();

    for (SBlob jarBlob : libraryJars) {
      SArray<SFile> files = unjarer.unjar(jarBlob, isClassFilePredicate());
      for (SFile classFile : files) {
        InputClassFile inputClassFile = new InputClassFile(classFile);
        String aPackage = inputClassFile.aPackage();
        if (result.containsEntry(aPackage, inputClassFile)) {
          throw new ErrorMessageException(new DuplicateClassFileError(classFile.path()));
        } else {
          result.put(aPackage, inputClassFile);
        }
      }
    }

    return result;
  }
}
