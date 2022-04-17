import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Class makes the client that will connect with the server.
 */

public class MorseCodeClient extends JFrame{
    /**
     * Text box to send message
     */
    private JTextField enterField; // enters information from user
    /**
     * Displays info in GUI
     */
    private JTextArea displayArea; // display information to user
    /**
     * Output to server
     */
    private ObjectOutputStream output; // output stream to server
    /**
     * Input from server
     */
    private ObjectInputStream input; // input stream from server
    /**
     * message form server
     */
    private String message = ""; // message from server
    /**
     * host server
     */
    private String chatServer; // host server for this application
    /**
     * socket to connect with server
     */
    private Socket client; // socket to communicate with server

    /**
     * Constructor of client and sets up client GUI
     * @param host server to be connected with
     */
    // initialize chatServer and set up GUI
    public MorseCodeClient(String host)
    {
        super("MorseCodeClient");

        chatServer = host; // set server to which this client connects

        enterField = new JTextField(); // create enterField
        enterField.setEditable(false);
        enterField.addActionListener(
                new ActionListener()
                {
                    // send message to server
                    public void actionPerformed(ActionEvent event)
                    {
                        sendData(event.getActionCommand());
                        enterField.setText("");
                    }
                }
        );

        add(enterField, BorderLayout.NORTH);

        displayArea = new JTextArea(); // create displayArea
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        setSize(300, 150); // set size of window
        setVisible(true); // show window
    }

    /**
     * Connects to server and processes messages
     */
    // connect to server and process messages from server
    public void runClient()
    {
        try // connect to server, get streams, process connection
        {
            connectToServer(); // create a Socket to make connection
            getStreams(); // get the input and output streams
            processConnection(); // process connection
        }
        catch (EOFException eofException)
        {
            displayMessage("\nClient terminated connection");
        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
        finally
        {
            closeConnection(); // close connection
        }
    }

    /**
     * Connects to server
     * @throws IOException
     */
    // connect to server
    private void connectToServer() throws IOException
    {
        displayMessage("Attempting connection\n");

        // create Socket to make connection to server
        client = new Socket(InetAddress.getByName(chatServer), 12345);

        // display connection information
        displayMessage("Connected to: " +
                client.getInetAddress().getHostName());
    }

    /**
     * Gets data to input and output to server.
     * @throws IOException
     */
    // get streams to send and receive data
    private void getStreams() throws IOException
    {
        // set up output stream for objects
        output = new ObjectOutputStream(client.getOutputStream());
        output.flush(); // flush output buffer to send header information

        // set up input stream for objects
        input = new ObjectInputStream(client.getInputStream());

        displayMessage("\nGot I/O streams\n");
    }

    /**
     * Checks connection
     * @throws IOException
     */
    // process connection with server
    private void processConnection() throws IOException
    {
        // enable enterField so client user can send messages
        setTextFieldEditable(true);

        do // process messages sent from server
        {
            try // read message and display it
            {
                message = (String) input.readObject(); // read new message
                displayMessage("\n" + message); // display message
            }
            catch (ClassNotFoundException classNotFoundException)
            {
                displayMessage("\nUnknown object type received");
            }

        } while (!message.equals("SERVER>>> TERMINATE"));
    }

    /**
     * Closes connection
     */
    // close streams and socket
    private void closeConnection()
    {
        displayMessage("\nClosing connection");
        setTextFieldEditable(false); // disable enterField

        try
        {
            output.close(); // close output stream
            input.close(); // close input stream
            client.close(); // close socket
        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }

    /**
     * sends message to server
     * @param message message to send to server
     */
    // send message to server
    private void sendData(String message)
    {
        try // send object to server
        {
            output.writeObject(message);
            output.flush(); // flush data to output
            displayMessage("\nCLIENT>>> " + message);
        }
        catch (IOException ioException)
        {
            displayArea.append("\nError writing object");
        }
    }

    /**
     * displays message in GUI
     * @param messageToDisplay message being displayed
     */
    // manipulates displayArea in the event-dispatch thread
    private void displayMessage(final String messageToDisplay)
    {
        SwingUtilities.invokeLater(
                new Runnable()
                {
                    public void run() // updates displayArea
                    {
                        displayArea.append(messageToDisplay);
                    }
                }
        );
    }

    /**
     * decides whether a messages is able to be typed and sent.
     * @param editable true or false
     */
    // manipulates enterField in the event-dispatch thread
    private void setTextFieldEditable(final boolean editable)
    {
        SwingUtilities.invokeLater(
                new Runnable()
                {
                    public void run() // sets enterField's editability
                    {
                        enterField.setEditable(editable);
                    }
                }
        );
    }
}
