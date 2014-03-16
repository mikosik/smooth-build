package org.smoothbuild.cli;

import java.util.Map;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class CliParser {
  public static final String COMMAND_IMPLEMENTATION_DEST = "command_instance";

  private final ArgumentParser parser;
  private final Map<Command, Subparser> map;

  public CliParser() {
    this.parser = ArgumentParsers.newArgumentParser("smooth", false);

    Subparsers subparsers = parser().addSubparsers();
    subparsers.metavar("COMMAND");
    subparsers.help("DESCRIPTION");
    subparsers.title("All available commands are");

    this.map = createCommandParsers(subparsers);
  }

  private ImmutableMap<Command, Subparser> createCommandParsers(Subparsers subparsers) {
    Builder<Command, Subparser> builder = ImmutableMap.builder();

    for (Command command : Command.values()) {
      Subparser subparser = subparsers.addParser(command.commandName());
      subparser.setDefault(COMMAND_IMPLEMENTATION_DEST, command.handler());
      command.commandSpec().configureParser(subparser);
      builder.put(command, subparser);
    }

    return builder.build();
  }

  public ArgumentParser parser() {
    return parser;
  }

  public void printHelp() {
    parser().printHelp();
  }

  public void printHelpFor(Command command) {
    map.get(command).printHelp();
  }

}
