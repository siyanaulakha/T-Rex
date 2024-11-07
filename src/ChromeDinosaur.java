import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.io.*;
import java.util.Collections;
import java.util.Comparator;

public class ChromeDinosaur extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 800;
    int boardHeight = 400;
    Image backImg, hospImg, dinosaurImg, dinosaurDeadImg, dinosaurJumpImg, cactus1Img, cactus2Img, cactus3Img, bird1Img, bird2Img, birdGif, trackImg;
    int dinosaurWidth = 88, dinosaurHeight = 94, dinosaurX = 50, dinosaurY = boardHeight - dinosaurHeight;
    Block dinosaur;
    int cactus1Width = 34, cactus2Width = 69, cactus3Width = 102;
    int cactusHeight = 70, cactusX = 700, cactusY = boardHeight - cactusHeight, initialfreq = 1000;
    ArrayList<Block> cactusArray = new ArrayList<>();
    int velocityX = -10, velocityY = 0, gravity = 1, score = 0;
    boolean gameOver = false;
    Timer gameLoop, placeCactusTimer;
    HighScoreManager highScoreManager = new HighScoreManager();
    int trackX1 = 0, trackX2;
    String playerName;
    JComboBox<String> characterComboBox;
    int charac;
    
    public ChromeDinosaur() {
        playerName = JOptionPane.showInputDialog("Enter your name:");

        // Setup character selection
        characterComboBox = new JComboBox<>(new String[]{"dino", "bbg", "virus"});
        characterComboBox.setSelectedIndex(0);
        characterComboBox.addActionListener(e -> loadCharacterAssets((String) characterComboBox.getSelectedItem()));
        JOptionPane.showMessageDialog(null, characterComboBox, "Select Character", JOptionPane.QUESTION_MESSAGE);
        
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        backImg = new ImageIcon(getClass().getResource("./img/back.png")).getImage();
        hospImg = new ImageIcon(getClass().getResource("./img/hosp.jpg")).getImage();
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // Load assets for initially selected character
        loadCharacterAssets((String) characterComboBox.getSelectedItem());

        bird1Img = new ImageIcon(getClass().getResource("./img/bird1.png")).getImage();
        bird2Img = new ImageIcon(getClass().getResource("./img/bird2.png")).getImage();
        birdGif = new ImageIcon(getClass().getResource("./img/bird.gif")).getImage();
        trackImg = new ImageIcon(getClass().getResource("./img/track.png")).getImage();
        trackX2 = trackImg.getWidth(null);

        dinosaur = new Block(dinosaurX, dinosaurY, dinosaurWidth, dinosaurHeight, dinosaurImg);
        gameLoop = new Timer(1000 / 80, this);
        gameLoop.start();
        placeCactusTimer = new Timer(initialfreq, e -> placeCactus());
        placeCactusTimer.start();
    }

    private void loadCharacterAssets(String character) {
        switch (character) {
            case "dino":
                dinosaurImg = new ImageIcon(getClass().getResource("./img/dino-run.gif")).getImage();
                dinosaurDeadImg = new ImageIcon(getClass().getResource("./img/dino-dead.png")).getImage();
                dinosaurJumpImg = new ImageIcon(getClass().getResource("./img/dino-jump.png")).getImage();
                cactus1Img = new ImageIcon(getClass().getResource("./img/cactus1.png")).getImage();
                cactus2Img = new ImageIcon(getClass().getResource("./img/cactus2.png")).getImage();
                cactus3Img = new ImageIcon(getClass().getResource("./img/cactus3.png")).getImage();
                charac = 1;
                break;

            case "bbg":
                dinosaurImg = new ImageIcon(getClass().getResource("./img/mini1.gif")).getImage();
                dinosaurDeadImg = dinosaurImg;
                dinosaurJumpImg = dinosaurImg;
                cactus1Img = new ImageIcon(getClass().getResource("./img/cactus1.png")).getImage();
                cactus2Img = new ImageIcon(getClass().getResource("./img/cactus2.png")).getImage();
                cactus3Img = new ImageIcon(getClass().getResource("./img/cactus3.png")).getImage();
                charac = 2;
                break;

            case "virus":
                dinosaurImg = new ImageIcon(getClass().getResource("./img/virus.png")).getImage();
                dinosaurDeadImg = dinosaurImg;
                dinosaurJumpImg = dinosaurImg;
                cactus1Img = new ImageIcon(getClass().getResource("./img/doc.png")).getImage().getScaledInstance(75, 250, Image.SCALE_SMOOTH);
                cactus2Img = new ImageIcon(getClass().getResource("./img/antivirus1.png")).getImage().getScaledInstance(100, 250, Image.SCALE_SMOOTH);
                cactus3Img = new ImageIcon(getClass().getResource("./img/antivirus2.png")).getImage().getScaledInstance(120, 250, Image.SCALE_SMOOTH);
                charac = 3;
                break;
        }
    }


    // Place cactus logic with no birds if the "virus" character is selected
