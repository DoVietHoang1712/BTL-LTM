/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import server.controller.GameClient;
import server.controller.ClientManager;
import server.controller.RoomManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

 
public class RunServer {

    public static volatile ClientManager clientManager;
    public static volatile RoomManager roomManager;
    public static boolean isShutDown = false;
    public static ServerSocket ss;

    public RunServer() {

        try {
            int port = 12345;

            ss = new ServerSocket(port);
            System.out.println("Created Server at port " + port + ".");

            // init managers
            clientManager = new ClientManager();
            roomManager = new RoomManager();

            // create threadpool
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    10, // corePoolSize
                    100, // maximumPoolSize
                    100, // thread timeout
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(20) // queueCapacity
            );

            // server main loop - listen to client's connection
            while (!isShutDown) {
                try {
                    // socket object to receive incoming client requests
                    Socket s = ss.accept();
                    // System.out.println("+ New Client connected: " + s);

                    // create new client runnable object
                    GameClient c = new GameClient(s);
                    clientManager.add(c);

                    // execute client runnable
                    executor.execute(c);

                } catch (IOException ex) {
                    // Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    isShutDown = true;
                }
            }

            System.out.println("shutingdown executor...");
            executor.shutdownNow();

        } catch (IOException ex) {
            Logger.getLogger(RunServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        new RunServer();
    }
}
