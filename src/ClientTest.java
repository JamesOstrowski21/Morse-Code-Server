import javax.swing.*;

/**
 * Class that creates a client in main and runs it
 */
public class ClientTest {
    /**
     * Main class that runs client
     */
    public static void main(String[] args)
    {
        MorseCodeClient application; // declare client application

        // if no command line args
        if (args.length == 0)
            application = new MorseCodeClient("127.0.0.1"); // connect to localhost
        else
            application = new MorseCodeClient(args[0]); // use args to connect

        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        application.runClient(); // run client application
    }
}
