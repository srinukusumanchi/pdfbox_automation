package com.quantum.utility;

import com.jcraft.jsch.*;

import java.util.Vector;

public class FTPUtil {


    private String host = "cs1bsibq02.bns";
    private int port = 22;
    private Session session = null;

    public void connect() throws JSchException {
        JSch jsch = new JSch();

        // Uncomment the line below if the FTP server requires certificate
//        jsch.addIdentity("private-key-path);

        session = jsch.getSession(host);

        // Uncomment the two lines below if the FTP server requires password
        session = jsch.getSession("vi778", host, port);
        session.setPassword("cdic1234");

        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
    }

    public void download(String source, String destination) throws JSchException, SftpException {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;
        sftpChannel.cd(source);
        Vector filelist = sftpChannel.ls(source);
        for (int i = 0; i < filelist.size(); i++) {
            ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) filelist.get(i);
            if (entry.getFilename().contains(".txt")) {
                sftpChannel.get(entry.getFilename(), destination);
            }

            System.out.println(entry.getFilename());
        }
        sftpChannel.exit();
    }

    public void disconnect() {
        if (session != null) {
            session.disconnect();
        }
    }

    public static void main(String args[]) throws JSchException, SftpException {
        FTPUtil util = new FTPUtil();
        util.connect();
        util.download("/appdata/iwayapps2/iway2/bns/7w/app_7w_out/cdic/2022011_QAT_RFR_New", System.getProperty("user.dir") + "/resources/dqFiles");
        util.disconnect();
    }
}




