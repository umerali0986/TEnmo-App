package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.*;

import java.math.BigDecimal;
import java.util.InputMismatchException;
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

    public void printCurrentBalance(AccountService accountService) {

        System.out.println();
        System.out.println("-------------------------------------------");
        System.out.println("Current Balance");
        System.out.println("-------------------------------------------");
        System.out.println();
        System.out.println("$" + accountService.getBalance());
    }


    public void printReceipt(Transfer[] pendingTransfers, AccountService accountService, UserService userService, AuthenticatedUser currentUser) {
        //TODO- add formating to it
        for (Transfer transfer : pendingTransfers){
            if(transfer.getAccount_from() == accountService.getAccountByUserId(currentUser.getUser().getId()).getAccount_id()){
                System.out.println(transfer.getTransfer_id() + " To: " + userService.getUserByAccountId(transfer.getAccount_to()).getUsername() + ", Amount: $" + transfer.getAmount() + ((transfer.getTransfer_status_id() != 1 ?  ", balance: $" + transfer.getCurrentAccountFromBalance() : "")) + ", Date = " + transfer.getTransactionDate());
            }
            else {
                System.out.println(transfer.getTransfer_id() + " From: " + userService.getUserByAccountId(transfer.getAccount_from()).getUsername() + ", Amount: $" + transfer.getAmount()+ ((transfer.getTransfer_status_id() != 1 ?  ", balance: $" + transfer.getCurrentAccountToBalance() : "")) + ", Date = " + transfer.getTransactionDate());
            }
        }

    }

    public int promptUserToInsertRecipientId(UserService userService, AuthenticatedUser currentUser) {
        boolean running = true;
        int recipientId = 0;

        while(running) {
            System.out.println();
            System.out.print("Please select id for recipient (Enter 0 to cancel): ");
            String userInput = scanner.nextLine();


            try {
                recipientId = Integer.parseInt(userInput);
                if(recipientId == 0){
                    running = false;
                    break;
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input for recipient id, please enter valid id");
                continue;
            }

            if(userService.getUserById(recipientId) == null){
                System.out.println("Recipient id is invalid");
                continue;
            }
            else if(currentUser.getUser().getId() == recipientId){
                System.out.println("You can't transfer money to your own account.");
                continue;
            }

            break;
        }

        return recipientId;
    }

    public BigDecimal promptUserToInsertTransferAmount(AccountService accountService, AuthenticatedUser currentUser, TransferService transferServices, int recipientId) {
        BigDecimal transferAmount = new BigDecimal(0);
        boolean running = true;

        while(running) {
            System.out.println();
            System.out.print("Please enter the amount to send (Enter 0 to cancel): ");

            try {
                transferAmount = scanner.nextBigDecimal();
            } catch (InputMismatchException e) {
                System.out.println("Input must be a valid currency amount.");
                scanner.nextLine();
                continue;
            }

            if (transferAmount.compareTo(new BigDecimal(0.0)) == 0.0) {
                break;
            }

            if (transferAmount.compareTo(new BigDecimal(0.01)) == -1) {
                System.out.println("Please enter an amount greater than $0.01 ");
                continue;
            }

            if (transferAmount.scale() > 2) {
                System.out.println("Transfer amount must not contain more than 2 decimal places.");
                continue;
            } else {


                Account senderAccount = accountService.getAccountByUserId(recipientId);
                Account receiverAccount = accountService.getAccountByUserId(currentUser.getUser().getId());

                Transfer transfer = new Transfer();


                transfer.setAmount(transferAmount);
                transfer.setAccount_from(senderAccount.getAccount_id());
                transfer.setAccount_to(receiverAccount.getAccount_id());
                transfer.setTransfer_type_id(1);
                transfer.setTransfer_status_id(1);
                transferServices.createReceipt(transfer);

            }
            break;

        }

        return transferAmount;
    }
}
