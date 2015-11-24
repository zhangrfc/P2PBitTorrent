package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.awt.Font;

import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import JvodInfrastructure.Handlers.ResponseHandler;
import JvodInfrastructure.PackageServers.PackageClient;
import JvoidInfrastructure.constData;
import JvodInfrastructure.Datas.*;
import JvodInfrastructure.Datas.Package;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.UnknownHostException;

public class WelcomeFrame extends JFrame
{

  private JPanel contentPane;
  private JTextField iptextField;

  /**
   * Launch the application.
   */
  public static void main(String[] args)
  {
    EventQueue.invokeLater(new Runnable()
    {
      public void run()
      {
        try
        {
          WelcomeFrame frame = new WelcomeFrame();
          frame.setVisible(true);
        }
        catch( Exception e )
        {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the frame.
   */
  public WelcomeFrame()
  {
    setBackground(Color.LIGHT_GRAY);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 1280, 720);
    contentPane = new JPanel();
    contentPane.setBackground(Color.WHITE);
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(null);
    
    JLabel logoLabel = new JLabel("");
    logoLabel.setIcon(new ImageIcon("D:\\JavaWorkspace\\SeverGui\\src\\Logo.PNG"));
    logoLabel.setBounds(10, 10, 325, 105);
    contentPane.add(logoLabel);
    
    JLabel serverLabel = new JLabel("Server IP:");
    serverLabel.setFont(new Font("Calibri", Font.BOLD, 30));
    serverLabel.setBounds(359, 293, 136, 50);
    contentPane.add(serverLabel);
    
    iptextField = new JTextField();
    iptextField.setBackground(Color.WHITE);
    iptextField.setFont(new Font("Calibri", Font.BOLD, 30));
    iptextField.setBounds(505, 294, 354, 50);
    contentPane.add(iptextField);
    iptextField.setColumns(10);
    iptextField.setText("35.2.73.235");
    
    JButton connectBtn = new JButton("Connect");
    connectBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String ipAddress = iptextField.getText().toString();
        ResponseHandler newRH;
        try
        {
          newRH = new ResponseHandler(constData.filePath, constData.peerPort);
          PackageClient pc = new PackageClient(ipAddress, constData.trackerPort, newRH);
          Package P = newRH.startProgram();
          pc.sendPackage(P);
          System.out.println("finish");
          DownloadFrame window = new DownloadFrame(newRH, pc);
          window.run();
          dispose();
        }
        catch( UnknownHostException e1 )
        {
          e1.printStackTrace();
        }
        catch( IOException e1 )
        {
          e1.printStackTrace();
        }
        catch( Exception e1 )
        {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        
      }
    });
    connectBtn.setFont(new Font("Calibri", Font.BOLD, 30));
    connectBtn.setBounds(548, 387, 136, 62);
    contentPane.add(connectBtn);
  }
}
