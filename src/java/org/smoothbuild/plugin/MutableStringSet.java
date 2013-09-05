package org.smoothbuild.plugin;

import java.util.Iterator;
import java.util.Set;


import com.google.common.collect.Sets;

public class MutableStringSet implements StringSet {
  private final Set<String> set;

  public MutableStringSet() {
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
