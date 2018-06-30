package com.gnavin.velocity.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.gremlin.util.Gremlin;

public class MainV2 {

  public static void main(String[] args) {

    // If you want to check your Gremlin version, uncomment the next line
    System.out.println("Gremlin version is: " + Gremlin.version());

    // write your code here
    TinkerGraph tg = TinkerGraph.open();

//         Create a Traversal source object
    GraphTraversalSource g = tg.traversal();

    final ArrayList<String> asinList = new ArrayList<>();
    asinList.add("asin1");
    asinList.add("asin2");

    // Add some nodes and vertices - Note the use of "iterate".
    g.addV("customer")
        .property("encryptedCustomerId", "c1")
        .property("asinList", asinList)
        .as("c1").
        addV("customer").property("encryptedCustomerId", "c2").as("c2").
        addV("customer").property("encryptedCustomerId", "c3").as("c3").
        addE("friend").from("c1").to("c2").
        addE("friend").from("c1").to("c3").
        addE("friend").from("c2").to("c3").iterate();

    System.out.println(g);
    System.out.println(g.V().valueMap(true).toList());

    // Simple example of how to work with the results we get back from a query

    List<Map<Object, Object>> vm = new ArrayList<Map<Object, Object>>();

    vm = g.V().valueMap(true).toList();

    // Dislpay the code property as well as the label and id.
    for (Map m : vm) {
      System.out
          .println(((List) (m.get("encryptedCustomerId"))).get(0) + " " + m.get(T.id) + " " + m
              .get(T.label));
    }
    System.out.println();

    // Display the routes in the graph we just created.
    // Each path will include the vertex code values and the edge.

    List<Path> paths = new ArrayList<Path>();

    paths = g.V().outE().inV().path().by("encryptedCustomerId").by().toList();

    for (Path p : paths) {
      System.out.println(p.toString());
    }

    // Count how many vertices and edges we just created.
    // Using groupCount is overkill when we only have one label
    // but typically you will have more so this is a useful technique
    // to be aware of.
    System.out.println("\nWe just created");
    List verts = g.V().groupCount().by(T.label).toList();
    System.out.println(((Map) verts.get(0)).get("customer") + " customers");
    List edges = g.E().groupCount().by(T.label).toList();
    System.out.println(((Map) edges.get(0)).get("friend") + " friends");

    // Note that we could also use the following code for a simple
    // case where we are only interested in specific labels.
    Long nv = g.V().hasLabel("customer").count().next();
    Long ne = g.E().hasLabel("friend").count().next();
    System.out.println("The graph has " + nv + " customers and " + ne + " friends");

    // Save the graph we just created as GraphML (XML) or GraphSON (JSON)
    try {
      // If you want to save the graph as GraphML uncomment the next line
      tg.io(IoCore.graphml()).writeGraph("mygraph.graphml");

      // If you want to save the graph as JSON uncomment the next line
      //tg.io(IoCore.graphson()).writeGraph("mygraph.json");
    } catch (IOException ioe) {
      System.out.println("Graph failed to save");
    }
  }
}
