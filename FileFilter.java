import java.util.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

class FileOperator {
  public FileOperator(String directory, String fileType, boolean deepFilter, JTextArea infoField) {

    this.infoField = infoField;
    this.filteredDir = new File(directory, "Filtered");
    this.infoField.append("[INFO] Filtered directory created at " + this.filteredDir.getPath() + "\n");
    this.deepFilter = deepFilter;
    this.fileType = fileType;
    try {
      File filtDir = new File(this.filteredDir.getPath());
      filtDir.mkdir();
    }catch(Exception e) {
    }
    filterDirectory(directory);
  }


  private void replaceFile(String from, String to) {
    File fromFile = new File(from);
    File toFile = new File(this.filteredDir, to);
    try{
      System.out.println(fromFile.getPath() + " " + toFile.getPath());
      FileInputStream toRead = new FileInputStream(fromFile.getPath());
      FileOutputStream toWrite = new FileOutputStream(toFile);

      byte[] buffer = new byte[1024];
      int length;

      while (( length = toRead.read(buffer)) > 0) {
        toWrite.write(buffer, 0, length);
      }
      toRead.close();
      toWrite.close();
      boolean del = fromFile.delete();
    } catch (Exception e) {
    }
  }

  private void filterDirectory(String directory) {
    File currDIR = new File(directory);
    File[] listDIR = currDIR.listFiles();
    for (int i=0; i<listDIR.length; i++) {
      File file = listDIR[i];
      if ( (file.isDirectory() && this.deepFilter) && (file.getName() != "Filtered") ) {
        File newDIR = new File(directory, file.getName());
        infoField.append("[FOLDER] Get into " + newDIR.getName() + "\n");
        filterDirectory(newDIR.getPath());
      } else if ( file.isFile() ) {
        String fileType = file.getName().split("\\.")[1].toLowerCase();
        if (fileType.equals(this.fileType)) {
          infoField.append("[FILE] Filter " + file.getName() +"\n");
          replaceFile(file.getPath(), file.getName());
        }
      } else {infoField.append("[ERROR] Some problems with " + file.getName() + "\n");} //PASS

    }
    infoField.append("[INFO] Finished with " + directory + "\n");



  }

  public String fileType;
  public File filteredDir;
  public boolean deepFilter;
  public JTextArea infoField;
}

abstract class ButtonAction implements ActionListener  {
    public JTextField field;
    public ButtonAction(JTextField field ) {
      this.field = field;
    }
}


class WorkElement {

  public JButton operBtn;
  public JTextField operEntry;


  public WorkElement(String buttonText, String entryText, Container mainContainter, GridBagConstraints constr) {
    this.operBtn = new JButton(buttonText);
    this.operEntry = new JTextField(entryText);
    operEntry.setEditable(false);

    constr.fill = constr.BOTH;
    constr.gridx = 0;
    constr.gridy = 0;
    mainContainter.add(operEntry, constr);
    constr.gridx = 2;
    constr.gridy = 0;
    mainContainter.add(operBtn, constr);
    constr.weighty = 0;

  }

  public void addActionListener(ActionListener actionList) {
    this.operBtn.addActionListener(actionList);
  }

}

class MainWindow extends JFrame {

  public String directory;
  public String fileType;
  public JTextField typeField;

  public MainWindow() {
    setTitle("File filter ( . )( . )");
    setSize(400, 300);

    Container container = getContentPane();
    container.setLayout(new GridBagLayout());
    GridBagConstraints constr = new GridBagConstraints();
    WorkElement filePath = new WorkElement("Set path", "Chose file type of file to eliminate", container, constr);

    typeField = new JTextField("( ͡° ͜ʖ ͡°)");
    typeField.setEditable(false);
    constr.gridx = 2;
    constr.gridy = 1;
    // constr.weightx = 3;
    container.add(typeField, constr);

    filePath.addActionListener(

    new ButtonAction(filePath.operEntry) {

      public void actionPerformed ( ActionEvent event ) {
        FileDialog dialog = new FileDialog((Frame) null);
        dialog.setVisible(true);
        String file = dialog.getFile();
        fileType = file.split("\\.")[1].toLowerCase();
        directory = dialog.getDirectory();
        this.field.setText(directory);
        typeField.setText(fileType.toUpperCase());
      }
    });




    JCheckBox checkDeep = new JCheckBox("Deep filter(Other directories)");
    constr.gridx = 0;
    constr.gridy = 1;
    constr.weightx = 3;
    container.add(checkDeep, constr);

    JTextArea infoField = new JTextArea("FileFilter v.1.0 \n============================\n");
    infoField.setLineWrap(true);
    JScrollPane scroll = new JScrollPane(infoField);
    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    constr.gridx = 0;
    constr.gridy = 2;
    constr.weightx = 2;
    constr.weighty = 1;
    container.add(scroll, constr);


    JButton filterLauncher = new JButton("Start");
    constr.gridy = 2;
    constr.gridx = 2;
    constr.weightx = 1;
    constr.weighty = 1;

    filterLauncher.addActionListener( new ActionListener() {

      public void actionPerformed(ActionEvent ev) {
        boolean deep;
        if (checkDeep.isSelected()) {
          deep = true;
        } else {
          deep = false;
        }
        infoField.setText("FileFilter v.1.0 \n============================\n");
        FileOperator fileOpr = new FileOperator(directory, fileType, deep, infoField);
      }

    });
    container.add(filterLauncher, constr);
    setVisible(true);

  }

}

public class FileFilter {
  public static void main( String args[] ) {
    MainWindow mainWindow = new MainWindow();
  }
}
