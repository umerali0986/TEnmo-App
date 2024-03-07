package com.techelevator.tenmo;

import com.techelevator.tenmo.Dto.UpdateAccountDto;
import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;
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
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
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
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
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

	private void viewCurrentBalance() {
        System.out.println(accountService.getBalance());
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        Account account = accountService.getAccountByUserId(currentUser.getUser().getId());
        Transfer[] transferHistory = transferService.viewTransferHistory(account.getAccount_id());

        for (Transfer transfer : transferHistory){
            if(transfer.getAccount_from() == accountService.getAccountByUserId(currentUser.getUser().getId()).getAccount_id()){
                System.out.println(transfer.getTransfer_id() + " Receiver: " + userService.getUserByAccountId(transfer.getAccount_to()).getUsername() + ", Amount: $" + transfer.getAmount());
            }
            else {
                System.out.println(transfer.getTransfer_id() + " Sender: " + userService.getUserByAccountId(transfer.getAccount_from()).getUsername() + ", Amount: $" + transfer.getAmount());
            }
        }
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		// TODO- move the logics around
        consoleService.printOtherUsers(userService.getOtherUsers());
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

        while(running) {
            System.out.println();
            System.out.print("Please enter the amount to send (Enter 0 to cancel): ");

            BigDecimal transferAmount;
            try {
                 transferAmount = scanner.nextBigDecimal();
            }
            catch (InputMismatchException e){
                System.out.println("Input must be a valid currency amount.");
                scanner.nextLine();
                continue;
            }

            if(transferAmount.compareTo(new BigDecimal (0.0)) == 0.0){
                break;
            }

            if(transferAmount.compareTo(new BigDecimal(0.01)) == -1){
                System.out.println("Please enter an amount greater than $0.01 ");
                continue;
            }

            if(transferAmount.scale() > 2){
                System.out.println("Transfer amount must not contain more than 2 decimal places.");
                continue;
            }

            if(transferAmount.compareTo(accountService.getBalance()) == 1){
                System.out.println("Insufficient funds");
            }
            else {
                Account senderAccount = accountService.getAccountByUserId(currentUser.getUser().getId());
                Account receiverAccount = accountService.getAccountByUserId(recipientId);
                Transfer transfer = new Transfer();


                transfer.setAmount(transferAmount);
                transfer.setAccount_from(senderAccount.getAccount_id());
                transfer.setAccount_to(receiverAccount.getAccount_id());
                transfer.setTransfer_type_id(2);
                transfer.setTransfer_status_id(2);
                transferService.createReceipt(transfer);

                UpdateAccountDto updateSenderAccount = new UpdateAccountDto();
                UpdateAccountDto updateReceiverAccount = new UpdateAccountDto();

                updateSenderAccount.setAccount(senderAccount);
                updateSenderAccount.setAmount(transferAmount);
                updateSenderAccount.setWithdaw(true);

                updateReceiverAccount.setAccount(receiverAccount);
                updateReceiverAccount.setAmount(transferAmount);
                updateReceiverAccount.setWithdaw(false);

                System.out.println("Transfer Successful");
                accountService.updateAccountBalance(updateSenderAccount);
                accountService.updateAccountBalance(updateReceiverAccount);
                System.out.println("Sender: " + currentUser.getUser().getUsername() + ", Receiver: " + userService.getUserById(recipientId).getUsername() + ", Amount: $" + transferAmount);
            }
            break;
        }

	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
