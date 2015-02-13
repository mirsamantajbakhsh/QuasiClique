/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.urmiauniversity.it.mst.quasiclique;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Lookup;

/**
 *
 * @author Account
 */
public class QuasiClique implements org.gephi.statistics.spi.Statistics, LongTask {

    private String report = "";
    private boolean cancel = false;
    private ProgressTicket progressTicket;
    private double k = 0.0d;
    private int CliqueID = 0;
    private Set<Set<Node>> Cliques = new HashSet<Set<Node>>();
    TreeSet<Node> orderedNodes;

    GenQueue<TreeSet<Node>> Bk = new GenQueue<TreeSet<Node>>();

    public class SortByDegree implements Comparator<Node> {

        Graph g;

        public SortByDegree(Graph g) {
            this.g = g;
        }

        public int compare(Node n1, Node n2) {
            if (g.getDegree(n1) >= g.getDegree(n2)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Queue Implementation">
    public Object getLastElement(final Collection c) {
        /*
         final Iterator itr = c.iterator();
         Object lastElement = itr.next();
         while (itr.hasNext()) {
         lastElement = itr.next();
         }
         return lastElement;
         */
        return null;
    }

    class GenQueue<E> {

        private LinkedList<E> list = new LinkedList<E>();

        public void enqueue(E item) {
            list.addLast(item);
        }

        public E dequeue() {
            return list.pollFirst();
        }

        public boolean hasItems() {
            return !list.isEmpty();
        }

        public int size() {
            return list.size();
        }

        public void addItems(GenQueue<? extends E> q) {
            while (q.hasItems()) {
                list.addLast(q.dequeue());
            }
        }
    }
    //</editor-fold>

    @Override
    public void execute(GraphModel gm, AttributeModel am) {
        Graph g = gm.getGraphVisible();

        g.readLock();

        //First sample a subgraph -> sort graph by degree and sample the most degree node
        HashSet<Node> subgraph = new HashSet<Node>();
        orderedNodes = new TreeSet<Node>(new SortByDegree(g));

        for (Node n : g.getNodes()) {
            orderedNodes.add(n);
        }

        Node max = orderedNodes.pollFirst(); //First node is the node with max degree

        //Detect quasi clique based on max degree node
        GenQueue<Node> semiMaxQuasi = new GenQueue<Node>();

        semiMaxQuasi.enqueue(max);

        //Local Search
        while (getQuasi(semiMaxQuasi, g) >= k) {
            Node largestNeighbour = getLargestNeighbour(semiMaxQuasi, g);
            semiMaxQuasi.enqueue(largestNeighbour);
        }
        //remove last added node because it made lower value than k
        semiMaxQuasi.list.removeLast();

        //Heuristic pruning
        double klambda = k * semiMaxQuasi.size();
        Vector<Node> peelabels = new Vector<Node>();

        report += "Maximum quasi-clique consists of following nodes ( k = " + semiMaxQuasi.size() + " nodes - lambda : ) " + k + " -> k*lambda = " + klambda + "):<br>";
        for (Node n : semiMaxQuasi.list) {
            report += n.getNodeData().getLabel() + "<br>";
        }
        report += "<br>Local search is done. Now heuristing pruning starts ...<br>";
        
        g.readUnlock();

        int pruningIteration = 1;
        String removedNodes = "";
        
        do {
            peelabels.clear();
            removedNodes = "";
            
            for (Node peelable : g.getNodes()) {
                if (ispeelable(peelable, g, klambda)) {
                    peelabels.addElement(peelable);
                }
            }
            
            for (Node n : peelabels) {
                removedNodes += n.getNodeData().getLabel() + ", ";
                g.removeNode(n);
                
            }
            
            if (!removedNodes.equalsIgnoreCase("")) {
                removedNodes = removedNodes.substring(0, removedNodes.length() - 2);
            }
            report += "Iteration " + pruningIteration++ + ": " + peelabels.size() + " node(s) pruned:<br>" + removedNodes + "<br><br>";
            
        } while (!peelabels.isEmpty());
        
        report += "Heuristic Pruning finished!";
        //Algorithm finished.
        //Write the output
        

    }

    private double getQuasi(GenQueue<Node> semiMaxQuasi, Graph g) {
        if (semiMaxQuasi.size() < 1) {
            return -1.0d;
        } else if (semiMaxQuasi.size() == 1) {
            return 1.0d; //Lambda is 1 for a single node
        } else { //find lamda and compare it to threshold lambda (k)
            double edgesCount = 0;

            for (Node n1 : semiMaxQuasi.list) {
                for (Node n2 : semiMaxQuasi.list) {
                    if (n1 != n2 && g.getEdge(n1, n2) != null) { //undirectedGraph
                        edgesCount++;
                    }
                }
            }
            edgesCount /= 2.0d; //every edge is counted two times
            return (2.0d * edgesCount) / (((double) semiMaxQuasi.size()) * ((double) (semiMaxQuasi.size() - 1)));
        }
    }

    private Node getLargestNeighbour(GenQueue<Node> semiMaxQuasi, Graph g) {
        Node n = null; //maximum node
        n = orderedNodes.last(); //Node with least degree

        for (Node firstNode : semiMaxQuasi.list) {
            for (Node neighboursOfFirstNode : g.getNeighbors(firstNode)) {
                if (g.getDegree(neighboursOfFirstNode) > g.getDegree(n) && !semiMaxQuasi.list.contains(neighboursOfFirstNode)) {
                    n = neighboursOfFirstNode;
                }
            }
        }

        if (n == orderedNodes.last()) { //Check if the least node is connected to semiMaxQuasi?
            for (Node tmp : semiMaxQuasi.list) {
                if (g.getEdge(n, tmp) != null) {
                    return n;
                }
            }
            return null;
        }

        return n;
    }

    private boolean ispeelable(Node peelable, Graph g, double klambda) {
        if (g.getDegree(peelable) < klambda) {
            return true;
            /*
            for (Node neighbour : g.getNeighbors(peelable).toArray()) {
                if (g.getDegree(neighbour) >= klambda) {
                    return false;
                }
            }
            return true;
            */
        }
        return false;
    }

    @Override
    public String getReport() {
        return report;
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket pt) {
        this.progressTicket = pt;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }
}
