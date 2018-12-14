package gui;

import directory.SettingsManager;

import java.security.Permission;
import java.util.Set;


public class ExitChecker {

    public ExitChecker() {
        System.setSecurityManager(new ExitMonitorSecurityManager());

        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook()));
    }

    private static class ExitMonitorSecurityManager extends SecurityManager {

        @Override
        public void checkPermission(Permission perm) {
        }

        @Override
        public void checkPermission(Permission perm, Object context) {
        }

        @Override
        public void checkExit(int status) {
            ShutdownHook.EXIT_STATUS = status;
        }
    }

    private static class ShutdownHook implements Runnable {

        public static Integer EXIT_STATUS = null;

        public void run() {
            if(EXIT_STATUS != null){
                switch (EXIT_STATUS){
                    case 0:
                        break;
                    case 4:
                        break;
                    case 6:
                        break;
                    case 20:
                        break;
                    case 22:
                        break;
                    default:{
                        SettingsManager.resetPreferences();
                        break;
                    }
                }
            }
        }
    }

}