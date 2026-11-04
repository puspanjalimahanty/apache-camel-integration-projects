package com.example.sftprest.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddedSftpServerConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public SshServer embeddedSftpServer() throws IOException {

    	Path sftpRoot = Paths.get("C:/CamelProject/sftp-server").toAbsolutePath();

    	Files.createDirectories(sftpRoot);
    	Files.createDirectories(sftpRoot.resolve("incoming"));
    	Files.createDirectories(sftpRoot.resolve(".processed"));
    	Files.createDirectories(sftpRoot.resolve(".error"));
        Path incoming = sftpRoot.resolve("incoming");
        Files.createDirectories(incoming);

        SshServer sshServer = SshServer.setUpDefaultServer();

        sshServer.setHost("localhost");
        sshServer.setPort(2222);

        sshServer.setKeyPairProvider(
                new SimpleGeneratorHostKeyProvider(
                        sftpRoot.resolve("hostkey.ser")));

        sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {

            @Override
            public boolean authenticate(
                    String username,
                    String password,
                    org.apache.sshd.server.session.ServerSession session) {

                return "cameluser".equals(username)
                        && "camel123".equals(password);
            }
        });
        

        sshServer.setSubsystemFactories(
                java.util.List.of(new SftpSubsystemFactory.Builder().build()));

        sshServer.setFileSystemFactory(
                new VirtualFileSystemFactory(sftpRoot));

        return sshServer;
    }
}