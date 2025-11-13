package mglucas0123.autosave;

import mglucas0123.Principal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AutoRestartManager {
    
    private Principal plugin;
    private BukkitRunnable checkTask;
    private boolean restartEnabled;
    private List<String> restartTimes;
    private boolean countdownEnabled;
    private List<Integer> countdownSeconds;
    private boolean broadcastRestart;
    private DateTimeFormatter timeFormatter;
    
    public AutoRestartManager(Principal plugin) {
        this.plugin = plugin;
        this.timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        loadConfig();
    }
    
    private void loadConfig() {
        this.restartEnabled = plugin.getConfig().getBoolean("AutoRestart.Enabled", false);
        this.restartTimes = plugin.getConfig().getStringList("AutoRestart.Times");
        this.countdownEnabled = plugin.getConfig().getBoolean("AutoRestart.Countdown.Enabled", true);
        this.countdownSeconds = plugin.getConfig().getIntegerList("AutoRestart.Countdown.Seconds");
        this.broadcastRestart = plugin.getConfig().getBoolean("AutoRestart.BroadcastMessage", true);
    }
    
    public void start() {
        if (!restartEnabled || restartTimes.isEmpty()) {
            return;
        }
        
        if (checkTask != null) {
            checkTask.cancel();
        }
        
        checkTask = new BukkitRunnable() {
            @Override
            public void run() {
                checkAndRestart();
            }
        };
        
        checkTask.runTaskTimer(plugin, 20L * 60L, 20L * 60L);
        
        Bukkit.getConsoleSender().sendMessage("§a[MGZ] AutoRestart ativado! Horários: " + String.join(", ", restartTimes));
    }
    
    private void checkAndRestart() {
        String currentTime = LocalTime.now().format(timeFormatter);
        
        if (restartTimes.contains(currentTime)) {
            Bukkit.getConsoleSender().sendMessage("§e[AutoRestart] Horário de restart detectado: " + currentTime);
            performRestart();
        }
    }
    
    public void performRestart() {
        performRestart(false);
    }
    
    public void performRestart(boolean skipCountdown) {
        if (checkTask != null) {
            checkTask.cancel();
        }
        
        executeRestart(skipCountdown);
    }
    
    private void executeRestart(boolean skipCountdown) {
        if (countdownEnabled && !skipCountdown && !countdownSeconds.isEmpty()) {
            startCountdown(() -> Bukkit.getScheduler().runTask(plugin, this::runRestartSequence));
        } else {
            Bukkit.getScheduler().runTask(plugin, this::runRestartSequence);
        }
    }

    private void startCountdown(Runnable onFinish) {
        int maxCountdown = countdownSeconds.stream().max(Integer::compare).orElse(60);

        new BukkitRunnable() {
            private int seconds = maxCountdown;

            @Override
            public void run() {
                if (countdownSeconds.contains(seconds)) {
                    String message = plugin.getConfig()
                        .getString("AutoRestart.Messages.Countdown", "§c[AutoRestart] Servidor reiniciando em {SECONDS} segundos!")
                        .replace("{SECONDS}", String.valueOf(seconds));
                    Bukkit.broadcastMessage(message);
                }

                if (seconds > 5) {
                    String actionBarText = getActionBarMessage(seconds);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                            net.md_5.bungee.api.chat.TextComponent.fromLegacyText(actionBarText));
                    }
                } else if (seconds > 0) {
                    String titleText = getTitleMessage(seconds);
                    String subtitleText = getSubtitleMessage(seconds);
                    String actionBarText = getActionBarMessage(seconds);
                    
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendTitle(titleText, subtitleText, 0, 25, 5);
                        player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                            net.md_5.bungee.api.chat.TextComponent.fromLegacyText(actionBarText));
                    }
                }

                if (seconds <= 0) {
                    cancel();
                    onFinish.run();
                    return;
                }

                seconds--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private String getActionBarMessage(int seconds) {
        if (seconds > 60) {
            int minutes = seconds / 60;
            return String.format("§c§l⏰ REINÍCIO DO SERVIDOR §8» §e%d minuto%s", 
                minutes, minutes > 1 ? "s" : "");
        } else if (seconds > 30) {
            return String.format("§6§l⚠ REINÍCIO EM BREVE §8» §e%d segundos", seconds);
        } else if (seconds > 10) {
            return String.format("§e§l⏳ PREPARE-SE §8» §c%d segundos para reiniciar", seconds);
        } else if (seconds > 5) {
            return String.format("§c§l⚠ ATENÇÃO §8» §4§l%d SEGUNDOS!", seconds);
        } else {
            return String.format("§4§l⚠ REINICIANDO §8» §c§l%d", seconds);
        }
    }

    private String getTitleMessage(int seconds) {
        switch (seconds) {
            case 5:
                return "§6§l5";
            case 4:
                return "§e§l4";
            case 3:
                return "§c§l3";
            case 2:
                return "§4§l2";
            case 1:
                return "§4§l1";
            default:
                return "§4§l" + seconds;
        }
    }

    private String getSubtitleMessage(int seconds) {
        switch (seconds) {
            case 5:
            case 4:
                return "§7Salve seus itens!";
            case 3:
                return "§7Servidor reiniciando...";
            case 2:
                return "§c§lPREPARE-SE!";
            case 1:
                return "§4§lREINICIANDO AGORA!";
            default:
                return "§7Servidor reiniciando em breve";
        }
    }

    private void runRestartSequence() {
        String message = plugin.getConfig().getString("AutoRestart.Messages.Restart", "§c[AutoRestart] Servidor reiniciando agora!");

        if (broadcastRestart) {
            Bukkit.broadcastMessage(message);
        }

        Bukkit.getConsoleSender().sendMessage("§c[AutoRestart] Executando reinício do servidor...");

        Bukkit.getConsoleSender().sendMessage("§e[AutoRestart] ▶ Salvando todos os dados...");

        Bukkit.getWorlds().forEach(world -> world.setAutoSave(false));

        Bukkit.getConsoleSender().sendMessage("§e[AutoRestart]   • Salvando jogadores...");
        Bukkit.savePlayers();

        Bukkit.getConsoleSender().sendMessage("§e[AutoRestart]   • Salvando mundos...");
        for (org.bukkit.World world : Bukkit.getWorlds()) {
            Bukkit.getConsoleSender().sendMessage("§7[AutoRestart]     - Salvando: " + world.getName());
            world.save();
        }

        Bukkit.getConsoleSender().sendMessage("§e[AutoRestart]   • Salvando dados do servidor...");
        try {
            Object server = Bukkit.getServer().getClass().getMethod("getServer").invoke(Bukkit.getServer());
            server.getClass().getMethod("saveChunks", boolean.class, boolean.class, boolean.class)
                .invoke(server, false, true, false);
        } catch (Exception e) {}

        Bukkit.getConsoleSender().sendMessage("§a[AutoRestart] ✓ Todos os dados salvos com sucesso!");

        String kickMessage = plugin.getConfig().getString("AutoRestart.Messages.KickMessage",
            "§c§l⚠ SERVIDOR REINICIANDO ⚠\n\n§7O servidor está sendo reiniciado\n§7para aplicar atualizações e melhorias!\n\n§a§lVolta em alguns segundos!\n\n§e§oAguarde e reconecte em breve...");

        List<Player> playersToKick = new ArrayList<>(Bukkit.getOnlinePlayers());
        int playerCount = playersToKick.size();

        Bukkit.getConsoleSender().sendMessage("§e[AutoRestart] ▶ Desconectando " + playerCount + " jogador(es)...");

        playersToKick.forEach(player -> player.kickPlayer(kickMessage));

        Bukkit.getConsoleSender().sendMessage("§a[AutoRestart] ✓ Todos os jogadores desconectados!");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            int remainingPlayers = Bukkit.getOnlinePlayers().size();
            if (remainingPlayers > 0) {
                Bukkit.getConsoleSender().sendMessage("§e[AutoRestart] ⚠ Ainda há " + remainingPlayers + " jogador(es) online!");
                Bukkit.getConsoleSender().sendMessage("§e[AutoRestart] Aguardando mais 2 segundos...");

                Bukkit.getScheduler().runTaskLater(plugin, this::executeRestartScript, 40L);
            } else {
                executeRestartScript();
            }
        }, 40L);
    }
    
    private void executeRestartScript() {
        try {
            Bukkit.getConsoleSender().sendMessage("§e[AutoRestart] ====================");
            Bukkit.getConsoleSender().sendMessage("§e[AutoRestart] Preparando restart...");
            Bukkit.getConsoleSender().sendMessage("§e[AutoRestart] PID do servidor: " + ManagementFactory.getRuntimeMXBean().getName().split("[@]")[0]);
            
            java.util.List<String> restartCommand = getRestartCommand();
            java.io.File scriptFile = RestartScript.createScript(restartCommand);
            
            Bukkit.getConsoleSender().sendMessage("§a[AutoRestart] ✓ Script criado: " + scriptFile.getAbsolutePath());
            Bukkit.getConsoleSender().sendMessage("§e[AutoRestart] Executando script...");
            
            new ProcessBuilder(getScriptExecutor(scriptFile))
                .directory(new java.io.File("."))
                .inheritIO()
                .start();
            
            Bukkit.getConsoleSender().sendMessage("§a[AutoRestart] ✓ Script em execução!");
            Bukkit.getConsoleSender().sendMessage("§e[AutoRestart] Aguardando 1 segundo...");
            Bukkit.getConsoleSender().sendMessage("§e[AutoRestart] ====================");
            
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.getConsoleSender().sendMessage("§c[AutoRestart] DESLIGANDO SERVIDOR...");
                Bukkit.shutdown();
            }, 20L);
            
        } catch (RestartScript.PlatformNotSupportedException e) {
            Bukkit.getConsoleSender().sendMessage("§c[AutoRestart] ==================== ERRO ====================");
            Bukkit.getConsoleSender().sendMessage("§c[AutoRestart] " + e.getMessage());
            Bukkit.getConsoleSender().sendMessage("§c[AutoRestart] Sistema operacional não suportado!");
            Bukkit.getConsoleSender().sendMessage("§c[AutoRestart] =============================================");
            Bukkit.shutdown();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§c[AutoRestart] ==================== ERRO ====================");
            Bukkit.getConsoleSender().sendMessage("§c[AutoRestart] Falha ao criar script de restart!");
            Bukkit.getConsoleSender().sendMessage("§c[AutoRestart] Erro: " + e.getMessage());
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("§c[AutoRestart] =============================================");
            Bukkit.shutdown();
        }
    }
    
    public void stop() {
        if (checkTask != null) {
            checkTask.cancel();
            checkTask = null;
            Bukkit.getConsoleSender().sendMessage("§c[MGZ] AutoRestart desativado!");
        }
    }
    
    public void reload() {
        stop();
        loadConfig();
        start();
    }
    
    public boolean isEnabled() {
        return restartEnabled;
    }
    
    private List<String> getRestartCommand() {
        List<String> command = new ArrayList<>();
        
        List<String> jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        String javaPath = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        
        command.add(javaPath);
        
        for (String arg : jvmArgs) {
            if (!arg.startsWith("-agentlib") && !arg.startsWith("-javaagent")) {
                command.add(arg);
            }
        }
        
        command.add("-jar");
        
        String serverJar = System.getProperty("sun.java.command");
        if (serverJar != null && serverJar.endsWith(".jar")) {
            String jarName = serverJar.split(" ")[0];
            command.add(jarName);
            Bukkit.getConsoleSender().sendMessage("§a[AutoRestart] JAR detectado via comando: " + jarName);
        } else {
            File serverDir = new File(".").getAbsoluteFile();
            
            File defaultJar = new File(serverDir, "server.jar");
            if (defaultJar.exists()) {
                command.add("server.jar");
                Bukkit.getConsoleSender().sendMessage("§a[AutoRestart] JAR detectado: server.jar");
            } else {
                File[] jarFiles = serverDir.listFiles((dir, name) -> {
                    String lower = name.toLowerCase();
                    return (lower.contains("paper") || lower.contains("spigot") || 
                            lower.contains("purpur")) && lower.endsWith(".jar");
                });
                
                if (jarFiles != null && jarFiles.length > 0) {
                    command.add(jarFiles[0].getName());
                    Bukkit.getConsoleSender().sendMessage("§a[AutoRestart] JAR detectado: " + jarFiles[0].getName());
                } else {
                    command.add("server.jar");
                    Bukkit.getConsoleSender().sendMessage("§c[AutoRestart] AVISO: JAR não encontrado, usando 'server.jar' por padrão");
                }
            }
        }
        
        command.add("nogui");
        
        Bukkit.getConsoleSender().sendMessage("§7[AutoRestart] Comando de restart: " + String.join(" ", command));
        
        return command;
    }
    
    private List<String> getScriptExecutor(File scriptFile) {
        List<String> executor = new ArrayList<>();
        
        if (File.separatorChar == '\\') {
            executor.add("cmd");
            executor.add("/c");
            executor.add("start");
            executor.add("/min");
            executor.add(scriptFile.getAbsolutePath());
        } else {
            executor.add("sh");
            executor.add(scriptFile.getAbsolutePath());
        }
        
        return executor;
    }
}
