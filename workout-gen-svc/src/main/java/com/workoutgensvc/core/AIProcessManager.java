package com.workoutgensvc.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class AIProcessManager {
    @Value("${chat2api.path:chat2api}")
    private String chat2apiPath;
    @Value("${chat2api.command.start:start-chat2api.bat}")
    private String chat2apiCommand;
    @Value("${chat2api.command.setup:setup-chat2api.bat}")
    private String chat2apiSetupCommand;
    @Value("${chat2api.access-token:}")
    private String chat2apiAccessToken;
    private Process chat2apiProcess;

    @PostConstruct
    public void startChat2Api() throws IOException {
        Path chat2apiDir = Paths.get(chat2apiPath);

        if (!Files.exists(chat2apiDir)) {
            setup();
        }

        Path startScript = Paths.get(chat2apiCommand);
        if (!Files.exists(startScript)) {
            return;
        }

        try {
            startProcess();
            Thread.sleep(10000);
        } catch (Exception e) {
            log.error("Failed to start Chat2API: {}", e.getMessage());
        }

        log.info("Chat2Api initialization completed.");
    }

    @PreDestroy
    public void stopChat2Api() {
        if (chat2apiProcess != null && chat2apiProcess.isAlive()) {
            log.info("Terminating Chat2API process...");

            chat2apiProcess.destroy();
            killPythonProcesses();

            log.info("Chat2API process terminated.");
        } else {
            log.info("No active Chat2API process to terminate");
        }
    }

    private void startProcess() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("cmd", "/c", "start", chat2apiCommand);
        processBuilder.redirectErrorStream(true);

        if (chat2apiAccessToken != null && !chat2apiAccessToken.trim().isEmpty()) {
            processBuilder.environment().put("CHAT2API_ACCESS_TOKEN", chat2apiAccessToken);
        }

        chat2apiProcess = processBuilder.start();
    }

    private void setup() throws IOException {
        Path setupScript = Paths.get(chat2apiSetupCommand);
        if (!Files.exists(setupScript)) {
            throw new IOException("Setup script not found: " + chat2apiSetupCommand);
        }

        ProcessBuilder setupProcess = new ProcessBuilder();
        setupProcess.command("cmd", "/c", "start", setupScript.toString());
        setupProcess.start();

        Path appPy = Paths.get("chat2api/app.py");
        long startTime = System.currentTimeMillis();
        long timeoutMs = 60000;

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (Files.exists(appPy)) {
                log.info("Setup completed successfully - found app.py");
                return;
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Setup wait was interrupted");
            }
        }

        throw new IOException("Setup timeout - app.py not found after 1 minute. Setup may have failed.");
    }

    private void killPythonProcesses() {
        try {
            new ProcessBuilder("taskkill", "/F", "/IM", "python.exe", "/T").start();
        } catch (Exception e) {
            log.debug("Could not kill python processes: {}", e.getMessage());
        }
    }
}