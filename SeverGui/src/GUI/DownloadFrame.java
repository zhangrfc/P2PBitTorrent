package GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;

import JvodInfrastructure.Datas.*;
import JvodInfrastructure.Datas.Package;

import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.ListCellRenderer;

import JvodInfrastructure.Handlers.ResponseHandler;
import JvodInfrastructure.PackageServers.PackageClient;
import JvoidInfrastructure.*;


public class DownloadFrame extends JFrame
{

  /**
   * Launch the application.
   */
  public void run()
  {
    try
    {
      
      //setExtendedState(JFrame.MAXIMIZED_BOTH);
      setVisible(true);
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }


  /**
   * Initialize the contents of the frame.
   */
  DownloadFrame(ResponseHandler newRH, PackageClient pc)
  {
    getContentPane().setBackground(Color.WHITE);
    setBounds(100, 100, 1280, 720);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    getContentPane().setLayout(null);
    
    JLabel logoLabel = new JLabel("");
    logoLabel.setIcon(new ImageIcon("D:\\JavaWorkspace\\SeverGui\\src\\Logo.PNG"));
    logoLabel.setBounds(10, 10, 325, 105);
    getContentPane().add(logoLabel);
    
    JLabel progressLabel = new JLabel("Download Progress");
    progressLabel.setFont(new Font("Calibri", Font.BOLD, 30));
    progressLabel.setBounds(26, 125, 248, 37);
    getContentPane().add(progressLabel);
    
    String[] items = {"Torrent \t \t \t \t \t \t \t \t \t \t \t \t \t Peer"};
    JList progressList = new JList(items);
    progressList.setBackground(Color.LIGHT_GRAY);
    progressList.setBounds(10, 172, 849, 359);
    TabListCellRenderer renderer = new TabListCellRenderer();
    progressList.setCellRenderer(renderer);
    getContentPane().add(progressList);
    
    JButton btnMakeTorrent = new JButton("Make Torrent");
    btnMakeTorrent.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser(new File(constData.defaultFile));
        int chooserReturn;
        File chosenFile;
        chooserReturn = fileChooser.showOpenDialog(DownloadFrame.this);
        fileChooser.setDialogTitle("Choose a file to create torrent");
        getContentPane().add(fileChooser);
        fileChooser.setVisible(true);
        if(chooserReturn == JFileChooser.APPROVE_OPTION)
        {
          chosenFile = fileChooser.getSelectedFile();
          System.out.println(chosenFile.getParentFile());
          System.out.println(constData.defaultFile);
          if((chosenFile.getParentFile().toString()).equals(constData.defaultFile))
          {
            //System.out.println(chosenFile.getName());
            try
            {
              Package P = newRH.register(chosenFile.getName());
              pc.sendPackage(P);
              fileChooser.setVisible(false);
             
            }
            catch( IOException e1 )
            {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
            catch( Exception e1 )
            {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
          else
          {
            JDialog d = new JDialog(DownloadFrame.this, "Path Error", true);
            d.setLayout(new BorderLayout());
            JLabel errorinfo = new JLabel("Please specify your path to :" + constData.filePath);
            d.add(errorinfo, BorderLayout.CENTER);
            d.pack();
            d.setLocationRelativeTo(DownloadFrame.this);
            d.setVisible(true);
          }
        }
        
        System.out.println("File");
      }
    });
    btnMakeTorrent.setFont(new Font("Calibri", Font.BOLD, 20));
    btnMakeTorrent.setBounds(952, 81, 185, 47);
    getContentPane().add(btnMakeTorrent);
    
    JButton btnDownload = new JButton("Download");
    btnDownload.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
        
        JFileChooser fileChooser = new JFileChooser(new File(constData.defaultTorrentPath));
        int chooserReturn;
        File chosenFile;
        chooserReturn = fileChooser.showOpenDialog(DownloadFrame.this);
        fileChooser.setDialogTitle("Choose a torrent to start download");
        getContentPane().add(fileChooser);
        fileChooser.setVisible(true);
        if(chooserReturn == JFileChooser.APPROVE_OPTION)
        {
          chosenFile = fileChooser.getSelectedFile();
          ThreadDownloadProgram T = new ThreadDownloadProgram(newRH, pc, chosenFile);
          T.start();
          
          fileChooser.setVisible(false);
          
        }
        
      }
    });
    btnDownload.setFont(new Font("Calibri", Font.BOLD, 20));
    btnDownload.setBounds(952, 172, 185, 47);
    getContentPane().add(btnDownload);
    
    JButton btnExit = new JButton("Exit");
    btnExit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ThreadEndProgram T = new ThreadEndProgram(newRH, pc);
        T.start();
        synchronized(T){
          try
          {
            T.wait();
          }
          catch( InterruptedException e1 )
          {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
        }
        
        if (newRH.checkEnd()){
          System.exit(0);
        } else {
          JDialog d = new JDialog(DownloadFrame.this, "Close Error", true);
          d.setLayout(new BorderLayout());
          JLabel errorinfo = new JLabel("Server pending, please try again");
          d.add(errorinfo, BorderLayout.CENTER);
          d.pack();
          d.setLocationRelativeTo(DownloadFrame.this);
          d.setVisible(true);
        }
        
      }
    });
    btnExit.setFont(new Font("Calibri", Font.BOLD, 20));
    btnExit.setBounds(952, 261, 185, 47);
    getContentPane().add(btnExit);
    
    setVisible(true);
  }
}

