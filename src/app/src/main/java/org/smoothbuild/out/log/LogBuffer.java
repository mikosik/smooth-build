package org.smoothbuild.out.log;

import static com.google.common.collect.Iterators.unmodifiableIterator;
import static org.smoothbuild.common.collect.List.listOfAll;

import java.util.ArrayList;
import java.util.Iterator;
import org.smoothbuild.common.collect.List;

public class LogBuffer implements Logger, Logs {
  private final ArrayList<Log> list = new ArrayList<>();

  @Override
  public void log(Log log) {
    list.add(log);
  }

  @Override
  public List<Log> toList() {
    return listOfAll(list);
  }

  @Override
  public ImmutableLogs toImmutableLogs() {
    return ImmutableLogs.logs(toList());
  }

  @Override
  public Iterator<Log> iterator() {
    return unmodifiableIterator(list.iterator());
  }

  @Override
  public String toString() {
    return toList().toString(",");
  }
}
