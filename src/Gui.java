//package ce326.hw3;

import javax.swing.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Gui extends JPanel implements ActionListener, MouseListener, KeyListener {
    static final int WIDTH = 700;
    static final int HEIGHT = 700;
    JMenu newGame, player, history, help;
    JMenuBar bar;
    JMenuItem trivial, medium, hard;
    JRadioButtonMenuItem ai, you;
    ButtonGroup group;
    JFrame window;
    JList<String> list;
    DefaultListModel<String> listModel;
    JScrollPane listScrollPane;
    AiModule aiModule;
    GameData game;
    String firstPlayerOfTheRadioButton, firstPlayerOfTheGameNow, filePath, beginDateTime, first;
    boolean checkForTheBegin, continueOrStop = false; //if there is a win or draw stop listeners until begin a game, true for continue false for stop
    int currentColumn;
    File folderHistory;
    File[] files;
    ArrayList<Integer> aiMoves, userMoves;
    JSONArray playAI, playUSER;
    Timer timer, timer1, timer2;

    public Gui() { //constructor that creates the frame and the menu bar and puts the listeners in the components
        newGame = new JMenu();
        player = new JMenu();
        history = new JMenu();
        help = new JMenu();
        bar = new JMenuBar();
        trivial = new JMenuItem();
        medium = new JMenuItem();
        hard = new JMenuItem();
        ai = new JRadioButtonMenuItem();
        you = new JRadioButtonMenuItem();
        group = new ButtonGroup();
        window = new JFrame("Connect 4");

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        Dimension centeredWindow = Toolkit.getDefaultToolkit().getScreenSize();
        int centeredX = (centeredWindow.width - window.getWidth())/2-300;
        window.setLocation(centeredX,0);
        window.setVisible(true);

        setBackground(Color.BLUE);

        //creates the folder connect4 if there is not
        filePath = System.getProperty("user.home");
        folderHistory = new File(filePath,"connect4");
        if(!folderHistory.isDirectory()) {
            folderHistory.mkdir();
        }
        
        newGame.setText("New Game");

        trivial.setText("Trivial");
        trivial.setActionCommand("Trivial");
        trivial.addActionListener(this);
        newGame.add(trivial);

        medium.setText("Medium");
        medium.setActionCommand("Medium");
        medium.addActionListener(this);
        newGame.add(medium);

        hard.setText("Hard");
        hard.setActionCommand("Hard");
        hard.addActionListener(this);
        newGame.add(hard);

        bar.add(newGame);

        player.setText("1st Player");
        
        ai.setText("AI");
        ai.addActionListener(this);
        ai.doClick();
        ai.setSelected(true);

        you.setSelected(false);
        you.setText("You");
        you.addActionListener(this);

        group.add(ai);
        group.add(you);
        player.add(ai);
        player.add(you);

        bar.add(player);

        history.setText("History");
        history.addMouseListener(this);
        bar.add(history);

        help.setText("Help");
        bar.add(help);

        window.setJMenuBar(bar);

        window.getJMenuBar().setPreferredSize(new Dimension(30,30));
        window.getJMenuBar().getMenu(0).setFont(new Font("Times New Roman",Font.BOLD,15));
        window.getJMenuBar().getMenu(1).setFont(new Font("Times New Roman",Font.BOLD,15));
        window.getJMenuBar().getMenu(2).setFont(new Font("Times New Roman",Font.BOLD,15));
        window.getJMenuBar().getMenu(3).setFont(new Font("Times New Roman",Font.BOLD,15));
        window.getJMenuBar().getMenu(0).getItem(0).setFont(new Font("Times New Roman",Font.BOLD,15));
        window.getJMenuBar().getMenu(0).getItem(1).setFont(new Font("Times New Roman",Font.BOLD,15));
        window.getJMenuBar().getMenu(0).getItem(2).setFont(new Font("Times New Roman",Font.BOLD,15));
        window.getJMenuBar().getMenu(1).getItem(0).setFont(new Font("Times New Roman",Font.BOLD,15));
        window.getJMenuBar().getMenu(1).getItem(1).setFont(new Font("Times New Roman",Font.BOLD,15));

        window.addKeyListener(this);
        window.addMouseListener(this);

        this.checkForTheBegin=true;
        initializeNewGame(1);
        this.checkForTheBegin=false;
        this.continueOrStop=false;
        window.add(this,BorderLayout.CENTER);
        window.setPreferredSize(new Dimension(640,600));
        window.pack();
    }

    public void drawPanel(Graphics g) { //draws the panel and is used inside the paintComponent
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D)(g)).setStroke(new BasicStroke(2.0f));
        int x = WIDTH/9, y = HEIGHT/9;
        
        for(int i=x; i<8*x; i=i+x) {
            for(int j=y; j<7*y; j=j+y) {
                if(game.getGameArray()[j/x-1][i/y-1]=='W') {
                    g.setColor(Color.WHITE);
                }
                else if(game.getGameArray()[j/x-1][i/y-1]=='Y'||game.getGameArray()[j/x-1][i/y-1]=='B') { //'B' is for the dark yellow
                    if(game.getGameArray()[j/x-1][i/y-1]=='Y') {
                        g.setColor(Color.YELLOW);
                    }
                    else {
                        Color darkYellow = new Color(200,150,0);
                        g.setColor(darkYellow);
                    }
                }
                else if(game.getGameArray()[j/x-1][i/y-1]=='R'||game.getGameArray()[j/x-1][i/y-1]=='G') { //'G' is for the dark red
                    if(game.getGameArray()[j/x-1][i/y-1]=='R') {
                        g.setColor(Color.RED);
                    }
                    else {
                        Color darkRed = new Color(153,0,0);
                        g.setColor(darkRed);
                    }
                }
                g.fillOval(i-30,j-50,x-10,y-10);
                g.setColor(Color.BLACK);
                g.drawOval(i-30,j-50,x-10,y-10);
            }
        }
    }
    
    public void paintComponent(Graphics g) { //is used to paint the panel for the game
        super.paintComponent(g);
        drawPanel(g);
    }
   
    public void keyTyped(KeyEvent e) {
    }
    public void keyPressed(KeyEvent e)  {
    }
    public void keyReleased(KeyEvent e) { //user move with keyboard from 0-6 that represents the columns of the game array
        char number = e.getKeyChar();
        int column;
        
        if((timer!=null&&timer.isRunning())||(timer1!=null&&timer1.isRunning())) { //if timers are running do nothing
            return;
        }
        if(this.continueOrStop) { //if it is activated
            if(number=='0'||number=='1'||number=='2'||number=='3'||number=='4'||number=='5'||number=='6') { //columns 0-6
                column = number - '0';
                if(game.getGameArray()[0][column]=='W') {
                    this.continueOrStop=false; //deactivated after the move until the ai make a move
                    addWithKeyOrMouseClicked(column);
                }
            }
        }
    }

    public void mouseClicked(MouseEvent e) { //user move if it is a double click somewhere in the columns or one click in history menu or double click in the list of history
        int numberOfClicks = e.getClickCount();
        int xActual = e.getX(), yActual = e.getY();
        int xPanel = WIDTH/9, yPanel = HEIGHT/9;

        if(numberOfClicks==2&&e.getSource() instanceof JList&&e.getButton()==MouseEvent.BUTTON1) { //double click in the list of the history to play a game from the past
            int index = list.locationToIndex(e.getPoint());

            if(index!=-1) {
                String game = (String) list.getModel().getElementAt(index);

                playGame(game);
            }
        }
        if(e.getSource() instanceof JMenu) {
            JMenu menuOption = (JMenu) e.getSource();
            String mouseText = menuOption.getText();
            if(mouseText.equals("History")) { //one click in the history menu
                makeListOfTheHistory();
            }
        }
        if((timer!=null&&timer.isRunning())||(timer1!=null&&timer1.isRunning())) { //if timers are running do nothing
            return;
        }
        if(numberOfClicks==2&&this.continueOrStop&&e.getButton()==MouseEvent.BUTTON1) { //user move with double click in one of the columns and must be activated this option
            if(yActual>=yPanel+14&&yActual<=6*(2*yPanel-62.5)) {
                for(int i=1,j=2; i<=7&&j<=8; i++,j++) {
                    if(xActual>=i*xPanel-30&&xActual<=j*xPanel-40) {
                        if(game.getGameArray()[0][i-1]=='W') { //column=i-1 from 0 to 6
                            this.continueOrStop=false; //deactivated after the move until the ai make a move
                            addWithKeyOrMouseClicked(i-1);
                            break;
                        }
                    }
                }
            }
        }
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e){
    }
    public void mousePressed(MouseEvent e) {
    }
    public void mouseReleased(MouseEvent e) {
    }

    class TimerListener implements ActionListener { //timer listener for the color change in the first move of the ai
        int count=0, column, row;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            if(count==0) {
                column=3;
                row=game.addInGameArray(aiModule.getPlayerTurn(), column);
                game.getGameArray()[row][column]='B'; //dark yellow
                aiMoves.add(column);
                repaint();
                revalidate();
            }
            else if(count==1) {
                game.removeFromGameArray(row, column);
                game.getGameArray()[row][column]='Y'; //yellow
                repaint();
                revalidate();
                Gui.this.continueOrStop=true; //activate the user options
                timer.stop();
            }
            count++;
        }
    }

    public void actionPerformed(ActionEvent e) { //listener for the buttons 'Trivial','Medium','Hard' and radio buttons 'AI','You'
        //code for buttons
        String difficulty = e.getActionCommand();
   
        if(difficulty.equals("Trivial")) { //trivial game
            beginNewGame(1);
        }
        else if(difficulty.equals("Medium")) { //medium game
            beginNewGame(3);
        }
        else if(difficulty.equals("Hard")) { //hard game
            beginNewGame(5);
        }
        else {
            //code for radio buttons
            if(ai.isSelected()) {
                this.firstPlayerOfTheRadioButton="AI";
            }
            else if(you.isSelected()) {
                this.firstPlayerOfTheRadioButton="User";
            }
        }
    }

    public void initializeNewGame(int maxDepth) { //begins a new game with correct initialization 

        aiModule = new AiModule(maxDepth,this.firstPlayerOfTheRadioButton);
        game = new GameData(new char[GameData.ROWS][GameData.COLUMNS]);
        game.initializationGameArray();
        aiMoves = new ArrayList<>();
        userMoves = new ArrayList<>();
        //stops the timers
        if(timer!=null&&timer.isRunning()) {
            timer.stop();
        }
        if(timer1!=null&&timer1.isRunning()) {
            timer1.stop();
        }
        if(timer2!=null&&timer2.isRunning()) {
            timer2.stop();
        }
        if(!this.checkForTheBegin) { //removes everything and puts them again in the panel of the game
            super.removeAll();

            super.repaint();
            super.revalidate();
        }
    }

    public void beginNewGame(int maxDepth) {

        if(listScrollPane!=null) { //removes the list if before it was in history mode and adds the panel of the game
            window.remove(listScrollPane);
            window.add(this,BorderLayout.CENTER);
            window.requestFocusInWindow();
            listScrollPane=null;
        }
        initializeNewGame(maxDepth);
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss");
        Calendar date = Calendar.getInstance();
        this.beginDateTime=dateFormat.format(date.getTime());
        this.firstPlayerOfTheGameNow=this.firstPlayerOfTheRadioButton;
        if(this.firstPlayerOfTheRadioButton.equals("AI")) { //if ai plays first
            aiModule.setPlayerTurn("AI");
            timer=new Timer(1000,new TimerListener()); //this timer is useful for the color change in the first move of the ai
            timer.setRepeats(true);
            timer.start();
        }
        else { //if user plays first activate the the user options to make a move
            this.continueOrStop=true;
        }
    }

    class Timer1Listener implements ActionListener { //timer listener for the moves of the user and after the ai and color changes on those moves
        int count=0, row, score, check=0;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            if(aiModule.getPlayerTurn().equals("User")&&check==0) {
                if(count==0) {
                    row=game.addInGameArray(aiModule.getPlayerTurn(), Gui.this.currentColumn);
                    game.getGameArray()[row][Gui.this.currentColumn]='G'; //dark red
                    userMoves.add(Gui.this.currentColumn);
                    repaint();
                    revalidate();
                    count++;
                } else if(count==1){
                    game.removeFromGameArray(row, Gui.this.currentColumn);
                    row=game.addInGameArray(aiModule.getPlayerTurn(), Gui.this.currentColumn); //red
                    score=aiModule.evaluateMove(game);
                    if(game.checkColumnFree()) { //plays the ai
                        ArrayList<Integer> bestMove=aiModule.minMaxWithAlphaBeta(game, 0, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
                        Gui.this.currentColumn=bestMove.get(1);
                    }
                    repaint();
                    revalidate();
                    count++;
                }
                if(count==2) {
                    check=1;
                    count=0;
                }
            }
            else {
                if(score==-10000) { //if the user won
                    makeGame("P");
                    JOptionPane.showMessageDialog(window,"You won!","Who is the winner?",JOptionPane.PLAIN_MESSAGE);
                    Gui.this.continueOrStop=false;
                    timer1.stop();
                } else if(aiModule.getPlayerTurn().equals("User")&&!game.checkColumnFree()) { //if it is a draw with the last move of the user
                    makeGame("Draw");
                    JOptionPane.showMessageDialog(window,"Draw!","Who is the winner?",JOptionPane.PLAIN_MESSAGE);
                    Gui.this.continueOrStop=false;
                    timer1.stop();
                } else { //plays the ai the next move
                    aiModule.setPlayerTurn("AI");
                    if(count==0) {
                        row=game.addInGameArray(aiModule.getPlayerTurn(), Gui.this.currentColumn);
                        game.getGameArray()[row][Gui.this.currentColumn]='B'; //dark yellow
                        aiMoves.add(Gui.this.currentColumn);
                        repaint();
                        revalidate();
                    } else if(count==1){
                        game.removeFromGameArray(row, Gui.this.currentColumn);
                        row=game.addInGameArray(aiModule.getPlayerTurn(), Gui.this.currentColumn); //yellow
                        score=aiModule.evaluateMove(game);
                        repaint();
                        revalidate();
                        if(score!=10000&&game.checkColumnFree()) { //if there is not a win or a draw acitivates the user options and stops the timer
                            Gui.this.continueOrStop=true;
                            timer1.stop();
                        }
                    }
                    if(count==2) {
                        if(score==10000) { //if the ai won
                            makeGame("AI");
                            JOptionPane.showMessageDialog(window,"You lost!","Who is the winner?",JOptionPane.PLAIN_MESSAGE);
                            Gui.this.continueOrStop=false;
                            timer1.stop();
                        }
                        else {
                            if(!game.checkColumnFree()) {  //if it is a draw with the last move of the ai
                                makeGame("Draw");
                                JOptionPane.showMessageDialog(window,"Draw!","Who is the winner?",JOptionPane.PLAIN_MESSAGE);
                                Gui.this.continueOrStop=false;
                            }
                            timer1.stop();
                        }
                    }
                    count++;
                }  
            }
        }
    }

    public void addWithKeyOrMouseClicked(int column) { //adds the user move and the ai move in the panel

        aiModule.setPlayerTurn("User");
        this.continueOrStop=false;
        this.currentColumn=column;
        timer1 = new Timer(1000,new Timer1Listener()); //moves for the user first and after the ai
        timer1.setRepeats(true);
        timer1.start();
    }

    public void makeListOfTheHistory() {

        //stops the timers
        if(timer!=null&&timer.isRunning()) {
            timer.stop();
        }
        if(timer1!=null&&timer1.isRunning()) {
            timer1.stop();
        }
        if(timer2!=null&&timer2.isRunning()) {
            timer2.stop();
        }
        if(listScrollPane!=null) { //removes the previous list
            window.remove(listScrollPane);
        }
        listModel = new DefaultListModel<>();
        files = folderHistory.listFiles(new FilenameFilter() { //takes the files that is .json inside the folder connect4
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });
        if(files!=null) {
            String[] listOfHistory = checkConnect4Folder(files); //list of history contains the strings that appears in the jlist and represents the games

            for(int i=0; i<listOfHistory.length; i++) {
                listModel.addElement(listOfHistory[i]);
            }
        }
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.setFont(new Font("Times New Roman",Font.PLAIN,18));
        list.addMouseListener(this);
        listScrollPane = new JScrollPane(list);
        window.remove(this);
        window.add(listScrollPane,BorderLayout.CENTER);
        window.repaint();
        window.revalidate();
        this.continueOrStop=false; //deactivated beacuse now this is in the history mode
    }

    public String[] checkConnect4Folder(File[] files) { //checks the files of the connect4 folder and returns the strings that is headers in the jlist
        String[] historyData = new String[files.length];
        StringBuilder strBuilder;
        String jsonString, dateTime, level, winner;
        int index=0, pos=0;
        String[] dateTimes = new String[files.length];

        for(File file : files) {
            try(Scanner sc = new Scanner(file)) {
                strBuilder = new StringBuilder();
                while(sc.hasNextLine()) {
                    String str = sc.nextLine();
                    strBuilder.append(str);
                    strBuilder.append("\n");
                }
            } catch(FileNotFoundException ex) { //if file does not exists or it is no readable
                continue;
            }

            jsonString=strBuilder.toString();

            try {
                JSONObject json = new JSONObject(jsonString);
                dateTime = (String) json.get("DateTime");
                level = (String) json.get("Level");
                winner = (String) json.get("Winner");
                json.getJSONArray("USERmoves");
                json.getJSONArray("AImoves");
                json.get("FirstPlayer");
            } catch(JSONException ex) {
                continue;
            }

            dateTimes[pos]=dateTime;
            pos++;

            if(level.equals("Trivial")) {
                historyData[index] = dateTime+"  L: "+level+"   W: "+winner;
            } else if(level.equals("Medium")) {
                historyData[index] = dateTime+"  L: "+level+"   W: "+winner;
            } else {
                historyData[index] = dateTime+"  L: "+level+"   W: "+winner;
            }
            index++;
        }
        int count=0;
        for(String str : dateTimes) {
            if(str!=null) {
                count++;
            }
        }
        String[] dateTimesHelp = new String[count];
        String[] historyDataHelp = new String[count];
        pos=0;
        for(String str : dateTimes) {
            if(str!=null) {
                dateTimesHelp[pos] = str;
                pos++;
            }
        }
        pos=0;
        for(String str : historyData) {
            if(str!=null) {
                historyDataHelp[pos] = str;
                pos++;
            }
        }
        dateTimes=dateTimesHelp;
        historyData=historyDataHelp;
        historyData = reverseSortByDateTime(historyData, dateTimes);
        return historyData;
    }

    public String[] reverseSortByDateTime(String[] historyData,String[] dateTimes) { //puts the most recent game first
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy.MM.dd - HH:mm:ss");
        ArrayList<LocalDateTime> dateTimeArray = new ArrayList<>();

        for(int i=0; i<dateTimes.length; i++) {
            dateTimeArray.add(LocalDateTime.parse(dateTimes[i],format));
        }
        Collections.sort(dateTimeArray, new Comparator<LocalDateTime>() {
            public int compare(LocalDateTime dateTime1, LocalDateTime dateTime2) {
                return dateTime2.compareTo(dateTime1);
            }
        });
        int pos=0;
        for(LocalDateTime time : dateTimeArray) { //makes the history date array with correct order
            dateTimes[pos] = time.format(format);
            for(int i=0; i<historyData.length; i++) {
                if(dateTimes[pos].equals(historyData[i].substring(0,21))) {
                    String temp = historyData[pos];
                    historyData[pos] = historyData[i];
                    historyData[i] = temp;
                }
            }
            pos++;
        }
        return historyData;
    }

    public void makeGame(String winner) { //creates the game after it is finished with a winner or a draw and put the details inside a new file
        JSONObject json = new JSONObject();
        String jsonString;

        json.put("DateTime",this.beginDateTime);
        if(aiModule.getMaxDepth()==1) {
            json.put("Level","Trivial");
        }
        else if(aiModule.getMaxDepth()==3) {
            json.put("Level","Medium");
        }
        else {
            json.put("Level","Hard");
        }
        json.put("FirstPlayer",this.firstPlayerOfTheGameNow);
        json.put("Winner",winner);
        JSONArray ai = new JSONArray(this.aiMoves);
        JSONArray user = new JSONArray(this.userMoves);
        json.put("AImoves",ai);
        json.put("USERmoves",user);
        jsonString = json.toString(2);
        int num=0;
        boolean check=true;
        File jsonFile = new File(folderHistory,"connect4_game"+"_"+num+".json");
        while(check) {
            if(jsonFile.exists()) {
                num++;
                jsonFile = new File(folderHistory,"connect4_game"+"_"+num+".json");
            }
            else {
                break;
            }
        }
        try {
            jsonFile.createNewFile();
            PrintWriter writer = new PrintWriter(jsonFile);
            writer.print(jsonString);
            writer.close();
        } catch (IOException e) {
        }
    }

    class Timer2Listener implements ActionListener { //timer for the representation of a game of the history and 3 seconds delay between each move
        int aiIndex=0, userIndex=0, currentIndex=0;
        String currentPlayer = first;
        JSONArray currentMoves;
        @Override
        public void actionPerformed(ActionEvent e) { //plays the moves from the arrays that are saved
            if(currentPlayer.equals("AI")) {
                currentMoves = playAI;
                currentIndex = aiIndex;
            } else {
                currentMoves = playUSER;
                currentIndex = userIndex;
            }

            if(aiIndex<playAI.length()||userIndex<playUSER.length()) {
                game.addInGameArray(currentPlayer, currentMoves.getInt(currentIndex));
                if(currentPlayer.equals("AI")) {
                    aiIndex++;
                    currentPlayer="User";
                }
                else {
                    userIndex++;
                    currentPlayer="AI";
                }
                repaint();
                revalidate();
            }
            else {
                if(currentPlayer.equals("User")&&game.checkColumnFree()) {
                    JOptionPane.showMessageDialog(window,"AI won!","Who is the winner?",JOptionPane.PLAIN_MESSAGE);
                }
                else if(currentPlayer.equals("AI")&&game.checkColumnFree()) {
                    JOptionPane.showMessageDialog(window,"Player won!","Who is the winner?",JOptionPane.PLAIN_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(window,"Draw!","Who is the winner?",JOptionPane.PLAIN_MESSAGE);
                }
                timer2.stop();
            }
        }
    }

    public void playGame(String jlistOption) { //plays the game of the history
        StringBuilder strBuilder;
        String jsonString;
        jlistOption = jlistOption.substring(0,21);

        for(File file : files) {
            try(Scanner sc = new Scanner(file)) {
                strBuilder = new StringBuilder();
                while(sc.hasNextLine()) {
                    String str = sc.nextLine();
                    strBuilder.append(str);
                    strBuilder.append("\n");
                }
            } catch(FileNotFoundException ex) { //if file does not exists or it is no readable
                continue;
            }
            jsonString=strBuilder.toString();

            JSONObject json = new JSONObject(jsonString);
            
            if(json.getString("DateTime").equals(jlistOption)) {
                playAI = json.getJSONArray("AImoves");
                playUSER = json.getJSONArray("USERmoves");
                first = json.getString("FirstPlayer");
                this.continueOrStop=false;
                window.remove(listScrollPane);
                window.add(this,BorderLayout.CENTER);
                
                repaint();
                revalidate();
                game = new GameData(new char[GameData.ROWS][GameData.COLUMNS]);
                game.initializationGameArray();
                timer2 = new Timer(3000, new Timer2Listener());
                timer2.setRepeats(true);
                timer2.start();
                break;
            }
            else {
                continue;
            }
        }        
    }
}