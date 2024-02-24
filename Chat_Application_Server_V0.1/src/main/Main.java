package main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;



public class Main {
	public static DataInputStream inputstream;
	public static DataOutputStream outputstream;
	public static String username;
	public static String dataofusers = "";
	public static File usersFolder; // Initialize this appropriately
    private static int activeUsers = 0;
    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    public static PrintWriter output;
    public static String adminName ="x";
    public static String adminPassword="x";
    public static String adminName2 ="x";
    public static String adminPassword2="x";
    public static String adminName3 ="x";
    public static String adminPassword3="x";
    
    public static void main(String[] args) {
        try {
            usersFolder = new File("Users.txt");
            if (usersFolder.exists()) {
                System.out.println("dosya zaten var");
            } else {
                if (usersFolder.createNewFile()) {
                    System.out.println("dosya oluşturuldu");
                }
            }
            ServerSocket serverSocket = new ServerSocket(6472);
            System.out.println("server started");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                try {
                    outputstream = new DataOutputStream(clientSocket.getOutputStream());
                    inputstream = new DataInputStream(clientSocket.getInputStream());
                    System.out.println("A new client connected : " + clientSocket.getInetAddress().getHostAddress());
                    BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    output = new PrintWriter(clientSocket.getOutputStream(), true);
                    String data = inputstream.readUTF();
                    System.out.println(data);
                    String splitedData[] = data.split(":");
                    username = splitedData[1];
                    System.out.println("splidata 0 :" + splitedData[0]);
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);
                    if (splitedData[0].equals("1")) {
                        login_Operation(data);
                    }
                    if (splitedData[0].equals("2")) {
                        register_Operation(data);
                    }
                    if (splitedData[0].equals("3")) {
                        saveNotes_Operation(data);
                    }
                    if (splitedData[0].equals("4")) {
                        changePassword_Operation(data);
                    }
                    if (splitedData[0].equals("5")) {
                   
                        clientHandler.start();
                    }
                } catch (Exception e) {
                    System.out.println("a client disconnected");
                }
            }
        } catch (Exception e) {
            System.out.println("a client disconnected");
        }
    }

	 static class ClientHandler extends Thread {
	        private Socket clientSocket;
	        private DataOutputStream output;
	        private DataInputStream inputstream;

	        public ClientHandler(Socket socket) {
	            this.clientSocket = socket;
	        }

	        public void run() {
	            try {
	    			inputstream = new DataInputStream(clientSocket.getInputStream());
	                output = new DataOutputStream(clientSocket.getOutputStream());

	                while (true) {
	                    String message = inputstream.readUTF();
	                    String splitedData[]=message.split(":");
	                    
	                    if (!(splitedData[2] == null)) {
	                    	 System.out.println("Received message: " + message);
		 	                    broadcast(splitedData[1]+" :"+splitedData[2]);
	                    }
	                    if(splitedData[2].equals("disconnected")) {
	                    	broadcast(splitedData[1]+" :"+splitedData[2]);
	                    }
	                   
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            } finally {
	                try {
	                    clientSocket.close();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        }

	        private void broadcast(String message) {
	        	
	            for (ClientHandler client : clients) {
	            	try {
		                client.output.writeUTF(message);

					} catch (Exception e) {
						// TODO: handle exception
					}
	            }
	        }
	    }
	public static void login_Operation(String data) throws IOException {
		try {
			Scanner filescanner = new Scanner(usersFolder);
			String splitedData[] = data.split(":");
			boolean found1 = false;
			
			System.out.println("data :" + data);
			if((splitedData[1].equals(adminName)&&splitedData[2].equals(adminPassword))||(splitedData[1].equals(adminName2)&&splitedData[2].equals(adminPassword2))||(splitedData[1].equals(adminName3)&&splitedData[2].equals(adminPassword3))) {
				System.out.println("123");
				outputstream.writeUTF("3");
			}
			else {

				while (filescanner.hasNextLine()) {
					String lines1 = filescanner.nextLine();
					String[] parts1 = lines1.split(":");

					if (splitedData[1].equals(parts1[0])) {
						if (splitedData[2].equals(parts1[1])) {

							System.out.println("user found in text");
							File file1 = new File(username + ".txt");
							Scanner userfolderscanner = new Scanner(file1);
							String filedata = "";
							while (userfolderscanner.hasNextLine()) {
								String data1 = userfolderscanner.nextLine();
								filedata += data1 + "\n";

							}
							userfolderscanner.close();
							found1 = true;

							outputstream.writeUTF("1");
							outputstream.writeUTF(filedata);
							break;

						}

					}

				}
				filescanner.close();
				if (found1 == false) {

					outputstream.writeUTF("0");

				}

			}
		} catch (FileNotFoundException e) {
		
			e.printStackTrace();
		}

	}

	public static void register_Operation(String data) throws IOException {

		Scanner filescanner = new Scanner(usersFolder);
		FileWriter fileWriter = new FileWriter(usersFolder, true);
		boolean found2 = false;
		String splitedData[] = data.split(":");

		while (filescanner.hasNextLine()) {

			String lines2 = filescanner.nextLine();
			String parts2[] = lines2.split(":");

			if (splitedData[1].equals(parts2[0])||splitedData[1].equals(adminName)||splitedData[1].equals(adminName2)||splitedData[1].equals(adminName)||splitedData[1].equals(adminName3)||splitedData[1].equals(adminName2)||splitedData[1].equals(adminName3)) {
				found2 = true;
				outputstream.writeUTF("0");
				break;

			}

		}
		filescanner.close();

		if (found2 == false) {
			outputstream.writeUTF("1");
			fileWriter.write(splitedData[1] + ":" + splitedData[2] + "\n");

			File newfolder = new File(username + ".txt");

			try {
				if (newfolder.exists()) {
					System.out.println(splitedData[1] + " already has a file");
				} else {
					if (newfolder.createNewFile()) {
						System.out.println("new file created for " + splitedData[1]);
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		fileWriter.close();

	}

	public static void saveNotes_Operation(String data) throws IOException {

		System.out.println("savenotes");
		String splitedData[] = data.split(":");

		FileWriter fileWriter_username = new FileWriter(username + ".txt");
		String splitedData3_2 = splitedData[2];

		if (splitedData3_2.equals("EMPTY")) {
			System.out.println(username + " doesnt want to save something");
			outputstream.writeUTF("1");

		} else {
			fileWriter_username.write(splitedData[2]);
			outputstream.writeUTF("1");
			fileWriter_username.close();

		}

	}

	public static void changePassword_Operation(String data) {
		try {
			System.out.println(data);
			String splitedData[] = data.split(":");
			System.out.println(
					"kullanıcı adı ve şifre : " + splitedData[1] + " " + splitedData[2] + " " + splitedData[3]);
			Scanner filescanner = new Scanner(usersFolder);

			boolean found5 = false;
			File draftFile = new File("draftFile.txt");

			FileWriter draftFile_Writer = new FileWriter(draftFile);
			while (filescanner.hasNextLine()) {

				String lines5 = filescanner.nextLine();
				String[] parts5 = lines5.split(":");
				System.out.println("partlanan kısımlar   :" + parts5[0] + " " + parts5[1]);

				if (splitedData[1].equals(parts5[0])) {
					if (splitedData[2].equals(parts5[1])) {
						found5 = true;
					}
				}

			}
			filescanner.close();
			if (found5 = true) {
				System.out.println("true");

				Scanner filescanner2 = new Scanner(usersFolder);
				while (filescanner2.hasNextLine()) {
					int time = 0;
					time++;
					String lines5_ = filescanner2.nextLine();
					String parts5_[] = lines5_.split(":");

					// System.out.println("partlanan kısımlar :"+ time+" :"+ parts5_[0] + " " +
					// parts5_[1]);
					String parts5_username = parts5_[0];
					String parts5_password = parts5_[1];

					if (!splitedData[1].equals(parts5_username)) {
						System.out.println("username bulundu");
						System.out.println(parts5_password);
						if (!splitedData[2].equals(parts5_password)) {

							draftFile_Writer.write(parts5_username + ":" + parts5_password + "\n");
							System.out.println("users saved");

						} else {
							System.out.println("bulunamadı şifre");
						}

					}

				}
				filescanner2.close();

				draftFile_Writer.write(splitedData[1] + ":" + splitedData[3] + "\n");
				filescanner.close();
				draftFile_Writer.close();

				outputstream.writeUTF("1");
				usersFolder.delete();
				draftFile.renameTo(usersFolder);

			}

			if (found5 == false) {
				System.out.println("false");
				outputstream.writeUTF("0");

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
