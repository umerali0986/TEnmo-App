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

    // TODO- delete scanner after implementation
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
            } else if (menuSelection == 2) {
                transferMenu();
            } else if (menuSelection == 3) {
                //viewPendingRequests();
                pendingMenu();
            } else if (menuSelection == 4) {
                sendBucks();
                scanner.nextLine();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
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
        //TODO once updated transaction can not be changed again
        boolean running = true;
        int approvedStatusCode = 2;
        int declinedStatusCode = 3;
        while (running){
            Transfer approvedTransfer = transferService.getTransferById(pendingTransactionId);

            if(userService.getUserByAccountId(approvedTransfer.getAccount_to()).getId() == currentUser.getUser().getId()){
                System.out.println();
                System.out.println("Waiting approval from recipient party.");
                break;
            }

            System.out.println();
            System.out.print("Would you like to accept transaction (Y/N)? ");
            String userInput = scanner.nextLine();

            if(userInput.equalsIgnoreCase("Y")) {
                transferService.updateTransactionStatus(approvedStatusCode, pendingTransactionId);
                //TODO update balance
                //TODO Begin here tomorrow
                //TODO add backend transfer amount by transfer id


                Account senderAccount = accountService.getAccountByUserId(userService.getUserByAccountId(approvedTransfer.getAccount_from()).getId());
                Account receiverAccount = accountService.getAccountByUserId(userService.getUserByAccountId(approvedTransfer.getAccount_to()).getId());

                if(senderAccount.getBalance().compareTo(approvedTransfer.getAmount()) == -1){
                    transferService.updateTransactionStatus(declinedStatusCode, pendingTransactionId);
                    System.out.println("Insufficient funds, Request rejected.");
                    break;
                }
                consoleService.updateAccountBalance(approvedTransfer.getAmount(), receiverAccount.getUserId(), accountService, currentUser, userService);
//                UpdateAccountDto updateSenderAccount = new UpdateAccountDto();
//                UpdateAccountDto updateReceiverAccount = new UpdateAccountDto();
//
//                updateSenderAccount.setAccount(senderAccount);
//                updateSenderAccount.setAmount(approvedTransfer.getAmount());
//                updateSenderAccount.setWithdaw(true);
//
//                updateReceiverAccount.setAccount(receiverAccount);
//                updateReceiverAccount.setAmount(approvedTransfer.getAmount());
//                updateReceiverAccount.setWithdaw(false);
//
//                System.out.println("Transfer Successful");
//                accountService.updateAccountBalance(updateSenderAccount);
//                accountService.updateAccountBalance(updateReceiverAccount);
//                System.out.println("Sender: " + currentUser.getUser().getUsername() + ", Receiver: " + userService.getUserById(approvedTransfer.getAccount_to()).getUsername() + ", Amount: $" + approvedTransfer.getAmount());

            }
            else if (userInput.equalsIgnoreCase("N")) {
                transferService.updateTransactionStatus(declinedStatusCode, pendingTransactionId);
            }
            break;
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
               scanner.nextLine();
            }
            else if (selection == 0) {
                running = false;
                break;
            }
            else{
                System.out.println("Invalid input, please enter valid option ");
            }

        }
    }

    // move this method's logic to console service class, and create printCurrentBalance() method
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

            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter valid option ");
                continue;
            }

            Transfer transferDetails = transferService.getTransferById(transferId);

            if(transferDetails == null) {
                System.out.println("Transfer with that Id does not exist.");
            }
            else if(transferDetails.getAccount_from() != accountService.getAccountByUserId(currentUser.getUser().getId()).getAccount_id() &&
                    transferDetails.getAccount_to() != accountService.getAccountByUserId(currentUser.getUser().getId()).getAccount_id()){
                System.out.println("Transfer with that Id does not exist.");

            }
            else{

                consoleService.printTransferDetails(transferDetails);
            }
                break;
        }
        }
	private void viewTransferHistory() {
        String redColor = "\u001B[31m";
        String resetColor = "\u001B[0m";
        Account account = accountService.getAccountByUserId(currentUser.getUser().getId());
        Transfer[] transferHistory = transferService.viewTransferHistory(account.getAccount_id());
        System.out.println();
        System.out.println("---------------------------------------------");
        System.out.println("Transaction History");
        //TODO - Continue improving visual formatting, Add Balance, Add isPendingStatus, Add Timestamps
        System.out.println("ID          From/To                   Amount");
        System.out.println("---------------------------------------------");
        System.out.println();

        //TODO include $ in color coding, add "-" to withdraws, add alignment padding based on length of negative amount value

        consoleService.printReceipt(transferHistory, accountService, userService,currentUser);

//        for (Transfer transfer : transferHistory){
//            String amount;
//            //this is the sender
//            if(transfer.getAccount_from() == accountService.getAccountByUserId(currentUser.getUser().getId()).getAccount_id()) {
////                amount = redColor + "" + transfer.getAmount() + resetColor;
////                displayTransferHistoryTableRow(transfer.getTransfer_id(), "To:", userService.getUserByAccountId(transfer.getAccount_to()).getUsername(), amount);
//                 System.out.println(transfer.getTransfer_id() + " Receiver: " + userService.getUserByAccountId(transfer.getAccount_to()).getUsername() + ", Amount: $" + transfer.getAmount() + ", balance: $" + transfer.getCurrentAccountFromBalance() + " " + transfer.getTransactionDate());
//            }
//            //This is the receiver
//            else {
//                //amount = "" + transfer.getAmount();
//                //displayTransferHistoryTableRow(transfer.getTransfer_id(), "From:", userService.getUserByAccountId(transfer.getAccount_from()).getUsername(), amount);
//                System.out.println(transfer.getTransfer_id() + " Sender: " + userService.getUserByAccountId(transfer.getAccount_from()).getUsername() + ", Amount: $" + transfer.getAmount() + ", balance: $" + transfer.getCurrentAccountToBalance() + transfer.getTransactionDate());
//            }
//        }
	}

	private void viewPendingRequests() {
        // TODO- make sure the pending transfer requests are in correct order when printing it.
        int pendingStatusCode = 1;
        Account account = accountService.getAccountByUserId(currentUser.getUser().getId());
        Transfer[] pendingTransfers = transferService.getPendingTransfersByAccountId(account.getAccount_id(), pendingStatusCode);
        System.out.println();
        System.out.println("-------------------------------------------");
        System.out.println("Pending Transactions");
        //TODO - Continue improving visual formatting, Add Balance, Add isPendingStatus
        System.out.println("ID     From/To       Amount");
        System.out.println("-------------------------------------------");
        System.out.println();



        consoleService.printReceipt(pendingTransfers, accountService, userService,currentUser);

//        for (Transfer transfer : pendingTransfers){
//            if(transfer.getAccount_from() == accountService.getAccountByUserId(currentUser.getUser().getId()).getAccount_id()){
//                System.out.println(transfer.getTransfer_id() + " To: " + userService.getUserByAccountId(transfer.getAccount_to()).getUsername() + ", Amount: $" + transfer.getAmount());
//            }
//            else {
//                System.out.println(transfer.getTransfer_id() + " From: " + userService.getUserByAccountId(transfer.getAccount_from()).getUsername() + ", Amount: $" + transfer.getAmount());
//            }
//        }
	}

	private void sendBucks() {
		// TODO- move the logics around
        consoleService.printOtherUsers(userService.getOtherUsers());
        boolean running = true;
        int recipientId = 0;

        recipientId = consoleService.promptUserToInsertRecipientId(userService,currentUser);

//        while(running) {
//            System.out.println();
//            System.out.print("Please select id for recipient (Enter 0 to cancel): ");
//            String userInput = scanner.nextLine();
//
//
//            try {
//                 recipientId = Integer.parseInt(userInput);
//                 if(recipientId == 0){
//                     running = false;
//                     break;
//                 }
//
//            } catch (NumberFormatException e) {
//                System.out.println("Invalid input for recipient id, please enter valid id");
//                continue;
//            }
//
//            if(userService.getUserById(recipientId) == null){
//                System.out.println("Recipient id is invalid");
//                continue;
//            }
//            else if(currentUser.getUser().getId() == recipientId){
//                System.out.println("You can't transfer money to your own account.");
//                continue;
//            }
//
//            break;
//        }

//        while(running) {
//            System.out.println();
//            System.out.print("Please enter the amount to send (Enter 0 to cancel): ");
//
//            BigDecimal transferAmount;
//            try {
//                 transferAmount = scanner.nextBigDecimal();
//            }
//            catch (InputMismatchException e){
//                System.out.println("Input must be a valid currency amount.");
//                scanner.nextLine();
//                continue;
//            }
//
//            if(transferAmount.compareTo(new BigDecimal (0.0)) == 0.0){
//                break;
//            }
//
//            if(transferAmount.compareTo(new BigDecimal(0.01)) == -1){
//                System.out.println("Please enter an amount greater than $0.01 ");
//                continue;
//            }
//
//            if(transferAmount.scale() > 2){
//                System.out.println("Transfer amount must not contain more than 2 decimal places.");
//                continue;
//            }
//
//            if(transferAmount.compareTo(accountService.getBalance()) == 1){
//                System.out.println("Insufficient funds");
//            }
//            else {
//                Account senderAccount = accountService.getAccountByUserId(currentUser.getUser().getId());
//                Account receiverAccount = accountService.getAccountByUserId(recipientId);
//                //recipientId
//                Transfer transfer = new Transfer();
//
//
//                transfer.setAmount(transferAmount);
//                transfer.setAccount_from(senderAccount.getAccount_id());
//                transfer.setAccount_to(receiverAccount.getAccount_id());
//                transfer.setTransfer_type_id(2);
//                transfer.setTransfer_status_id(2);
//                transferService.createReceipt(transfer);

               BigDecimal transferAmount = consoleService.promptUserToInsertTransferAmount(accountService,currentUser,transferService,recipientId, userService, true);

//               updateAccountBalance(transferAmount, recipientId);
//                Account senderAccount = accountService.getAccountByUserId(currentUser.getUser().getId());
//                Account receiverAccount = accountService.getAccountByUserId(recipientId);
//
//                UpdateAccountDto updateSenderAccount = new UpdateAccountDto();
//                UpdateAccountDto updateReceiverAccount = new UpdateAccountDto();
//
//                updateSenderAccount.setAccount(senderAccount);
//                updateSenderAccount.setAmount(transferAmount);
//                updateSenderAccount.setWithdaw(true);
//
//                updateReceiverAccount.setAccount(receiverAccount);
//                updateReceiverAccount.setAmount(transferAmount);
//                updateReceiverAccount.setWithdaw(false);
//
//                System.out.println("Transfer Successful");
//                accountService.updateAccountBalance(updateSenderAccount);
//                accountService.updateAccountBalance(updateReceiverAccount);
//                System.out.println("From: " + currentUser.getUser().getUsername() + ", To: " + userService.getUserById(recipientId).getUsername() + ", Amount: $" + transferAmount);
           // }
//            break;
//        }

	}

