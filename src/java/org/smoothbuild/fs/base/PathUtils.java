package org.smoothbuild.fs.base;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

/**
 * Helper methods for operating on Strings that represent filesystem paths.
 * 
 * All methods (except {@link #isValid(String)} {@link #validationError(String)}
 * {@link #toCanonical(String)}) require their arguments to be valid and in
 * canonical form. When method returns a path then such path is valid and in
 * canonical form (unless one of parameter was not valid or canonical).
 */
public class PathUtils {
  public static final String WORKING_DIR = ".";
  public static final String CURRENT_DIR = ".";

  public static final char SEPARATOR_CHARACTER = '/';
  public static final String SEPARATOR = new String(new char[] { SEPARATOR_CHARACTER });

  private PathUtils() {}

  public static boolean isValid(String path) {
    return validationError(path) == null;
  }

  public static String validationError(String path) {
    if (path.isEmpty()) {
      return "Empty paths are not allowed";
    }
    if (path.startsWith("/")) {
      return "Path cannot start with slash character '/'. Only paths relative to project root dir are allowed";
    }
    if (path.contains("//")) {
      return "Path cannot contain two slashes (//) in a row";
    }
    if (path.contains("/./") || path.endsWith("/.")) {
      return "Path can contain '.' element only at the beginning (for example './mypath').";
    }
    if (path.equals("..") || path.startsWith("../") || path.contains("/../")
        || path.endsWith("/..")) {
      return "Path cannot contain '..' element. Referencing files outside your project is a bad idea.";
    }
    return null;
  }

  /**
   * @return Canonical version of given path.
   */
  public static String toCanonical(String path) {
    // remove ending '/'
    path = path.endsWith(SEPARATOR) ? path.substring(0, path.length() - 1) : path;
    // remove trailing './'
    return path.startsWith("./") ? path.substring(2, path.length()) : path;
  }

  /**
   * @return parent directory of given path.
   * @throws IllegalArgumentException
   *           if path equals to {@link #WORKING_DIR}
   */
  public static String parentOf(String path) {
    if (path.equals(WORKING_DIR)) {
      throw new IllegalArgumentException("Cannot return parent of working dir '.'");
    }
    int index = path.lastIndexOf(SEPARATOR_CHARACTER);
    if (index == -1) {
      return WORKING_DIR;
    } else {
      return path.substring(0, index);
    }
  }

  /**
   * @return List of all name elements of given path. For path equal to
   *         {@link #WORKING_DIR} returns empty list.
   */
  public static ImmutableList<String> toElements(String path) {
    if (path.equals(WORKING_DIR)) {
      return ImmutableList.<String> of();
    } else {
      return ImmutableList.copyOf(Splitter.on(SEPARATOR_CHARACTER).split(path));
    }
  }

  /**
   * @return Returns last element in given path.
   * @throws IllegalArgumentException
   *           if path equals to {@link #WORKING_DIR}
   */
  public static String lastElement(String path) {
    if (path.equals(WORKING_DIR)) {
      throw new IllegalArgumentException("Cannot return last element of working dir '.'");
    }
    int index = path.lastIndexOf(SEPARATOR_CHARACTER);
    if (index == -1) {
      return path;
    } else {
      return path.substring(index + 1, path.length());
    }
  }

  /**
   * @return Concatenated paths.
   */
  public static String append(String path1, String path2) {
    if (path1.equals(WORKING_DIR)) {
      return path2;
    } else if (path2.equals(CURRENT_DIR)) {
      return path1;
    } else {
      return path1 + SEPARATOR + path2;
    }
  }
}
