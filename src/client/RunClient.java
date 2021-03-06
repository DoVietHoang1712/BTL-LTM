/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import client.controller.SocketHandler;
import client.view.helper.LookAndFeel;
import client.view.scene.ChangePassword;
import client.view.scene.InGame;
import client.view.scene.Login;
import client.view.scene.MainMenu;
import client.view.scene.Profile;
import client.view.scene.Signup;
import javax.swing.JOptionPane;


public class RunClient {

    public enum SceneName {
        LOGIN,
        SIGNUP,
        MAINMENU,
        CHANGEPASSWORD,
        INGAME,
        PROFILE
    }

    // scenes;
    public static Login loginScene;
    public static Signup signupScene;
    public static MainMenu mainMenuScene;
    public static ChangePassword changePasswordScene;
    public static InGame inGameScene;
    public static Profile profileScene;

    // controller 
    public static SocketHandler socketHandler;

    public RunClient() {
        socketHandler = new SocketHandler();
        socketHandler.connect("127.0.0.1", 12345);
    }

    public static void openScene(SceneName sceneName) {
        if (null != sceneName) {
            switch (sceneName) {
                case LOGIN:
                    loginScene = new Login();
                    loginScene.setVisible(true);
                    break;
                case SIGNUP:
                    signupScene = new Signup();
                    signupScene.setVisible(true);
                    break;
                case MAINMENU:
                    mainMenuScene = new MainMenu();
                    mainMenuScene.setUp();
                    mainMenuScene.setVisible(true);
                    break;
                case CHANGEPASSWORD:
                    changePasswordScene = new ChangePassword();
                    changePasswordScene.setVisible(true);
                    break;
                case INGAME:
                    inGameScene = new InGame();
                    inGameScene.setVisible(true);
                    break;
                case PROFILE:
                    profileScene = new Profile();
                    profileScene.setVisible(true);
                    break;
                default:
                    break;
            }
        }
    }

    public static void closeScene(SceneName sceneName) {
        if (null != sceneName) {
            switch (sceneName) {
                case LOGIN:
                    loginScene.dispose();
                    break;
                case SIGNUP:
                    signupScene.dispose();
                    break;
                case MAINMENU:
                    mainMenuScene.dispose();
                    break;
                case CHANGEPASSWORD:
                    changePasswordScene.dispose();
                    break;
                case INGAME:
                    inGameScene.dispose();
                    break;
                case PROFILE:
                    profileScene.dispose();
                    break;
                default:
                    break;
            }
        }
    }

    public static void closeAllScene() {
        if(loginScene != null) loginScene.dispose();
        if(signupScene != null) signupScene.dispose();
        if(mainMenuScene != null) mainMenuScene.dispose();
        if(changePasswordScene != null) changePasswordScene.dispose();
        if(inGameScene != null) inGameScene.dispose();
        if(profileScene != null) profileScene.dispose();
    }

    public static void main(String[] args) {
        LookAndFeel.setNimbusLookAndFeel();
        new RunClient().openScene(SceneName.LOGIN);
    }
}
