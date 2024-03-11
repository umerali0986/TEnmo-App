package com.techelevator.tenmo;

import com.techelevator.tenmo.Dto.UpdateAccountDto;
import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.InputMismatchException;
import java.util.Scanner;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService();
    private final UserService userService = new UserService();
    private final TransferService transferService = new TransferService();

    private final Scanner scanner = new Scanner(System.in);

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println();
        System.out.println("-------------------------------------------");
        System.out.println("Register");
        System.out.println("-------------------------------------------");
        System.out.println();
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println();
            System.out.println("-------------------------------------------");
            System.out.println("Registration successful. You can now login.");
            System.out.println("-------------------------------------------");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        System.out.println();
        System.out.println("-------------------------------------------");
        System.out.println("Login");
        System.out.println("-------------------------------------------");
        System.out.println();
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        } else {
            accountService.setAuthToken(currentUser.getToken());
            userService.setAuthToken(currentUser.getToken());
            transferService.setAuthToken(currentUser.getToken());
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
                consoleService.pause();
            } else if (menuSelection == 2) {
                transferMenu();
            } else if (menuSelection == 3) {
                pendingMenu();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println();
                System.out.println("Invalid Selection");
            }
        }
    }

    private void pendingMenu() {
        boolean running = true;

        while(running) {
            System.out.println();
            System.out.println("-------------------------------------------");
            System.out.println("Pending Transactions Menu");
            System.out.println("-------------------------------------------");
            System.out.println();
            System.out.println("1: View all pending transactions");
            System.out.println("2: Accept or decline pending transaction by Id");
            System.out.println("0: Return to Main Menu ");
            System.out.println();
            System.out.print("Please enter an option: ");

            String userInput = scanner.nextLine();
            int selection = 0;
            try {
                selection = Integer.parseInt(userInput);

            } catch (NumberFormatException e) {
                System.out.println();
                System.out.println("Invalid input, please enter valid option ");
                continue;
            }

            if(selection == 1){
                viewPendingRequests();
            }
            else if(selection == 2){
                displayPendingTransferById();

            }
            else if (selection == 0) {
                running = false;
            }
            else{
                System.out.println();
                System.out.println("Invalid input, please enter valid option ");
            }

        }
    }

    private void displayPendingTransferById(){
        boolean running = true;
        int rejectedStatusCode = 3;
        int approvedStatusCode = 2;

        while(running) {
            System.out.println();
            System.out.print("Please enter pending transaction Id: ");
            String userInput = scanner.nextLine();


            int pendingTransactionId = 0;
            try {
                pendingTransactionId = Integer.parseInt(userInput);

            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter valid option ");
                continue;
            }
            Transfer transferDetails = transferService.getTransferById(pendingTransactionId);

            if(transferDetails == null) {
                System.out.println("Transfer with that Id does not exist.");
            }
            else if(transferDetails.getAccount_from() != accountService.getAccountByUserId(currentUser.getUser().getId()).getAccount_id() &&
                    transferDetails.getAccount_to() != accountService.getAccountByUserId(currentUser.getUser().getId()).getAccount_id()){
                System.out.println("Transfer with that Id does not exist.");

            } else if (transferDetails.getTransfer_status_id() == approvedStatusCode || transferDetails.getTransfer_status_id() == rejectedStatusCode) {
                System.out.println("Transfer request has already been resolved.");

            } else {
                consoleService.printTransferDetails(transferDetails);
                handleAcceptOrDeclineTransaction(pendingTransactionId);
            }
            break;
        }
    }

    private void handleAcceptOrDeclineTransaction(int pendingTransactionId){

        boolean running = true;
        int approvedStatusCode = 2;
        int declinedStatusCode = 3;
        while (running){
            Transfer approvedTransfer = transferService.getTransferById(pendingTransactionId);

            if(userService.getUserByAccountId(approvedTransfer.getAccount_to()).getId() == currentUser.getUser().getId()){
                System.out.println();
                System.out.println("Waiting approval from recipient party.");
                System.out.println();
                consoleService.pause();
                break;
            }

            System.out.println();
            System.out.print("Would you like to accept transaction (Y/N)? ");
            String userInput = scanner.nextLine();

            if(userInput.equalsIgnoreCase("Y")) {
                transferService.updateTransactionStatus(approvedStatusCode, pendingTransactionId);

                Account senderAccount = accountService.getAccountByUserId(userService.getUserByAccountId(approvedTransfer.getAccount_from()).getId());
                Account receiverAccount = accountService.getAccountByUserId(userService.getUserByAccountId(approvedTransfer.getAccount_to()).getId());


                if(senderAccount.getBalance().compareTo(approvedTransfer.getAmount()) == -1){
                    transferService.updateTransactionStatus(declinedStatusCode, pendingTransactionId);
                    System.out.println("Insufficient funds, Request rejected.");
                    System.out.println();
                    consoleService.pause();
                    break;
                }
                consoleService.updateAccountBalance(approvedTransfer.getAmount(), receiverAccount.getUserId(), accountService, currentUser, userService, pendingTransactionId);
                break;

            }
            else if (userInput.equalsIgnoreCase("N")) {
                transferService.updateTransactionStatus(declinedStatusCode, pendingTransactionId);
                break;
            }
            else System.out.println("Invalid input, please try again");
        }
    }

    private void transferMenu() {


        boolean running = true;

        while(running) {
            System.out.println();
            System.out.println("---------------------------------------------");
            System.out.println("Transactions Menu");
            System.out.println("---------------------------------------------");
            System.out.println();
            System.out.println("1: View all transactions");
            System.out.println("2: View transaction by id ");
            System.out.println("0: Return to Main Menu ");
            System.out.println();
            System.out.print("Please enter an option: ");

            String userInput = scanner.nextLine();
            int selection = 0;
            try {
                selection = Integer.parseInt(userInput);

            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter valid option ");
                continue;
            }

            if(selection == 1){
                viewTransferHistory();
            }
            else if(selection == 2){
               viewTransferDetails();
               consoleService.pause();
            }
            else if (selection == 0) {
                break;
            }
            else{
                System.out.println("Invalid input, please enter valid option ");
            }

        }
    }

    private void viewCurrentBalance() {

       consoleService.printCurrentBalance(accountService);
	}

    private void viewTransferDetails(){

        boolean running = true;

        while(running) {

            System.out.println();
            System.out.print("Please enter transfer Id to see the details :");
            String userInput = scanner.nextLine();
            int transferId = 0;

            try {

                transferId = Integer.parseInt(userInput);
                Transfer transferDetails = transferService.getTransferById(transferId);

                if(transferDetails == null) {

                    System.out.println();
                    System.out.println("Transfer with that Id does not exist.");

                } else if(transferDetails.getAccount_from() != accountService.getAccountByUserId(currentUser.getUser().getId()).getAccount_id() &&
                    transferDetails.getAccount_to() != accountService.getAccountByUserId(currentUser.getUser().getId()).getAccount_id()){

                    System.out.println();
                    System.out.println("Transfer with that Id does not exist.");

                } else{

                    consoleService.printTransferDetails(transferDetails);
                }

            } catch (NumberFormatException e) {

                System.out.println();
                System.out.println("Invalid input, please enter valid option ");

            } break;

        }

    }
	private void viewTransferHistory() {
        String redColor = "\u001B[31m";
        String resetColor = "\u001B[0m";
        Account account = accountService.getAccountByUserId(currentUser.getUser().getId());
        Transfer[] transferHistory = transferService.viewTransferHistory(account.getAccount_id());
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------------------------");
        System.out.println("Transaction History");
        System.out.println("ID              From/To                      Amount         Balance         Time Of Transaction");
        System.out.println("-----------------------------------------------------------------------------------------------");
        System.out.println();

        consoleService.printReceipt(transferHistory, accountService, userService,currentUser);
        consoleService.pause();

	}

	private void viewPendingRequests() {
        int pendingStatusCode = 1;
        Account account = accountService.getAccountByUserId(currentUser.getUser().getId());
        Transfer[] pendingTransfers = transferService.getPendingTransfersByAccountId(account.getAccount_id(), pendingStatusCode);
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------------------------");
        System.out.println("Pending Transactions");
        System.out.println("ID              From/To                      Amount         Balance         Time Of Transaction");
        System.out.println("-----------------------------------------------------------------------------------------------");
        System.out.println();

        consoleService.printReceipt(pendingTransfers, accountService, userService,currentUser);
        consoleService.pause();

	}

	private void sendBucks() {

        consoleService.printOtherUsers(userService.getOtherUsers());
        int recipientId = 0;

        recipientId = consoleService.promptUserToInsertRecipientId(userService,currentUser);

        if (recipientId != 0) {

            consoleService.promptUserToInsertTransferAmount(accountService, currentUser, transferService, recipientId, userService, true);

        }

	}

	private void requestBucks() {
        consoleService.printOtherUsers(userService.getOtherUsers());
        int recipientId = 0;

      recipientId = consoleService.promptUserToInsertRecipientId(userService, currentUser);

      if (recipientId != 0) {

          consoleService.promptUserToInsertTransferAmount(accountService, currentUser, transferService, recipientId, userService, false);

      }

    }

}
