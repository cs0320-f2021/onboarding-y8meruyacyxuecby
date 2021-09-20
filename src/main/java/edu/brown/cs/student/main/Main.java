package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;

import com.google.common.collect.ImmutableMap;

import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  // use port 4567 by default when running server
  private static final int DEFAULT_PORT = 4567;

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  /**
   * Method that finds k nearest neighbors of a location
   * @param k - number of neighbors to find
   * @param stars - ArrayList of stars
   * @param x - x coordinate
   * @param y - y coordinate
   * @param z - z coordinate
   * @param isStar - boolean representing if it is a star or not
   * @return ArrayList of star IDs
   * @throws Exception - when k exceeds the number of possible neighbors
   */
  private ArrayList<Integer> naiveNeighbors(int k, ArrayList<Star> stars,
                                         float x, float y, float z,
                                         boolean isStar) throws Exception {
    if (isStar) {
      if (k >= stars.size()) {
        throw new Exception("ERROR: k exceeds number of possible neighbors");
      }
    } else {
      if (k > stars.size()) {
        throw new Exception("ERROR: k exceeds number of possible neighbors");
      }
    }
    Comparator<Star> starComparator = new Comparator<Star>() {
      @Override
      public int compare(Star s1, Star s2) {
        return Double.compare(s1.calcDistance(x, y, z), s2.calcDistance(x, y, z));
      }
    };

    PriorityQueue<Star> nearestQueue = new PriorityQueue<>(starComparator);
    nearestQueue.addAll(stars);

    ArrayList<Integer> nearestStars = new ArrayList<>();

    for (int i = 0; i < k; i++) {
      if (i == 0 && isStar) {
        // If it is the first one, and it is a star, its nearest neighbor cannot
        // be itself, so skip
        i--;
      } else {
        Star nextStar = nearestQueue.poll();
        nearestStars.add(nextStar.getId());
      }
    }

    return nearestStars;

  }

  private void run() {
    // set up parsing of command line flags
    OptionParser parser = new OptionParser();

    // "./run --gui" will start a web server
    parser.accepts("gui");

    // use "--port <n>" to specify what port on which the server runs
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);

    OptionSet options = parser.parse(args);
    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

    ArrayList<Star> stars = null;
    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
      String input;
      while ((input = br.readLine()) != null) {
        try {
          input = input.trim();
          String[] arguments = input.split(" ");
          if (arguments[0].equals("add")) {
            // the subsequent catch statements should catch any errors that
            // the parse methods throw
            double d1 = Double.parseDouble(arguments[1]);
            double d2 = Double.parseDouble(arguments[2]);
            System.out.println(new MathBot().add(d1, d2));
          } else if (arguments[0].equals("subtract")) {
            double d1 = Double.parseDouble(arguments[1]);
            double d2 = Double.parseDouble(arguments[2]);
            System.out.println(new MathBot().subtract(d1, d2));
          } else if (arguments[0].equals("stars")) {
            if (arguments.length != 2) {
              throw new Exception("ERROR: Incorrect amount of arguments!");
            }
            stars = new CSVReader().readCSV(arguments[1]);
            if (stars == null) {
              throw new IOException("ERROR: Invalid File!");
            }
            System.out.println("Read " + stars.size() + " stars from " + arguments[1]);
          } else if (arguments[0].equals("naive_neighbors")) {
            if (stars == null) {
              throw new IOException("ERROR: No Stars Loaded!");
            }
            if (arguments.length == 5) {
              ArrayList<Integer> nearestStars = naiveNeighbors(
                  Integer.parseInt(arguments[1]),
                  stars,
                  Float.parseFloat(arguments[2]),
                  Float.parseFloat(arguments[3]),
                  Float.parseFloat(arguments[4]),
                  false);

              for (int s : nearestStars) {
                System.out.println(s);
              }
            } else if (arguments.length == 3) {
              if (stars == null) {
                throw new IOException("ERROR: No Stars Loaded!");
              } else if (!(arguments[2].charAt(0) == arguments[2].charAt(arguments[2].length() - 1)
                  && arguments[2].charAt(0) == '"')) {
                throw new IOException("ERROR: Invalid star name input");
              } else {
                Star targetStar = null;
                for (Star s : stars) {
                  if (s.getName().equals(arguments[2].substring(1, arguments[2].length() - 1))) {
                    targetStar = s;
                    ArrayList<Integer> nearestStars = naiveNeighbors(
                        Integer.parseInt(arguments[1]),
                        stars,
                        targetStar.getX(),
                        targetStar.getY(),
                        targetStar.getZ(),
                        true);

                    for (int id : nearestStars) {
                      System.out.println(id);
                    }
                    break;
                  }
                }
                if (targetStar == null) {
                  throw new Exception("ERROR: Input star not found.");
                }

              }
            }
          } else {
            throw new Exception("ERROR: Command not found");
          }

        } catch (Exception e) {
//           e.printStackTrace();
          System.out.println("ERROR: We couldn't process your input");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("ERROR: Invalid input for REPL");
    }
  }



  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration(Configuration.VERSION_2_3_0);

    // this is the directory where FreeMarker templates are placed
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    // set port to run the server on
    Spark.port(port);

    // specify location of static resources (HTML, CSS, JS, images, etc.)
    Spark.externalStaticFileLocation("src/main/resources/static");

    // when there's a server error, use ExceptionPrinter to display error on GUI
    Spark.exception(Exception.class, new ExceptionPrinter());

    // initialize FreeMarker template engine (converts .ftl templates to HTML)
    FreeMarkerEngine freeMarker = createEngine();

    // setup Spark Routes
    Spark.get("/", new MainHandler(), freeMarker);
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler<Exception> {
    @Override
    public void handle(Exception e, Request req, Response res) {
      // status 500 generally means there was an internal server error
      res.status(500);

      // write stack trace to GUI
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * A handler to serve the site's main page.
   *
   * @return ModelAndView to render.
   * (main.ftl).
   */
  private static class MainHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      // this is a map of variables that are used in the FreeMarker template
      Map<String, Object> variables = ImmutableMap.of("title",
          "Go go GUI");

      return new ModelAndView(variables, "main.ftl");
    }
  }
}
