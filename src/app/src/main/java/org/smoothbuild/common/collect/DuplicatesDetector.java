package org.smoothbuild.common.collect;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;

public class DuplicatesDetector<T> {
  private final Multiset<T> set = HashMultiset.create();
  private boolean hasDuplicates = false;

  public boolean addValue(T value) {
    boolean alreadyContains = set.contains(value);
    hasDuplicates = hasDuplicates || alreadyContains;
    set.add(value);
    return alreadyContains;
  }

  public boolean hasDuplicates() {
    return hasDuplicates;
  }

  public Set<T> getDuplicateValues() {
    Set<T> result = new HashSet<>();
    for (Multiset.Entry<T> entry : set.entrySet()) {
      if (1 < entry.getCount()) {
        result.add(entry.getElement());
      }
    }
    return result;
  }

  public Set<T> getUniqueValues() {
    return ImmutableSet.copyOf(set.elementSet());
  }
}
