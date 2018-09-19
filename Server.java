package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
       Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

    }


    public static void main(String[] args)throws Exception
    {

        CServer server =new CServer(14777);
        Thread game =new Thread(server);
        game.start();

    }
}
class CServer extends Thread
{
    private ServerSocket m_pSocket;
    private Socket m_pClient;
    private ObjectOutputStream m_pObjectOutputStream;
    private ObjectInputStream m_pObjectInputStream;
    private int m_nPort;

    public CServer(int port) throws Exception
    {
        m_nPort=port;
        m_pSocket = new ServerSocket(port);



    }
    @Override
    public void run()
    {
        CGame game =new CGame();
        boolean win=false;
        String result;
        try (Socket socket = m_pClient = m_pSocket.accept())
        {
            m_pObjectOutputStream = new ObjectOutputStream(m_pClient.getOutputStream());
            m_pObjectInputStream = new ObjectInputStream(m_pClient.getInputStream());
            m_pObjectOutputStream.writeObject("Game started");
            while(!win)
            {

                int clientMessage = (int) m_pObjectInputStream.readObject();
                System.out.println("from client: "+clientMessage);
                result=game.check(clientMessage);
                if(result=="you win")
                {
                    win=true;
                    m_pObjectOutputStream.writeObject(result + " :" + game.getTarget());
                    m_pObjectOutputStream.writeObject("WON__UB");
                }
                else
                    m_pObjectOutputStream.writeObject(result + " try again" );

            }

        }catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }


    }




}
class CGame
{
    private int m_nTarget;
    private final String cMORE="n>";
    private final String cLESS="n<";
    private final String cWIN="you win";
    private  Random m_pRnd;

    public int getTarget()
    {
        return m_nTarget;
    }
    public CGame()
    {
        m_pRnd = new Random(System.currentTimeMillis());
        m_nTarget=  m_pRnd.nextInt(100);
        System.out.println(m_nTarget);

    }
    public String check(int cur)
    {
       // int cur = Integer.parseInt(numb);
        if(cur>m_nTarget)
            return cLESS;
        if(cur<m_nTarget)
            return cMORE;
        if(cur==m_nTarget)
            return cWIN;
        else
            return "Undefined behavior";
    }

}