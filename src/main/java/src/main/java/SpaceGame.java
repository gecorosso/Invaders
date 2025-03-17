package src.main.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SpaceGame extends JPanel implements ActionListener, KeyListener {
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 600;
    private static final int CANNON_WIDTH = 50;
    private static final int CANNON_HEIGHT = 40;
    private static final int SPACESHIP_WIDTH = 50;
    private static final int SPACESHIP_HEIGHT = 30;
    
    private int cannonX = GAME_WIDTH / 2;
    private int score = 0;
    private boolean isGameOver = false;
    private Timer gameTimer;
    private ArrayList<Projectile> projectiles = new ArrayList<>();
    private ArrayList<Spaceship> spaceships = new ArrayList<>();
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    
    private static class Projectile {
        int x, y;
        static final int WIDTH = 4;
        static final int HEIGHT = 10;
        
        Projectile(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        void move() {
            y -= 5;
        }
        
        
        Rectangle getBounds() {
            return new Rectangle(x, y, WIDTH, HEIGHT);
        }
    }
    
    private static class Spaceship {
        int x, y;
        int direction = 1;
        
        Spaceship(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        void move() {
            x += direction * 2;
            if (x <= 0 || x >= GAME_WIDTH - SPACESHIP_WIDTH) {
                direction *= -1;
                y += 20;
            }
        }
        
        Rectangle getBounds() {
            return new Rectangle(x, y, SPACESHIP_WIDTH, SPACESHIP_HEIGHT);
        }
    }
    
    public SpaceGame() {
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        
        // Initialize spaceships
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 8; col++) {
                spaceships.add(new Spaceship(50 + col * 80, 50 + row * 60));
            }
        }
        
        gameTimer = new Timer(16, this);
        gameTimer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw space cannon
        drawSpaceCannon(g, cannonX, GAME_HEIGHT - 60);
        
        // Draw projectiles
        g.setColor(Color.YELLOW);
        for (Projectile p : projectiles) {
            g.fillRect(p.x, p.y, Projectile.WIDTH, Projectile.HEIGHT);
        }
        
        // Draw UFOs
        for (Spaceship ship : spaceships) {
            // UFO body (oval)
            g.setColor(Color.LIGHT_GRAY);
            g.fillOval(ship.x, ship.y + 10, SPACESHIP_WIDTH, SPACESHIP_HEIGHT/2);
            
            // UFO dome
            g.setColor(Color.CYAN);
            g.fillArc(ship.x + 10, ship.y, SPACESHIP_WIDTH - 20, SPACESHIP_HEIGHT, 0, 180);
            
            // UFO lights
            g.setColor(Color.YELLOW);
            g.fillOval(ship.x + 8, ship.y + 15, 6, 6);
            g.fillOval(ship.x + SPACESHIP_WIDTH - 14, ship.y + 15, 6, 6);
            g.fillOval(ship.x + (SPACESHIP_WIDTH/2) - 3, ship.y + 15, 6, 6);
        }
        
        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 25);
        
        if (isGameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("GAME OVER", GAME_WIDTH/2 - 140, GAME_HEIGHT/2);
        }
    }
    
    private void drawSpaceCannon(Graphics g, int x, int y) {
        // Base del cannone
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x + 5, y + 20, CANNON_WIDTH - 10, 15);
        
        // Canna del cannone
        g.setColor(Color.GREEN);
        g.fillRect(x + (CANNON_WIDTH/2) - 4, y, 8, 25);
        
        // Supporto del cannone
        g.setColor(Color.GRAY);
        int[] xPoints = {x, x + CANNON_WIDTH, x + (CANNON_WIDTH/2)};
        int[] yPoints = {y + 35, y + 35, y + 20};
        g.fillPolygon(xPoints, yPoints, 3);
        
        // Dettagli del cannone
        g.setColor(Color.CYAN);
        g.drawLine(x + (CANNON_WIDTH/2) - 4, y + 5, x + (CANNON_WIDTH/2) + 4, y + 5);
        g.drawLine(x + (CANNON_WIDTH/2) - 4, y + 10, x + (CANNON_WIDTH/2) + 4, y + 10);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) {
            // Move cannon
            if (leftPressed && cannonX > 0) cannonX -= 5;
            if (rightPressed && cannonX < GAME_WIDTH - CANNON_WIDTH) cannonX += 5;
            
            // Move projectiles and check collisions
            projectiles.removeIf(p -> p.y < 0);
            for (Projectile p : new ArrayList<>(projectiles)) {
                p.move();
                for (Spaceship ship : new ArrayList<>(spaceships)) {
                    if (p.getBounds().intersects(ship.getBounds())) {
                        projectiles.remove(p);
                        spaceships.remove(ship);
                        score += 100;
                        break;
                    }
                }
            }
            
            // Move spaceships
            for (Spaceship ship : spaceships) {
                ship.move();
                if (ship.y > GAME_HEIGHT - 100) {
                    isGameOver = true;
                }
            }
            
            // Check win condition
            if (spaceships.isEmpty()) {
                isGameOver = true;
            }
        }
        repaint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                leftPressed = true;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = true;
                break;
            case KeyEvent.VK_SPACE:
                if (!isGameOver) {
                    projectiles.add(new Projectile(
                        cannonX + (CANNON_WIDTH/2) - (Projectile.WIDTH/2),
                        GAME_HEIGHT - 70
                    ));
                }
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                break;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Space Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new SpaceGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}