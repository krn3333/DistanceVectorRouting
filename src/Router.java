import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Router.java
 * @author    Karan Chauhan
 */

public class Router {

        int size;
    RouterListener listener;
        ArrayList<Edge> neighbours;                            // array list to hold neighbors
        HashMap<String, HashMap<String, Integer>> routingTable; // hash map for the routing table
        String ip;                                              // ip address

           Random random;

        public Router() {

                size = 4;                                     // number of routers is 4
                listener = new RouterListener(this);
                listener.start();                             // start the listener thread
                neighbours = new ArrayList<Edge>();
                routingTable = new HashMap<String, HashMap<String, Integer>>();
                random = new Random();
                try {
                        ip = Inet4Address.getLocalHost().getHostAddress();      // get the ip address
                } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }


        }



                /**
         * This function updates the routing table when it recieves the neighbouring tables
         * @param   rt              object
         * @param   neighbourip     ip address of the neighbour
         */

        public void update(Object rt, String neighbourip) {
                HashMap<String, HashMap<String, Integer>> viaTable = (HashMap<String, HashMap<String, Integer>>) rt;

                for (String destId : viaTable.keySet()) {
                        if (destId.equals(ip))
                                continue;
                        if (!routingTable.containsKey(destId)) {
                                routingTable.put(destId, new HashMap<String, Integer>());
                        }
                        HashMap<String, Integer> temp = viaTable.get(destId);
                        int mincost = Integer.MAX_VALUE;
                        for (String tempValue : viaTable.get(destId).keySet()) {
                                if (viaTable.get(destId).get(tempValue) < mincost) {
                                        mincost = viaTable.get(destId).get(tempValue);
                                }
                        }
                        if(!routingTable.containsKey(destId)) routingTable.put(destId, new HashMap<String,Integer>());
                        if(!routingTable.containsKey(neighbourip)) routingTable.put(neighbourip, new HashMap<String,Integer>());
                        if(!routingTable.get(neighbourip).containsKey(neighbourip)) routingTable.get(neighbourip).put(neighbourip, Integer.MAX_VALUE);
                        if (mincost < Integer.MAX_VALUE && mincost > 0) {
                                if (!routingTable.get(destId).containsKey(neighbourip) || routingTable.get(destId)
                                                .get(neighbourip) > (routingTable.get(neighbourip).get(neighbourip) + mincost)) {
                                        routingTable.get(destId).put(neighbourip, routingTable.get(neighbourip).get(neighbourip) + mincost);
                                }

                        }
                }
                display();
        }


         /**
         * This function adds neighbour to the node
         * @param   ip    ip address
         * @param   dist  cost to the neighbour
         */
        public void addNeighbours(String ip,int dist) {
                System.out.println("Adding neighbour" +ip);


                Edge edge= new Edge(this.ip,ip,dist);               // pass the source,destination and the cost
                if (!neighbours.contains(edge))                     // check if it is a neighbour
                        neighbours.add(edge); // add the edge

                routingTable.put(ip, new HashMap<String , Integer>());
                routingTable.get(ip).put(ip, dist);

                initialize();
        }


        /** 
         * This function initializes the routing tables for each router
         * 
         */
        public void initialize() {
                System.out.print("Initializing Routing tables");
                for (Edge edge : neighbours) {
                        System.out.println("Edge "  +edge.dest);
                        if (!routingTable.containsKey(edge.dest)) {
                                routingTable.put(edge.dest, new HashMap<String, Integer>());
                                routingTable.get(edge.dest).put(edge.dest, edge.distance);
                        }
                }
                display();
        }

/**
         * This function sends out the routing tables to its neighbours
         * 
         */
        public void send() {
                while (true) {
                        for (Edge n : neighbours) {
                                try {
                                        Socket s = new Socket(n.dest, 5555);             // create socket for connection
                                        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

                                        oos.writeObject(routingTable);
                                        oos.flush();
                                } catch (UnknownHostException e) {

                                } catch (IOException e) {

                                }

                        }
                        try {

                                Thread.sleep(5000);                 // wait for 5 seconds

                        } catch (InterruptedException e) {

                        }
                }
        }

        /**
         * This function displays the routing table
         */

        public void display() {

                System.out.print("Neighbours ");
                for (Edge n : neighbours) {
                        System.out.print(n.dest + " ");
                }
                System.out.println();
                System.out.println(routingTable);                    // print the routing table

        }



             public static void main(String args[]) {

                Router r = new Router();
                if (args.length < 2) {
                        System.out.println("enter router1 cost1 router2 cost2....");
                }
                for (int i = 0; i < args.length-1; i=i+2) {
                        r.addNeighbours(args[i],Integer.parseInt(args[i+1]));
                }

                r.send();

        }
}
