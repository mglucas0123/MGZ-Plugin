package mglucas0123.autosave;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class RestartScript {
    
    public static File createScript(List<String> command) throws IOException {
        List<String> cmdList = new ArrayList<>(command);
        
        if (isUnix()) {
            return createUnixRestartScript(cmdList);
        } else if (isWindows()) {
            return createWindowsRestartScript(cmdList);
        } else {
            throw new PlatformNotSupportedException("Sistema operacional não suportado para auto-restart");
        }
    }
    
    private static File createUnixRestartScript(List<String> command) throws IOException {
        File scriptFile = new File("mgz_restart_script.sh");
        scriptFile.createNewFile();
        
        try (PrintStream ps = new PrintStream(new FileOutputStream(scriptFile))) {
            String readableCommand = String.join(" ", command);
            String commandLine = buildCommandLine(command, false);

            ps.println("#!/bin/bash");
            ps.println("echo \"[MGZ AutoRestart] Aguardando servidor desligar...\"");
            ps.println("trap : SIGHUP");
            ps.println("while [ -d /proc/" + getPID() + " ]; do");
            ps.println("sleep 1");
            ps.println("done");
            ps.println("echo \"[MGZ AutoRestart] Servidor desligado! Iniciando em 3 segundos...\"");
            ps.println("sleep 3");
            ps.println("rm \"" + scriptFile.getAbsolutePath() + "\"");
            ps.println("echo \"[MGZ AutoRestart] Executando: " + readableCommand + "\"");
            ps.println(commandLine);
        }
        
        scriptFile.setExecutable(true);
        return scriptFile;
    }
    
    private static File createWindowsRestartScript(List<String> command) throws IOException {
        File scriptFile = new File("mgz_restart_script.bat");
        scriptFile.createNewFile();
        
        try (PrintStream ps = new PrintStream(new FileOutputStream(scriptFile))) {
            String readableCommand = String.join(" ", command);
            String commandLine = buildCommandLine(command, true);

            ps.println("@echo off");
            ps.println("echo [MGZ AutoRestart] Aguardando servidor desligar...");
            ps.println(":loop");
            ps.println("tasklist | find \"" + getPID() + " \" >nul");
            ps.println("if not errorlevel 1 (");
            ps.println("timeout /t 1 >nul");
            ps.println("goto :loop");
            ps.println(")");
            ps.println("echo [MGZ AutoRestart] Servidor desligado! Iniciando em 3 segundos...");
            ps.println("timeout /t 3 >nul");
            ps.println("del \"" + scriptFile.getAbsolutePath() + "\"");
            ps.println("echo [MGZ AutoRestart] Executando: " + readableCommand);
            ps.println(commandLine);
        }
        
        scriptFile.setExecutable(true);
        return scriptFile;
    }
    
    private static boolean isUnix() {
        return File.separatorChar == '/';
    }
    
    private static boolean isWindows() {
        return File.separatorChar == '\\';
    }
    
    private static String getPID() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("[@]")[0];
    }

    private static String buildCommandLine(List<String> command, boolean windows) {
        StringBuilder builder = new StringBuilder();
        for (String arg : command) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(windows ? quoteWindows(arg) : quoteUnix(arg));
        }
        return builder.toString();
    }

    private static String quoteUnix(String arg) {
        if (arg.isEmpty()) {
            return "''";
        }
        return "'" + arg.replace("'", "'\"'\"'") + "'";
    }

    private static String quoteWindows(String arg) {
        String escaped = arg
            .replace("\\", "\\\\")
            .replace("\"", "\\\"");
        return "\"" + escaped + "\"";
    }
    
    public static class PlatformNotSupportedException extends RuntimeException {
        public PlatformNotSupportedException(String message) {
            super(message);
        }
    }
}
