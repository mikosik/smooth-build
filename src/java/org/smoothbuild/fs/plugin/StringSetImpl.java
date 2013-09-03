package org.smoothbuild.fs.plugin;

import java.util.Iterator;
import java.util.Set;

import org.smoothbuild.plugin.StringSet;

import com.google.common.collect.Sets;

public class StringSetImpl implements StringSet {
  private final Set<String> set;

  public StringSetImpl() {
    this.set = Sets.newHashSet();
  }

  @Override
  public Iterator<String> iterator() {
    return set.iterator();
  }

  @Override
  public boolean contains(String string) {
    return set.contains(string);
  }

  public void add(String string) {
    set.add(string);
  }
}