void placeCactus() {
    if (!this.gameOver) {
        double randomValue = Math.random();
        Block obstacle;

        // Check if character is "virus" to avoid generating birds
        if (!"virus".equals(characterComboBox.getSelectedItem()) && score > 500 && randomValue > 0.5) {
            int birdHeight = 50; // Bird height
            int birdY = this.cactusY - 80; // Bird's position higher than the cactus

            if (randomValue > 0.85) {
                obstacle = new Block(this.cactusX, birdY, 50, birdHeight, this.birdGif);
            } else if (randomValue > 0.75) {
                obstacle = new Block(this.cactusX, cactusY, 50, birdHeight, this.birdGif);
            } else {
                obstacle = new Block(this.cactusX, birdY + 30, 50, birdHeight, this.birdGif);
            }
        } else {
            // Place a cactus if not generating birds
            if (randomValue > 0.475) {
                obstacle = new Block(this.cactusX, this.cactusY, this.cactus3Width, this.cactusHeight, this.cactus3Img);
            } else if (randomValue > 0.375) {
                obstacle = new Block(this.cactusX, this.cactusY, this.cactus2Width, this.cactusHeight, this.cactus2Img);
            } else {
                obstacle = new Block(this.cactusX, this.cactusY, this.cactus1Width, this.cactusHeight, this.cactus1Img);
            }
        }

        this.cactusArray.add(obstacle);
        if (this.cactusArray.size() > 10) {
            this.cactusArray.remove(0);
        }
    }
}


    // Drawing components
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(charac == 2){
        if (backImg != null) {
            g.drawImage(backImg, 0, 0, getWidth(), getHeight(), this);
        }
    }
    else if(charac == 3){
        g.drawImage(hospImg, 0, 0, getWidth(), getHeight(), this);
    }
        draw(g);
    }

    public void draw(Graphics g) {
        // Draw the moving ground track
        g.drawImage(trackImg, trackX1, boardHeight - trackImg.getHeight(null), null);
        g.drawImage(trackImg, trackX2, boardHeight - trackImg.getHeight(null), null);
    
        // Draw other game elements
        g.drawImage(dinosaur.img, dinosaur.x, dinosaur.y, dinosaur.width, dinosaur.height, null);
        for (Block cactus : cactusArray) {
            g.drawImage(cactus.img, cactus.x, cactus.y, cactus.width, cactus.height, null);
        }
    
        g.setColor(Color.white);
        g.setFont(new Font("Press Start 2P", Font.BOLD, 20));
        if (gameOver) {
            g.drawString("Game Over: " + score, 280, boardHeight / 2);
        } else {
            g.drawString("Score: "+score, 10, 35);
        }
    }

    public void move() {
        // Move the track to the left
        trackX1 += velocityX;
        trackX2 += velocityX;
    
        // Reset track position for continuous scrolling
        if (trackX1 + trackImg.getWidth(null) < 0) {
            trackX1 = trackX2 + trackImg.getWidth(null);
        }
        if (trackX2 + trackImg.getWidth(null) < 0) {
            trackX2 = trackX1 + trackImg.getWidth(null);
        }
    
        // Existing game mechanics for dinosaur, cactus, etc.
        velocityY += gravity;
        dinosaur.y += velocityY;
        if (dinosaur.y > dinosaurY) {
            dinosaur.y = dinosaurY;
            velocityY = 0;
            dinosaur.img = dinosaurImg;
        }
        for (Block cactus : cactusArray) {
            cactus.x += velocityX;
            if (collision(dinosaur, cactus)) {
                gameOver = true;
                dinosaur.img = dinosaurDeadImg;
            }
        }
        score += 1;
       // Increase obstacle speed and spawn frequency with score
    if (score % 300 == 0) {  // Increase every 100 points
        velocityX -= 1;  // Speed up
        int newDelay = Math.max(200, placeCactusTimer.getDelay() - 40);  // Decrease delay, minimum 200ms
        placeCactusTimer.setDelay(newDelay);
    }
}
    

    boolean collision(Block a, Block b) {
        int collisionMargin = 10;

        return a.x + collisionMargin < b.x + b.width - collisionMargin &&
               a.x + a.width - collisionMargin > b.x + collisionMargin &&
               a.y + collisionMargin < b.y + b.height - collisionMargin &&
               a.y + a.height - collisionMargin > b.y + collisionMargin;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
            repaint();
        } else {
            placeCactusTimer.stop();
            gameLoop.stop();
            showGameOverScreen();
        }
    }

    private void showGameOverScreen() {
        highScoreManager.updateScore(playerName, score); // Update the score if player exists
        showLeaderboard();
    }

    private void showLeaderboard() {
        List<Score> scores = highScoreManager.loadScores();
        StringBuilder leaderboardText = new StringBuilder("Leaderboard:\n");
        for (Score score : scores) leaderboardText.append(score).append("\n");
        JOptionPane.showMessageDialog(null, leaderboardText.toString(), "Leaderboard", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (dinosaur.y == dinosaurY) {
                velocityY = -17;
                dinosaur.img = dinosaurJumpImg;
            }
            if (gameOver) {
                resetGame();
            }
        }
    }

    private void resetGame() {
        dinosaur.y = dinosaurY;
        dinosaur.img = dinosaurImg;
        velocityY = 0;
        cactusArray.clear();
        score = 0;
        gameOver = false;
        gameLoop.start();
        placeCactusTimer.start();
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    class Block {
        int x, y, width, height;
        Image img;
        Block(int x, int y, int width, int height, Image img) {
            this.x = x; this.y = y; this.width = width; this.height = height; this.img = img;
        }
    }

    class HighScoreManager {
        private final String filePath = "highscores.txt";
        private final int maxScores = 5;

        public List<Score> loadScores() {
            List<Score> scores = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) scores.add(new Score(parts[0], Integer.parseInt(parts[1])));
                }
            } catch (IOException e) { System.out.println("Error reading scores: " + e.getMessage()); }
            scores.sort(Comparator.comparingInt(Score::getScore).reversed());
            return scores;
        }

        public void saveScores(List<Score> scores) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
                for (Score score : scores) bw.write(score.getName() + ":" + score.getScore() + "\n");
            } catch (IOException e) { System.out.println("Error saving scores: " + e.getMessage()); }
        }

        public void updateScore(String playerName, int score) {
            List<Score> scores = loadScores();
            boolean updated = false;
            for (Score s : scores) {
                if (s.getName().equals(playerName)) {
                    if (score > s.getScore()) { // Update score only if new score is higher
                        s.score = score;
                    }
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                scores.add(new Score(playerName, score));
            }
            scores.sort(Collections.reverseOrder());
            if (scores.size() > maxScores) scores = scores.subList(0, maxScores);
            saveScores(scores);
        }
    }

    class Score implements Comparable<Score> {
        private final String name;
        private int score;
        Score(String name, int score) { this.name = name; this.score = score; }
        public String getName() { return name; }
        public int getScore() { return score; }
        @Override public int compareTo(Score other) { return Integer.compare(other.score, this.score); }
        @Override public String toString() { return name + ": " + score; }
    }
}
