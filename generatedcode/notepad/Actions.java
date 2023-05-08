public class Actions {
  private int returnVal;

  private int option;

  private String fileContent = null;

  private String fileName = null;

  private JFileChooser jfc = new JFileChooser(".");

  private ExampleFileFilter filter = new ExampleFileFilter();

  Notepad n;

  public Fonts font = new Fonts();

  public Actions(Notepad n) {
    this.n = n;
  }

  public void neW() {
    if (!n.getTextArea().getText().equals("") && !n.getTextArea().getText().equals(fileContent)) {
      if (fileName == null) {
        option = JOptionPane.showConfirmDialog(null, "Do you want to save the changes ??");
        if (option == 0) {
          saveAs();
          n.getTextArea().setText("");
        }
        if (option == 1) {
          n.getTextArea().setText("");
        }
      } else {
        option = JOptionPane.showConfirmDialog(null, "Do you want to save the changes ??");
        if (option == 0) {
          save();
          n.getTextArea().setText("");
        }
        if (option == 1) {
          n.getTextArea().setText("");
        }
      }
    } else {
      n.getTextArea().setText("");
    }
    n.setTitle("Notepad");
  }

  public void opeN() {
    if (!n.getTextArea().getText().equals("") && !n.getTextArea().getText().equals(fileContent)) {
      if (fileName == null) {
        option = JOptionPane.showConfirmDialog(null, "Do you want to save the changes ??");
        if (option == 0) {
          saveAs();
          open();
        }
        if (option == 1) {
          open();
        }
      } else {
        option = JOptionPane.showConfirmDialog(null, "Do you want to save the changes ??");
        if (option == 0) {
          save();
          open();
        }
        if (option == 1) {
          open();
        }
      }
    } else {
      open();
    }
  }

  public void savE() {
    if (fileName == null) {
      saveAs();
    } else {
      save();
    }
  }

  public void prinT() {
    Print.printComponent(n.getTextArea());
  }

  public void exiT() {
    if (!n.getTextArea().getText().equals("") && !n.getTextArea().getText().equals(fileContent)) {
      if (fileName == null) {
        option = JOptionPane.showConfirmDialog(null, "Do you want to save the changes ??");
        if (option == 0) {
          saveAs();
          System.exit(0);
        }
        if (option == 1) {
          System.exit(0);
        }
      } else {
        option = JOptionPane.showConfirmDialog(null, "Do you want to save the changes ??");
        if (option == 0) {
          save();
          System.exit(0);
        }
        if (option == 1) {
          System.exit(0);
        }
      }
    } else {
      System.exit(0);
    }
  }

  public void selectALL() {
    n.getTextArea().selectAll();
  }

  public void lineWraP() {
    if (n.getLineWrap().isSelected()) {
      n.getTextArea().setLineWrap(true);
      n.getTextArea().setWrapStyleWord(true);
    } else {
      n.getTextArea().setLineWrap(false);
      n.getTextArea().setWrapStyleWord(false);
    }
  }

  public void fonT() {
    font.setVisible(true);
    font.pack();
    font.getOkjb().addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        n.getTextArea().setFont(font.font());
        font.setVisible(false);
      }
    });
    font.getCajb().addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        font.setVisible(false);
      }
    });
  }

  public void abouT() {
    JOptionPane.showMessageDialog(null, new About(), "About Notepad", JOptionPane.PLAIN_MESSAGE);
  }

  public void open() {
    filter.addExtension("txt");
    filter.setDescription("TXT Documents");
    jfc.setFileFilter(filter);
    returnVal = jfc.showOpenDialog(n);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      n.getTextArea().setText(null);
      try {
        fileName = jfc.getSelectedFile().getPath();
        Reader in = new FileReader(jfc.getSelectedFile());
        char[] buff = new char[100000];
        int nch;
        while ((nch = in.read(buff, 0, buff.length)) != -1)
          n.getTextArea().append(new String(buff, 0, nch));
        fileContent = n.getTextArea().getText();
      } catch (FileNotFoundException x) {
      } catch (IOException ioe) {
        System.err.println("I/O Error on Open");
      }
    }
    n.setTitle(jfc.getSelectedFile().getName() + " - JAVA� Notepad");
  }

  public void save() {
    try {
      PrintWriter fout = new PrintWriter(new FileWriter(jfc.getSelectedFile()));
      fileContent = n.getTextArea().getText();
      StringTokenizer st = new StringTokenizer(fileContent, System.getProperty("line.separator"));
      while (st.hasMoreTokens()) {
        fout.println(st.nextToken());
      }
      fout.close();
    } catch (IOException ioe) {
      System.err.println("I/O Error on Save");
    }
    n.setTitle(jfc.getSelectedFile().getName() + " - JAVA� Notepad");
  }

  public void saveAs() {
    filter.addExtension("txt");
    filter.setDescription("TXT Documents");
    jfc.setFileFilter(filter);
    returnVal = jfc.showSaveDialog(n);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      PrintWriter fout = null;
      try {
        fout = new PrintWriter(new FileWriter(jfc.getSelectedFile() + ".txt"));
        fileContent = n.getTextArea().getText();
        fileName = jfc.getSelectedFile().getPath();
        StringTokenizer st = new StringTokenizer(fileContent, System.getProperty("line.separator"));
        while (st.hasMoreTokens()) {
          fout.println(st.nextToken());
        }
        fout.close();
      } catch (IOException ioe) {
        System.err.println("I/O Error on Save");
      }
    }
    n.setTitle(jfc.getSelectedFile().getName() + " - JAVA� Notepad");
  }

  public void cuT() {
    n.getTextArea().cut();
  }

  public void copY() {
    n.getTextArea().copy();
  }

  public void pastE() {
    n.getTextArea().paste();
  }

  private String findword;

  public void finD() {
    try {
      findword = JOptionPane.showInputDialog("Type the word to find");
      while (n.getTextArea().getText().indexOf(findword) == -1) {
        JOptionPane.showMessageDialog(null, "Word not found!", "No match", JOptionPane.WARNING_MESSAGE);
        findword = JOptionPane.showInputDialog("Type the word to find");
      }
      n.getTextArea().select(n.getTextArea().getText().indexOf(findword),
          n.getTextArea().getText().indexOf(findword) + findword.length());
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, "Search canceled", "Abourted", JOptionPane.WARNING_MESSAGE);
    }
  }

  public void findNexT() {
    n.getTextArea().select(
        n.getTextArea().getText().indexOf(findword, (int) n.getTextArea().getText().indexOf(findword) + 1),
        n.getTextArea().getText().indexOf(findword, (int) n.getTextArea().getText().indexOf(findword) + 1));
  }
}