//    public void updateAccountBalance(BigDecimal transferAmount, int recipientId){
//
//        Account senderAccount = accountService.getAccountByUserId(currentUser.getUser().getId());
//        Account receiverAccount = accountService.getAccountByUserId(recipientId);
//
//        UpdateAccountDto updateSenderAccount = new UpdateAccountDto();
//        UpdateAccountDto updateReceiverAccount = new UpdateAccountDto();
//
//        updateSenderAccount.setAccount(senderAccount);
//        updateSenderAccount.setAmount(transferAmount);
//        updateSenderAccount.setWithdaw(true);
//
//        updateReceiverAccount.setAccount(receiverAccount);
//        updateReceiverAccount.setAmount(transferAmount);
//        updateReceiverAccount.setWithdaw(false);
//
//        System.out.println("Transfer Successful");
//        accountService.updateAccountBalance(updateSenderAccount);
//        accountService.updateAccountBalance(updateReceiverAccount);
//        System.out.println("From: " + currentUser.getUser().getUsername() + ", To: " + userService.getUserById(recipientId).getUsername() + ", Amount: $" + transferAmount);
//    }

	private void requestBucks() {
		// TODO stole logic from Send method, simplify into 1 method
        consoleService.printOtherUsers(userService.getOtherUsers());
        boolean running = true;
        int recipientId = 0;

        // create promptUserToInsertRecipientId() method in console service that return recipientId

      recipientId = consoleService.promptUserToInsertRecipientId(userService, currentUser);

//        while(running) {
//            System.out.println();
//            System.out.print("Please select id for recipient (Enter 0 to cancel): ");
//            String userInput = scanner.nextLine();
//
//
//            try {
//                recipientId = Integer.parseInt(userInput);
//                if(recipientId == 0){
//                    running = false;
//                    break;
//                }
//
//            } catch (NumberFormatException e) {
//                System.out.println("Invalid input for recipient id, please enter valid id");
//                continue;
//            }
//
//            if(userService.getUserById(recipientId) == null){
//                System.out.println("Recipient id is invalid");
//                continue;
//            }
//            else if(currentUser.getUser().getId() == recipientId){
//                System.out.println("You can't transfer money to your own account.");
//                continue;
//            }
//
//            break;
//        }

        // create promptUserToInsertTransferAmount() method in console service that return amount

        consoleService.promptUserToInsertTransferAmount(accountService, currentUser, transferService,recipientId, userService, false);
//        while(running) {
//            System.out.println();
//            System.out.print("Please enter the amount to send (Enter 0 to cancel): ");
//
//            BigDecimal transferAmount;
//            try {
//                transferAmount = scanner.nextBigDecimal();
//            } catch (InputMismatchException e) {
//                System.out.println("Input must be a valid currency amount.");
//                scanner.nextLine();
//                continue;
//            }
//
//            if (transferAmount.compareTo(new BigDecimal(0.0)) == 0.0) {
//                break;
//            }
//
//            if (transferAmount.compareTo(new BigDecimal(0.01)) == -1) {
//                System.out.println("Please enter an amount greater than $0.01 ");
//                continue;
//            }
//
//            if (transferAmount.scale() > 2) {
//                System.out.println("Transfer amount must not contain more than 2 decimal places.");
//                continue;
//            } else {
//
//
//                Account senderAccount = accountService.getAccountByUserId(recipientId);
//                Account receiverAccount = accountService.getAccountByUserId(currentUser.getUser().getId());
//
//                Transfer transfer = new Transfer();
//
//
//                transfer.setAmount(transferAmount);
//                transfer.setAccount_from(senderAccount.getAccount_id());
//                transfer.setAccount_to(receiverAccount.getAccount_id());
//                transfer.setTransfer_type_id(1);
//                transfer.setTransfer_status_id(1);
//                transferService.createReceipt(transfer);
//
//            }
//            break;
//
//        }

	}

    //Used to format printed table of Transfer History
    private static void displayTransferHistoryTableRow(int transferId, String toFrom, String name, String colorCode, String amount) {
        System.out.printf("%-16d%-6s%-20s%1s%10s%n", transferId, toFrom, name, "$" + amount);
    }
//    3012 From: devin, Amount: $100.00, balance: $900.00, Date = 2024-03-09 09:05:22.639

}
