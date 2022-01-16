/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view.helper;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.regex.Pattern;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
/**
 *
 * @author hoang
 */
public class Validation {
    public static boolean checkUsername(String username) {
        return username.length() >= 5 && username.length() <= 30;
    }

    public static boolean checkPassword(String pass) {
        return pass.length() >= 6 && pass.length() <= 30;
    }


    public static String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }
    
    public static boolean checkInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void checkNumberInputChanged(JTextField numberFormatedField) {
        Runnable doAssist = new Runnable() {
            String temp = numberFormatedField.getText();//temp = 1234a

            @Override
            public void run() {
//      check if input is Integer
                if (!checkInt(temp)) {          // temp = 1234a
//      loop to remove the last char if not Integer until temp is Integer
                    while (!checkInt(temp)) {   // temp = 1234a
                        temp = temp.substring(0, temp.length() - 1);// temp = 1234
                    }
//                  Set temp as text for the textField
                    numberFormatedField.setText(temp);
                }

            }
        };
        SwingUtilities.invokeLater(doAssist);
    }
}
