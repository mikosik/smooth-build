package org.smoothbuild.task;

import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.function.def.DefinitionNode;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.hash.HashCode;

public class TaskGenerator {
  private final Map<HashCode, Task> generatedTasks;

  @Inject
  public TaskGenerator() {
    this.generatedTasks = Maps.newHashMap();
  }

  public HashCode generateTask(DefinitionNode node) {
    Task justGenerated = node.generateTask(this);
    HashCode hash = justGenerated.hash();
    Task prevGenerated = generatedTasks.get(hash);
    if (prevGenerated == null) {
      generatedTasks.put(hash, justGenerated);
    }
    return hash;
  }

  public Map<HashCode, Task> allTasks() {
    return ImmutableMap.copyOf(generatedTasks);
  }
}
