package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferType;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);
    private final TransferService transferService = new TransferService();
    private final String blueColorCode = "\u001B[34m";
    private final String resetColorCode = "\u001B[0m";
    //TODO - Maybe use this for all of our menu selections?
    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println(blueColorCode + " /$$$$$$$$  /$$$$$$$$  ");
        System.out.println("|__  $$__/ | $$_____/  ");
        System.out.println("   | $$    | $$        /$$$$$$$   /$$$$$$/$$$$    /$$$$$$ ");
        System.out.println("   | $$    | $$$$$    | $$__  $$ | $$_  $$_  $$  /$$__  $$");
        System.out.println("   | $$    | $$__/    | $$  \\ $$ | $$ \\ $$ \\ $$ | $$  \\ $$");
        System.out.println("   | $$    | $$       | $$  | $$ | $$ | $$ | $$ | $$  | $$");
        System.out.println("   | $$    | $$$$$$$$ | $$  | $$ | $$ | $$ | $$ |  $$$$$$/");
        System.out.println("   |__/    |________/ |__/  |__/ |__/ |__/ |__/  \\______/ " + resetColorCode);
        System.out.println("***********************************************************");
        System.out.println("***                  Welcome to TEnmo!!!                ***");
        System.out.println("***********************************************************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("-------------------------------------------");
        System.out.println("Main Menu");
        System.out.println("-------------------------------------------");
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transactions");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

    public void printOtherUsers(User[] users){
        for (User user : users){
            System.out.println("Username: " + user.getUsername() + ", User ID: " + user.getId());
        }
    }

    public void printTransferDetails(Transfer transfer){
        System.out.println("-----------------------------------------");
        System.out.println("Transfer Details");
        System.out.println("-----------------------------------------");
        System.out.println("ID: " + transfer.getTransfer_id());
        System.out.println("From: " + transfer.getAccount_from());
        System.out.println("To: " + transfer.getAccount_to());
        System.out.println("Type: " + transferService.getTransferTypeById(transfer.getTransfer_type_id()).getTransferTypeDesc());
        System.out.println("Status: " + transferService.getTransferStatusById(transfer.getTransfer_status_id()).getTransferStatusDesc());
        System.out.println("Amount: $" + transfer.getAmount());
    }
}
