package io.zrz.graphql.cli;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Splitter;

import io.airlift.airline.Arguments;
import io.airlift.airline.Cli;
import io.airlift.airline.Cli.CliBuilder;
import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.Option;
import io.airlift.airline.OptionType;
import io.airlift.airline.ParseException;
import io.zrz.graphql.core.binder.TypeScanner;
import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.lang.GQLSchemaBuilder;
import io.zrz.graphql.core.lang.GQLTypeRegistry;
import io.zrz.graphql.core.parser.GQLParser;
import io.zrz.graphql.core.types.GQLTypes;
import io.zrz.graphql.core.utils.TypePrinter;
import io.zrz.graphql.generator.java.JavaClientGenerator;

public class CommandLineMain {

  public static void main(String[] args) {

    final CliBuilder<Runnable> builder = Cli.builder("jgql");

    builder.withDescription("GraphQL Toolkit");
    builder.withDefaultCommand(Help.class);

    final List<Class<? extends Runnable>> cmds = new LinkedList<>();

    cmds.add(Help.class);

    cmds.add(ExtractSchema.class);
    cmds.add(GenerateClientCode.class);

    cmds.add(RegistryPush.class);
    cmds.add(RegistryGet.class);
    cmds.add(RegistryMonitor.class);
    cmds.add(RunQuery.class);
    cmds.add(Diff.class);
    cmds.add(TestSchema.class);

    builder.withCommands(cmds);

    final Cli<Runnable> parser = builder.build();

    try {
      parser.parse(args).run();
    } catch (final ParseException e) {
      System.err.print(e.getMessage());
      System.err.println(String.format(".  See '%s help'.", self()));
    }

  }

  private static Object self() {
    return System.getProperty("argv0", "jgql");
  }

  public abstract static class GitCommand implements Runnable {

    @Option(type = OptionType.GLOBAL, name = "-v", description = "Verbose mode")
    public boolean verbose;

  }

  @Command(name = "generate", description = "Generate client code")
  public static class GenerateClientCode extends GitCommand {

    @Option(name = { "-s", "--schema" }, type = OptionType.COMMAND, description = "Path to GraphQL schema", required = true)
    public String schema;

    @Option(name = { "-c", "--class" }, type = OptionType.COMMAND, description = "Fully qualified class name for client", required = true)
    public String className;

    @Option(name = { "-r", "--root" }, type = OptionType.COMMAND, description = "GQL output type used as root", required = true)
    public String rootQueryType;

    @Option(name = { "-o", "--output" }, type = OptionType.COMMAND, description = "Path to output (default: stdout)", required = false)
    public String output;

    @Arguments(description = "GraphQL Query File", required = true)
    public String queryFile;

    @Override
    public void run() {
      try {

        final GQLTypeRegistry ps = new GQLParser().parseSchema(this.schema.equals("-") ? System.in : new FileInputStream(Paths.get(this.schema).toFile()));

        // now, generate the queries.
        final GQLDocument doc = GQLParser.parseDocument(new FileInputStream(this.queryFile));

        // and generate the java code ...
        final JavaClientGenerator gen = new JavaClientGenerator(ps, ps.decl(this.rootQueryType), doc);

        if (this.output == null || this.output.isEmpty() || this.output.equals("-")) {
          gen.generate(this.className, System.out);
        } else {
          final FileOutputStream out = new FileOutputStream(this.output);
          gen.generate(this.className, out);
          out.flush();
          out.close();
        }
      } catch (final Exception ex) {
        ex.printStackTrace(System.err);
        System.err.println("BOOM");
      }
    }

  }

  @Command(name = "extract", description = "Extract schema from JAR")
  public static class ExtractSchema extends GitCommand {

    @Option(name = { "-cp" }, type = OptionType.COMMAND, description = "Classpath", required = true)
    public List<String> classpath;

    @Option(name = { "-o", "--output" }, type = OptionType.COMMAND, description = "Path to output (default: stdout)", required = false)
    public String output;

    @Arguments(description = "GraphQL root class", required = true)
    public List<String> classNames;

    @Override
    public void run() {

      final URL[] urls = this.classpath.stream()
          .map(cp -> Splitter.on(':').omitEmptyStrings().trimResults().splitToList(cp))
          .flatMap(in -> in.stream())
          .map(cp -> {
            try {
              return Paths.get(cp).toAbsolutePath().toUri().toURL();
            } catch (final Exception ex) {
              throw new RuntimeException(ex);
            }
          })
          .toArray(len -> new URL[len]);

      try (URLClassLoader cloader = new URLClassLoader(urls)) {

        final GQLSchemaBuilder builder = new GQLSchemaBuilder();

        final TypeScanner scanner = new TypeScanner(builder);

        builder.add(GQLTypes.builtins());

        for (final String className : this.classNames) {
          final Class<?> klass = cloader.loadClass(className);
          scanner.add(klass);
        }

        scanner.finish();

        final GQLTypeRegistry reg = builder.build();

        reg.apply(new TypePrinter(System.out));

      } catch (final Exception e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }

    }

  }

}