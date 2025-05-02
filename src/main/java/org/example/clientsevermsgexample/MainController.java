package org.example.clientsevermsgexample;



import com.sun.tools.javac.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;

public class MainController implements Initializable {
    @FXML
    private ComboBox dropdownPort;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dropdownPort.getItems().addAll("7",     // ping
                "13",     // daytime
                "21",     // ftp
                "23",     // telnet
                "71",     // finger
                "80",     // http
                "119",     // nntp (news)
                "161"      // snmp);
        );
    }

    @FXML
    private Button clearBtn;

    @FXML
    private TextArea resultArea;

    @FXML
    private Label server_lbl;

    @FXML
    private Button testBtn;

    @FXML
    private Label test_lbl;

    @FXML
    private TextField urlName;

    Socket socket1;
    Socket socket2;

    Label lb122, lb12;
    TextField msgText;

    TextArea tf_message;
    TextArea tf_message2;
    TextField messageArea1;
    TextField messageArea2;

    @FXML
    void checkConnection(ActionEvent event) {

        String host = urlName.getText();
        int port = Integer.parseInt(dropdownPort.getValue().toString());

        try {
            Socket sock = new Socket(host, port);
            resultArea.appendText(host + " listening on port " + port + "\n");
            sock.close();
        } catch (UnknownHostException e) {
            resultArea.setText(String.valueOf(e) + "\n");
            return;
        } catch (Exception e) {
            resultArea.appendText(host + " not listening on port "
                    + port + "\n");
        }


    }


    @FXML
    void clearBtn(ActionEvent event) {
        resultArea.setText("");
        urlName.setText("");

    }



    @FXML
    void startServer(ActionEvent event) {
        Stage stage = new Stage();
        Group root = new Group();
        Label lb11 = new Label("Server");
        lb11.setLayoutX(100);
        lb11.setLayoutY(100);

        lb12 = new Label("info");
        lb12.setLayoutX(100);
        lb12.setLayoutY(200);
        root.getChildren().addAll(lb11, lb12);
        Scene scene = new Scene(root, 600, 350);
        stage.setScene(scene);
        lb12.setText("Server is running and waiting for a client...");

        stage.setTitle("Server");
        stage.show();


        new Thread(this::runServer).start();

    }

    String message;

    private void runServer() {
        try {

            ServerSocket serverSocket = new ServerSocket(6666);
            updateServer("Server is running and waiting for a client...");
            while (true) { // Infinite loop
                try {
                    Socket clientSocket = serverSocket.accept();
                    updateServer("Client connected!");

                    new Thread(() -> {
                        try {
                            sleep(3000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

                    message = dis.readUTF();
                    updateServer("Message from client: " + message);

                    // Sending a response back to the client
                    dos.writeUTF("Received: " + message);

                    dis.close();
                    dos.close();

                } catch (IOException e) {
                    updateServer("Error: " + e.getMessage());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (message.equalsIgnoreCase("exit")) break;

            }
        } catch (IOException e) {
            updateServer("Error: " + e.getMessage());
        }
    }

    private void updateServer(String message) {
        // Run on the UI thread
        javafx.application.Platform.runLater(() -> lb12.setText(message + "\n"));
    }


    @FXML
    void startClient(ActionEvent event) {
        Stage stage = new Stage();
        Group root = new Group();
        Button connectButton = new Button("Connect to server");
        connectButton.setLayoutX(100);
        connectButton.setLayoutY(300);
        connectButton.setOnAction(this::connectToServer);
        // new Thread(this::connectToServer).start();

        Label lb11 = new Label("Client");
        lb11.setLayoutX(100);
        lb11.setLayoutY(100);
        msgText = new TextField("msg");
        msgText.setLayoutX(100);
        msgText.setLayoutY(150);

        lb122 = new Label("info");
        lb122.setLayoutX(100);
        lb122.setLayoutY(200);
        root.getChildren().addAll(lb11, lb122, connectButton, msgText);


        Scene scene = new Scene(root, 600, 350);
        stage.setScene(scene);
        stage.setTitle("Client");
        stage.show();


    }


    private void connectToServer(ActionEvent event) {


        try {
            socket1 = new Socket("localhost", 6666);

            DataOutputStream dos = new DataOutputStream(socket1.getOutputStream());
            DataInputStream dis = new DataInputStream(socket1.getInputStream());

            dos.writeUTF(msgText.getText());
            String response = dis.readUTF();
            updateTextClient("Server response: " + response + "\n");

            dis.close();
            dos.close();
            socket1.close();
        } catch (Exception e) {
            updateTextClient("Error: " + e.getMessage() + "\n");
        }


    }

    private void updateTextClient(String message) {
        // Run on the UI thread
        javafx.application.Platform.runLater(() -> lb122.setText(message + "\n"));
    }

    public void openUser1() {

        Stage stage = new Stage();
        AnchorPane root = new AnchorPane();

        Label textLabel = new Label();
        textLabel.setLayoutX(170); textLabel.setLayoutY(25); textLabel.setText("Message!");

        messageArea1 = new TextField();
        messageArea1.setLayoutX(30); messageArea1.setLayoutY(70); messageArea1.setPrefWidth(420); messageArea1.setPrefHeight(255);

        tf_message = new TextArea();
        tf_message.setLayoutX(34); tf_message.setLayoutY(340); tf_message.setPrefWidth(361.6); tf_message.setPrefHeight(361.6);

        Button sendMsg1 = new Button();
        sendMsg1.setLayoutX(404); sendMsg1.setLayoutY(340); sendMsg1.setPrefWidth(46); sendMsg1.setPrefHeight(26);
        sendMsg1.setText("Send");
        sendMsg1.setOnAction(this::connectToUser2);

        root.getChildren().addAll(tf_message, messageArea1, textLabel, sendMsg1);

        Scene scene = new Scene(root, 470, 365);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        new Thread(this::runMsgServer).start();
    }

    public void openUser2() {

        Stage stage = new Stage();
        AnchorPane root = new AnchorPane();

        Label textLabel = new Label();
        textLabel.setLayoutX(170); textLabel.setLayoutY(25); textLabel.setText("Message!");

        messageArea2 = new TextField();
        messageArea2.setLayoutX(30); messageArea2.setLayoutY(70); messageArea2.setPrefWidth(420); messageArea2.setPrefHeight(255);

        tf_message2 = new TextArea();
        tf_message2.setLayoutX(34); tf_message2.setLayoutY(340); tf_message2.setPrefWidth(361.6); tf_message2.setPrefHeight(361.6);

        Button sendMsg2 = new Button();
        sendMsg2.setLayoutX(404); sendMsg2.setLayoutY(340); sendMsg2.setPrefWidth(46); sendMsg2.setPrefHeight(26);
        sendMsg2.setText("Send");
        sendMsg2.setOnAction(this::connectToUser);

        root.getChildren().addAll(tf_message2, messageArea2, textLabel, sendMsg2);

        Scene scene = new Scene(root, 470, 365);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        new Thread(this::runMsgServer2).start();
    }

    private void connectToUser2(ActionEvent event) {

        try {
            socket1 = new Socket("localhost", 6666);

            DataOutputStream dos = new DataOutputStream(socket1.getOutputStream());
            DataInputStream dis = new DataInputStream(socket1.getInputStream());

            dos.writeUTF(tf_message.getText());
            String response = dis.readUTF();
            updateMessages(response + "\n");

            dis.close();
            dos.close();
            socket1.close();

        } catch (Exception e) {
            //updateMessages("Error: " + e.getMessage() + "\n");
        }
    }

    private void updateMessages(String message) {
        // Run on the UI thread
        javafx.application.Platform.runLater(() -> messageArea2.setText(messageArea2.getText() + "\n" + message));
    }

    private void runMsgServer() {

        try {

            ServerSocket serverSocket = new ServerSocket(6666);
            //updateMessages("Server is running and waiting for a client...");
            while (true) { // Infinite loop
                try {
                    Socket clientSocket = serverSocket.accept();
                    //updateMessages("Client connected!");

                    new Thread(() -> {
                        try {
                            sleep(3000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

                    message = dis.readUTF();
                    updateMessages(message + "\n");

                    // Sending a response back to the client
                    //dos.writeUTF(message + "\n");

                    dis.close();
                    dos.close();

                } catch (IOException e) {
                    //updateMessages("Error: " + e.getMessage());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (message.equalsIgnoreCase("exit")) break;

            }
        } catch (IOException e) {
            //updateMessages("Error: " + e.getMessage());
        }
    }

    private void connectToUser(ActionEvent event) {

        try {
            socket2 = new Socket("localhost", 6666);

            DataOutputStream dos = new DataOutputStream(socket2.getOutputStream());
            DataInputStream dis = new DataInputStream(socket2.getInputStream());

            dos.writeUTF(tf_message2.getText());
            String response = dis.readUTF();
            updateMessages(response + "\n");

            dis.close();
            dos.close();
            socket2.close();

        } catch (Exception e) {
            //updateMessages("Error: " + e.getMessage() + "\n");
        }
    }

    private void updateMessages2(String message) {
        // Run on the UI thread
        javafx.application.Platform.runLater(() -> messageArea1.setText(messageArea1.getText() + "\n" + message));
    }

    private void runMsgServer2() {

        try {

            ServerSocket serverSocket = new ServerSocket(6666);
            //updateMessages2("Server is running and waiting for a client...");
            while (true) { // Infinite loop
                try {
                    Socket clientSocket = serverSocket.accept();
                    //updateMessages("Client connected!");

                    new Thread(() -> {
                        try {
                            sleep(3000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

                    message = dis.readUTF();
                    updateMessages2(message + "\n");

                    // Sending a response back to the client
                    //dos.writeUTF(message + "\n");

                    dis.close();
                    dos.close();

                } catch (IOException e) {
                    updateMessages("Error: " + e.getMessage());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (message.equalsIgnoreCase("exit")) break;

            }
        } catch (IOException e) {
           // updateMessages2("Error: " + e.getMessage());
        }
    }
}
