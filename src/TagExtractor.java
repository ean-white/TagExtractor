
import java.awt.Font;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

public class TagExtractor extends JFrame
{
        String inputFileName;
        String stopFileName;
        String inputFilePath;
        String stopFilePath;

        ArrayList<String> stop = new ArrayList<>();

        TreeMap<String, Integer> tags = new TreeMap<String, Integer>();

        JButton chooseFile = new JButton("Choose File");
        JButton stopFile = new JButton("Stop File");
        JButton saveFile = new JButton("Save to File");
        JLabel FileNameLabel = new JLabel();
        JLabel Label = new JLabel();
        JScrollPane panel3;

        JTextArea textarea = new JTextArea();

        public TagExtractor()
        {
            JFrame frame = new JFrame("file chooser");

            frame.setSize(600, 600);

            textarea.setFont(new Font("Times New Romman", Font.PLAIN, 20));

            frame.setVisible(true);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setLayout(null);

            chooseFile.setBounds(10, 10, 100, 30);
            stopFile.setBounds(10, 50, 100, 30);
            FileNameLabel.setBounds(130, 10, 200, 30);
            Label.setBounds(130, 50, 200, 30);
            saveFile.setBounds(10, 500, 100, 30);

            frame.add(chooseFile);
            frame.add(stopFile);
            frame.add(FileNameLabel);
            frame.add(Label);
            frame.add(saveFile);

            textarea.setBounds(10, 10, 300, 300);
            panel3 = new JScrollPane(textarea);
            panel3.setBounds(10, 100, 400, 400);
            frame.add(panel3);
            //add actionListener to all buttons
            chooseFile.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {

                    // create an object of JFileChooser class
                    JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                    // invoke the showsSaveDialog function to show the save dialog
                    int r = j.showSaveDialog(null);

                    // if the user selects a file
                    if (r == JFileChooser.APPROVE_OPTION) {

                        //get FileName
                        inputFileName = j.getSelectedFile().getName();
                        //get FilePath
                        inputFilePath = j.getSelectedFile().getAbsolutePath();
                        //display file name
                        FileNameLabel.setText("File name : " + inputFileName);
                        //clear the contents of textarea and tags
                        textarea.setText("");
                        tags.clear();
                    } // if the user cancelled the operation
                    else {
                        FileNameLabel.setText("file not choosed");
                    }
                }

            });

            stopFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    // create an object of JFileChooser class
                    JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                    // invoke the showsSaveDialog function to show the save dialog
                    int r = j.showSaveDialog(null);

                    // if the user selects a file
                    if (r == JFileChooser.APPROVE_OPTION) {

                        //get stop file path
                        stopFilePath = j.getSelectedFile().getAbsolutePath();
                        Label.setText("");
                        //convert stop file to arraylist
                        readstopFile();
                        //perform the filter process
                        Perform();
                    } // if the user cancelled the operation
                    else {
                        Label.setText("file not choosed");
                    }
                }

            });

            saveFile.addActionListener(new ActionListener() {
                                           @Override
                                           public void actionPerformed(ActionEvent e) {

                                               try {
                                                   FileWriter fw = new FileWriter("SampleOutput.txt");

                                                   //write the tag and its value to a file
                                                   for (Map.Entry m : tags.entrySet()) {

                                                       fw.write((String.format("%-10s\t\t%d\n",m.getKey(), m.getValue())));
                                                   }

                                                   //show message
                                                   JOptionPane.showMessageDialog(Label, "File Saved");

                                                   fw.close();
                                               } catch (Exception ex) {
                                                   System.out.println(ex);
                                               }

                                           }

                                       }
            );
        }

        public static void main(String[] args)
        {
            new TagExtractor();
        }

        //method to remove non alphabetic character
        public String removeNonCharacter(String word) {

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                continue;
            } else {
                word = word.replace(c + "", " ");
            }
        }

        return word;
    }

        //method to insert a tag to tags
        public void insert(String word) {
        if (word != null && !"".equals(word) && !"".equals(word)) {
            tags.put(word, 1);
        }
    }

        //method to perform the filter process
        public void Perform() {

        try {
            //the file to be opened for reading
            FileInputStream fis = new FileInputStream(inputFilePath);
            Scanner sc = new Scanner(fis);    //file to be scanned
            //returns true if there is another line to read
            while (sc.hasNextLine())
            {
                //get a line and convert to lowercase
                String line = sc.nextLine().toLowerCase();
                //remove all non alphabetic character
                line = removeNonCharacter(line);
                //split the line
                String word[] = line.split(" ");
                for (int i = 0; i < word.length; i++) {
                    String current = word[i];
                    //check it already in tags or not
                    if (isAvailable(current, tags)) {

                    } //check it contains stop words or not
                    else if (!isStop(current)) {
                        //insert to tags
                        insert(current);

                    }
                }
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //print the tags into text area
        for (Map.Entry m : tags.entrySet()) {

            textarea.append(String.format("%-10s\t\t%d\n",m.getKey(), m.getValue()));

        }

    }

        //method to read stop file and store it in arrayList
        public void readstopFile()
        {

            try {
                //the file to be opened for reading
                FileInputStream fis = new FileInputStream(stopFilePath);
                Scanner sc = new Scanner(fis);    //file to be scanned
                //returns true if there is another line to read
                while (sc.hasNextLine())
                {
                    //add to arraylist
                    stop.add(sc.nextLine());
                }
                sc.close();     //closes the scanner
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //method to check if the word is stop word or not
        public boolean isStop(String word) {
        for (String s : stop) {
            if (s.equals(word)) {
                return true;
            }
        }

        return false;
    }

        //method to check if the tag already present in tags
        //if already present increment its value
        public boolean isAvailable(String word, TreeMap<String, Integer> tags)
        {
            for (Map.Entry m : tags.entrySet()) {
                if (m.getKey().equals(word)) {
                    int value = Integer.parseInt(m.getValue().toString()) + 1;
                    m.setValue(value);

                    return true;
                }
            }

            return false;
        }

}
