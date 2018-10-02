package com.company;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("ENTER 'j' - TO JOIN, 'r' - TO REGISTRATION");
            while (true) {
                String letter = scanner.next();
                if (letter.equals("j")) join();
                else if (letter.equals("r")) registration();
                else System.out.println("ENTER 'j' - TO JOIN, 'r' - TO REGISTRATION");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void showOldMessages(String login) {
        Thread th = new Thread(new GetThread(login));
        th.setDaemon(true);
        th.start();
    }

    private static void goChat(String login) throws IOException {
        Scanner scanner = new Scanner(System.in);
        writeHelpCommands();
        System.out.println("ENTER YOU MESSAGE: ");
        while (true) {
            String text = scanner.nextLine();
            if (text.equals("list")) {
                showUsersStatus();
            } else if (text.isEmpty() || text.equals("exit")) {
                exit(login);
            } else if (text.equals("help")) {
                writeHelpCommands();
            } else if (text.equals("incognito")) {
                sendMessageOneUser(login);
            } else if (text.equals("room")) {
                enterRoom(login);
            } else {
                String to = "All";
                sendMessage(login, to, text);
            }
        }
    }

    private static void enterRoom(String login) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("SELECT ROOM FROM 1 TO 4, OR 'exit' - TO EXIT");
            String room = scanner.nextLine();
            if (room.equals("1") || room.equals("2")
                    || room.equals("3") || room.equals("4")) {
                chatRoom(login, room);
            } else if (room.equals("exit")) {
                goChat(login);
            }
        }
    }

    private static void chatRoom(String login, String room) throws IOException {
        Scanner scanner = new Scanner(System.in);
        welcomeRoom(room);
        addUserRoom(login, room);
        String to = "room" + room;
        while (true) {
            String text = scanner.nextLine();
            if (text.equals("list")) {
                showUsersStatus();
            } else if (text.isEmpty() || text.equals("exit")) {
                exitRoom(login, to);
                goChat(login);
            } else if (text.equals("help")) {
                writeHelpCommands();
            } else if (text.equals("incognito")) {
                sendMessageOneUser(login);
            } else {
                sendMessage(login, to, text);
            }
        }
    }

    private static void exitRoom(String login, String room) throws IOException {
        System.out.println(RequestWoker.getResponse(Utils.getURL() + "/status?room=" + room + "&login=" + login));
    }

    private static void addUserRoom(String login, String room) throws IOException {
        RequestWoker.getResponse(Utils.getURL() + "/addUserRoom?login=" + login + "&room=room" + room);
    }

    private static void welcomeRoom(String room) {
        System.out.println("Welcome to the room #" + room);
    }

    private static void sendMessageOneUser(String login) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("WRITE MESSAGE, PLEASE:");
        String text = scanner.nextLine();
        System.out.print("WRITE THE USERLOGIN TO SEND THIS MESSAGE: ");
        String to = scanner.next();
        try {
            if (checkLogin(to)) {
                sendMessage(login, to, text);
            } else {
                System.out.println("NO SUCH USER!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkLogin(String to) throws IOException {
        String resultChecking = RequestWoker.getResponse(Utils.getURL() + "/checkLogin?login=" + to);
        if (resultChecking.equals("pravda")) {
            return true;
        } else {
            return false;
        }
    }

    private static void writeHelpCommands() throws IOException {
        System.out.println(RequestWoker.getResponse(Utils.getURL() + "/help"));
    }

    private static void sendMessage(String login, String to, String text) throws IOException {
        Message m = new Message(login, to, text);
        int res = m.send(Utils.getURL() + "/add");
        if (res != 200) { // 200 OK
            System.out.println("HTTP error occured: " + res);
            return;
        }
    }

    private static void exit(String login) throws IOException {
        String exitFromChat = RequestWoker.getResponse(Utils.getURL() + "/status?room=exitChat&login=" + login);
        if (exitFromChat.equals("offline")) {
            System.out.println(login + " LEFT THE CHAT.");
            System.exit(-1);
        }
    }

    private static void showUsersStatus() throws IOException {
        System.out.println(RequestWoker.getResponse(Utils.getURL() + "/list"));
    }

    private static void join() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("ENTER YOUR LOGIN: ");
        String login = scanner.nextLine();
        System.out.println("ENTER YOUR PASSWORD: ");
        String password = scanner.nextLine();
        String join = RequestWoker.getResponse(Utils.getURL() + "/join?login=" + login + "&password=" + password);
        if (join.equals("registration")) {
            System.out.println("YOU AREN`T REGISTRED IN THE CHAT. LET`S SIGN UP NOW!");
            registration();
        } else if (join.equals("join")) {
            System.out.println(login + ", WELCOME TO THE CHAT!!!");
            showOldMessages(login);
            goChat(login);
        }
    }

    private static void registration() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("ENTER YOUR NAME: ");
        String name = scanner.nextLine();
        System.out.println("ENTER YOUR SURNAME: ");
        String surname = scanner.nextLine();
        System.out.println("ENTER YOUR LOGIN: ");
        String login = scanner.nextLine();
        System.out.println("ENTER YOUR PASSWORD: ");
        String password = scanner.nextLine();
        System.out.println("REENTER YOUR PASSWORD: ");
        String rePassword = scanner.nextLine();
        String status = "offline";
        if (name.length() < 2 || login.length() < 2 || password.length() < 2 ||
                name.contains(" ") || login.contains(" ") || password.contains(" ")) {
            System.out.println("WRONG LOGIN OR PASSWORD FORMAT. TRY AGAIN!");
            registration();
        } else if (!password.equals(rePassword)) {
            System.out.println("PASSWORDS ARE NOT EQUALS.TRY AGAIN!");
            registration();
        }
        String registrationResult = RequestWoker.getResponse(Utils.getURL() + "/registration?"
                + "name=" + name + "&surname=" + surname + "&login=" + login
                + "&password=" + password + "&status=" + status);
        if (registrationResult.equals("loginBusy")) {
            System.out.println("THIS LOGIN IS BUSY, SELECT ANOTHER. TRY AGAIN!");
            registration();
        } else if (registrationResult.equals("registration is successful")) {
            System.out.println("REGISTRATION IS SUCCESSFUL. " + login + ", WELCOME TO THE CHAT!");
            showOldMessages(login);
            goChat(login);
        }
    }
}
