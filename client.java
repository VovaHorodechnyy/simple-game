package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) throws Exception {
       // launch(args);
        CClient client = new CClient(14777,"127.0.0.1");
        Thread thread = new Thread(client);
        thread.start();
    }
}
class CClient extends Thread
{
    private Socket m_pClient;
    private int m_nPort;
    private String m_sAddress;
    private ObjectOutputStream m_pObjectOutputStream;
    private ObjectInputStream m_pObjectInputStream;

    public CClient(int aPort,String aAddress) throws Exception
    {
        this.m_nPort=aPort;
        this.m_sAddress=aAddress;
        m_pClient = new Socket(aAddress,aPort);
        m_pObjectOutputStream = new ObjectOutputStream(m_pClient.getOutputStream());
        m_pObjectInputStream = new ObjectInputStream(m_pClient.getInputStream());

    }

    @Override
    public void run()
    {
        CGame game = new CGame();
        while(!game.getWin())
        {
            try {
                String clientMessage = (String) m_pObjectInputStream.readObject();
                System.out.println("From server: "+clientMessage);

                Scanner in = new Scanner(System.in);
                System.out.println("Enter some number: ");
                int input = in.nextInt();
                m_pObjectOutputStream.writeObject(input);
                Thread.sleep(500);


                game.check(clientMessage);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

    }
}

class CGame
{
    private boolean isWin=false;

    public boolean getWin()
    {
        return isWin;
    }
    public boolean check(String aRes)
    {
        if(aRes=="WON__UB")
        {
            isWin=true;
        }
        else
        {
            System.out.println(aRes);
        }
        return  isWin;
    }

}
