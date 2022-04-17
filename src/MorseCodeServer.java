import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Class that creates a server and converts an input from client to either english or morse code.
 *
 */

public class MorseCodeServer extends JFrame{
    /**
     * Text box to send message
     */
    private JTextField enterField; // inputs message from user
    /**
     * Displays info in GUI
     */
    private JTextArea displayArea; // display information to user
    /**
     * Output to client
     */
    private ObjectOutputStream output; // output stream to client
    /**
     * Input from client
     */
    private ObjectInputStream input; // input stream from client
    /**
     * server socket
     */
    private ServerSocket server; // server socket
    /**
     * connection to client
     */
    private Socket connection; // connection to client
    /**
     * counts connections
     */
    private int counter = 1; // counter of number of connections
    /**
     * stores client input to be converted
     */
    private String clientinput;//store client input.
    /**
     * Hashtable that holds all character conversions
     */
    public Hashtable<String,String> Table = new Hashtable<String,String>();//dictionary of morse code conversion.

    /**
     * Constructor that sets up GUI
     */
    // set up GUI
    public MorseCodeServer()
    {
        super("MorseCodeServer");

        enterField = new JTextField(); // create enterField
        enterField.setEditable(false);
        enterField.addActionListener(
                new ActionListener()
                {
                    // send message to client
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
        Populate(Table);
    }

    /**
     * Sets up and runs the server
     */
    // set up and run server
    public void runServer()
    {
        try // set up server to receive connections; process connections
        {
            server = new ServerSocket(12345, 100); // create ServerSocket

            while (true)
            {
                try
                {
                    waitForConnection(); // wait for a connection
                    getStreams(); // get input & output streams
                    processConnection(); // process connection
                }
                catch (EOFException eofException)
                {
                    displayMessage("\nServer terminated connection");
                }
                finally
                {
                    closeConnection(); //  close connection
                    ++counter;
                }
            }
        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }

    /**
     * Waits for Connection from client
     * @throws IOException
     */
    // wait for connection to arrive, then display connection info
    private void waitForConnection() throws IOException
    {
        displayMessage("Waiting for connection\n");
        connection = server.accept(); // allow server to accept connection
        displayMessage("Connection " + counter + " received from: " +
                connection.getInetAddress().getHostName());
    }

    /**
     * Gets input and output data
     * @throws IOException
     */
    // get streams to send and receive data
    private void getStreams() throws IOException
    {
        // set up output stream for objects
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush(); // flush output buffer to send header information

        // set up input stream for objects
        input = new ObjectInputStream(connection.getInputStream());

        displayMessage("\nGot I/O streams\n");
    }

    /**
     * Processes connection to client
     * @throws IOException
     */

    // process connection with client
    private void processConnection() throws IOException
    {
        String message = "Connection successful";
        sendData(message); // send connection successful message

        // enable enterField so server user can send messages
        setTextFieldEditable(true);

        do // process messages sent from client
        {
            try // read message and display it
            {
                message = (String) input.readObject(); // read new message
                clientinput = message;//stores message received as clientinput
                displayMessage("\n" + message); // display message
                sendData(MorseCodeConverter(clientinput));// Sends back morse code or english depending on what was entered
            }
            catch (ClassNotFoundException classNotFoundException)
            {
                displayMessage("\nUnknown object type received");
            }

        } while (!message.equals("CLIENT>>> TERMINATE"));
    }

    /**
     * Closes connection to client
     */
    // close streams and socket
    private void closeConnection()
    {
        displayMessage("\nTerminating connection\n");
        setTextFieldEditable(false); // disable enterField

        try
        {
            output.close(); // close output stream
            input.close(); // close input stream
            connection.close(); // close socket
        }
        catch (IOException ioException)
        {
            ioException.printStackTrace();
        }
    }

    /**
     * Allows server to send message to the client
     * @param message String that needs to be sent
     */
    // send message to client
    private void sendData(String message)
    {
        try // send object to client
        {
            output.writeObject("SERVER>>> " + message);
            output.flush(); // flush output to client
            displayMessage("\nSERVER>>> " + message);
        }
        catch (IOException ioException)
        {
            displayArea.append("\nError writing object");
        }
    }

    /**
     * Displays the message in GUI
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
                        displayArea.append(messageToDisplay); // append message
                    }
                }
        );
    }

    /**
     * Makes text editable or able to send a message
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

    /**
     * Converts string from client into either morse code or english depending on what the client sends.
     * @param input input from client as string
     * @return returns conversion as string
     */
    public String MorseCodeConverter(String input){
        String output = "";//stores output
        ArrayList<String> outputwords = new ArrayList<String>(); //array of words to combine
        String word; //stores each word as they are converted
        String value;//stores each letter in hash
        String[] words;//stores all words in input
        if(input.charAt(0) == '.' || input.charAt(0) == '-'){
            words = input.split("   ");//splits input into words
            for(int y = 0; y < words.length; y++){
                word = "";//resets word to empty string
                String[] letters = words[y].split(" ");//splits word into letters
                for(int x = 0; x < letters.length;x++){
                    value = letters[x];//stores each letter 1 by 1
                    for(Map.Entry entry: Table.entrySet()){//loops through hashtable
                        if(value.equals(entry.getValue())){//checks if letter/morse letter matches
                            word =  word + entry.getKey();//add letter to word
                        }
                    }
                }
                outputwords.add(word);//adds word to array of words
            }
            for(int z = 0;z < outputwords.size(); z ++){
                output = output + outputwords.get(z) + " ";//combines array of words to make sentence
            }
        } else {
            words = input.split(" ");//splits words
            for(int y = 0; y < words.length; y ++){
                word = "";
                String[] letters = words[y].split("");//splits letters
                for(int x = 0; x < letters.length; x ++){
                    value = letters[x];//stores each letter
                    for(Map.Entry entry: Table.entrySet()){ //loops through hashtable
                        if(value.equals(entry.getKey())){ //checks for matching key
                            word =  word + entry.getValue() + " "; //adds converted letter to word
                        }
                    }

                }
                outputwords.add(word); //adds word to array of words
            }
            for(int z = 0;z < outputwords.size(); z ++){ //combines words to make a sentence
                output = output + outputwords.get(z)+ "   ";
            }
        }
        return output;
    }

    /**
     * Populates the hashtable of conversion characters
     * @param T hashtable to populate
     */
    private void Populate(Hashtable<String,String> T){ //populates hashtable with all conversions
        T.put("A", ".-");
        T.put("B", "-...");
        T.put("C", "-.-.");
        T.put("D", "-..");
        T.put("E", ".");
        T.put("F", "..-.");
        T.put("G", "--.");
        T.put("H", "....");
        T.put("I", "..");
        T.put("J", ".---");
        T.put("K", "-.-");
        T.put("L", ".-..");
        T.put("M", "--");
        T.put("N", "-.");
        T.put("O", "---");
        T.put("P", ".--.");
        T.put("Q", "--.-");
        T.put("R", ".-.");
        T.put("S", "...");
        T.put("T", "-");
        T.put("U", "..-");
        T.put("V", "...-");
        T.put("W", ".--");
        T.put("X", "-..-");
        T.put("Y", "-.--");
        T.put("Z", "--..");
        T.put("0", "-----");
        T.put("1", ".----");
        T.put("2", "..---");
        T.put("3", "...--");
        T.put("4", "....-");
        T.put("5", ".....");
        T.put("6", "-....");
        T.put("7", "--...");
        T.put("8", "---..");
        T.put("9", "----.");
    }
}