class ThreadEndProgram extends Thread{
  ResponseHandler newRH;
  PackageClient pc;
  
  ThreadEndProgram(ResponseHandler _newRH, PackageClient _pc){
    newRH = _newRH;
    pc = _pc;
  }
  
  public void run(){
    synchronized(this){
      Package P = newRH.endProgram();
      try
      {
        pc.sendPackage(P);
      }
      catch( IOException e1 )
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      catch( Exception e )
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      notify();
    }
  }
}

class ThreadDownloadProgram extends Thread{
  ResponseHandler newRH;
  PackageClient pc;
  File chosenFile;
  
  ThreadDownloadProgram(ResponseHandler _newRH, PackageClient _pc,File _chosenFile){
    newRH = _newRH;
    pc = _pc;
    chosenFile = _chosenFile;
  }
  
  public void run(){
    synchronized(this){
      Torrent torrent;
      try
      {
        torrent = newRH.readTorrentFle(chosenFile.getAbsolutePath());
        Package P = newRH.getPeer(torrent);
        pc.sendPackage(P);
      }
      catch( IOException e )
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch( Exception e )
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      notify();
    }
  }
}

class TabListCellRenderer extends JLabel implements ListCellRenderer {
  protected static Border m_noFocusBorder = new EmptyBorder(1, 1, 1, 1);

  protected FontMetrics m_fm = null;

  public TabListCellRenderer() {
    super();
    setOpaque(true);
    setBorder(m_noFocusBorder);
  }

  public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean isSelected, boolean cellHasFocus) {
    setText(value.toString());

    setBackground(isSelected ? list.getSelectionBackground() : list
        .getBackground());
    setForeground(isSelected ? list.getSelectionForeground() : list
        .getForeground());

    setFont(list.getFont());
    setBorder((cellHasFocus) ? UIManager
        .getBorder("List.focusCellHighlightBorder") : m_noFocusBorder);

    return this;
  }

  public void paint(Graphics g) {
    m_fm = g.getFontMetrics();

    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());
    getBorder().paintBorder(this, g, 0, 0, getWidth(), getHeight());

    g.setColor(getForeground());
    g.setFont(getFont());
    Insets insets = getInsets();
    int x = insets.left;
    int y = insets.top + m_fm.getAscent();

    StringTokenizer st = new StringTokenizer(getText(), "\t");
    while (st.hasMoreTokens()) {
      String str = st.nextToken();
      g.drawString(str, x, y);
      //insert distance for each tab
      x += m_fm.stringWidth(str) + 50;

      if (!st.hasMoreTokens())
        break;
    }
  }
}
