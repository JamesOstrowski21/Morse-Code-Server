import javax.swing.*;

/**
 * Class that creates a server in main and runs it.
 */
public class ServerTest {
    /**
     * Main class that runs server
     * @param args
     */
    public static void main(String[] args)
    {
        MorseCodeServer application = new MorseCodeServer(); // create server
        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        application.runServer(); // run server application
    }
}
