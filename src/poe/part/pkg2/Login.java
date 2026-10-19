/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package poe.part.pkg2;

/**
 *
 * @author Student
 */
public class Login {
      private String username;

    private String password;

    private String firstName;

    private String lastName;

    private String cellNumber;

    private boolean loggedIn = false;



    // Username: contains an underscore and is no more than 5 characters long

    public boolean checkUserName(String username) {

        return username != null

                && username.contains("_")

                && username.length() <= 5;

    }



    // Password: at least 8 characters, a capital letter, a number and a special character

    public boolean checkPasswordComplexity(String password) {

        return password != null

                && password.length() >= 8

                && password.matches(".*[A-Z].*")

                && password.matches(".*[0-9].*")

                && password.matches(".*[^A-Za-z0-9].*");

    }



    // Cell number: international code (+) followed by up to ten digits

    public boolean checkCellPhoneNumber(String cell) {

        return cell != null && cell.matches("^\\+[0-9]{1,3}[0-9]{9,10}$");

    }



    // Registers the user and returns a message describing the result

    public String registerUser(String firstName, String lastName, String username,

                               String password, String cellNumber) {



        if (!checkUserName(username)) {

            return "Username is not correctly formatted. Please ensure that your "

                    + "username contains an underscore and is no more than five characters in length.";

        }

        if (!checkPasswordComplexity(password)) {

            return "Password is not correctly formatted. Please ensure that the password "

                    + "contains at least eight characters, a capital letter, a number, "

                    + "and a special character.";

        }

        if (!checkCellPhoneNumber(cellNumber)) {

            return "Cell phone number is incorrectly formatted or does not contain "

                    + "an international code. Please correct the number and try again.";

        }



        this.firstName = firstName;

        this.lastName = lastName;

        this.username = username;

        this.password = password;

        this.cellNumber = cellNumber;

        return "Username successfully captured. Password successfully captured. "

                + "Cell phone number successfully added. User registered successfully.";

    }



    // Checks the entered details against the registered ones

    public boolean loginUser(String enteredUsername, String enteredPassword) {

        loggedIn = username != null

                && username.equals(enteredUsername)

                && password.equals(enteredPassword);

        return loggedIn;

    }

    public String getCellNumber() {
        return cellNumber;
    }

    public boolean returnLoginStatus() {

        return loggedIn;

    }



    public String returnLoginMessage() {

        if (loggedIn) {

            return "Welcome " + firstName + " " + lastName + ", it is great to see you again.";

        }

        return "Username or password incorrect, please try again.";

    }
}