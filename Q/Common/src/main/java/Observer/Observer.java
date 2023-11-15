package Observer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.*;

import Map.IMap;
import Referee.IGameState;
import Serialization.JState;
import com.google.gson.Gson;

/**
 * Renders and saves the observed QGame.
 */
public class Observer extends JFrame implements IObserver {

  private final List<IGameState> states;
  private final String tempDir;
  private int displayedStateIndex;
  private final JButton nextButton;
  private final JButton previousButton;
  private final JButton saveButton;
  private JComponent activeState;
  private JScrollPane scrollActiveState;
  private final JLabel scoreLabel;

  public Observer() {
    this.states = new ArrayList<>();
    this.displayedStateIndex = -1;
    JPanel buttonPannel = new JPanel();
    buttonPannel.setLayout(new BoxLayout(buttonPannel, BoxLayout.X_AXIS));
    this.nextButton = new JButton("Next!");
    this.previousButton = new JButton("Previous!");
    this.saveButton = new JButton("Save!");
    this.nextButton.addActionListener(e -> this.next());
    this.previousButton.addActionListener(e -> this.previous());
    this.saveButton.addActionListener(e -> this.saveJson());
    this.nextButton.setEnabled(false);
    this.previousButton.setEnabled(false);
    this.saveButton.setEnabled(false);
    buttonPannel.add(this.previousButton);
    buttonPannel.add(this.saveButton);
    buttonPannel.add(this.nextButton);
    this.add(buttonPannel, BorderLayout.PAGE_START);
    this.pack();
    this.tempDir = "Tmp";
    new File(tempDir).mkdir();
    this.scoreLabel = new JLabel();
    this.add(this.scoreLabel, BorderLayout.PAGE_END);
  }

  @Override
  public void receiveState(IGameState state) {
    this.states.add(state);
    if (this.displayedStateIndex == -1) {
      //this.displayedStateIndex = 0;
      this.saveButton.setEnabled(true);
    }
    else {
      this.nextButton.setEnabled(false);
      this.previousButton.setEnabled(true);
    }
    this.displayedStateIndex = this.states.size() - 1;
    String filepath = tempDir + "/" + (this.states.size() - 1) + ".png";
    this.displayCurrentState();
    try {
      this.saveImage(filepath);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to save image to " + filepath, e);
    }
  }

  private void next() {
    if (this.states.size() - 1 > this.displayedStateIndex) {
      this.displayedStateIndex += 1;
      this.previousButton.setEnabled(true);
      if (this.states.size() - 1 == this.displayedStateIndex) {
        this.nextButton.setEnabled(false);
      }
      this.displayCurrentState();
    }
  }

  private void previous() {
    if (this.displayedStateIndex > 0) {
      this.displayedStateIndex -= 1;
      this.nextButton.setEnabled(true);
      if (0 == this.displayedStateIndex) {
        this.previousButton.setEnabled(false);
      }
      this.displayCurrentState();
    }
  }

  /**
   * EFFECT: Updates this JFrame to display the currently rendered state.
   */
  private void displayCurrentState() {
    if (!(this.activeState == null)) {
      this.remove(this.scrollActiveState);
    }
    if (this.displayedStateIndex == -1) {
      throw new IllegalStateException("Cannot draw the current state when no states have been given!");
    }
    this.activeState = this.states.get(displayedStateIndex).getMap().render();
    this.scrollActiveState = new JScrollPane(this.activeState);
    this.add(this.scrollActiveState, BorderLayout.CENTER);

    this.scoreLabel.setText("Scores:\n" +
        this.states.get(this.displayedStateIndex).getPlayerStates().stream()
            .map(player -> player.getName() + ": " + player.getScore()+"\n")
            .reduce("", String::concat)
    );

    this.pack();
  }

  private void saveJson() throws IllegalStateException{
    JFileChooser filepathSelector = new JFileChooser();
    filepathSelector.setFileSelectionMode(JFileChooser.FILES_ONLY);
    int result = filepathSelector.showSaveDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      try {
        File file = filepathSelector.getSelectedFile();
        new PrintWriter(new FileOutputStream(file)).println(
            new JState(this.states.get(this.displayedStateIndex)).serialize()
        );
      }
      catch (IOException e) {
        throw new IllegalStateException("Unable to save file: " +
                filepathSelector.getSelectedFile().getAbsolutePath(), e);
      }
    }
  }

  /**
   * Saves the board at the given index to the given filepath.
   * @param filename the filepath to save the image to.
   * @throws IOException if there is an error writing to the given filepath.
   */
  private void saveImage(String filename) throws IOException{
    BufferedImage imageBoard = new BufferedImage(this.activeState.getWidth(),
            this.activeState.getHeight(), BufferedImage.TYPE_INT_ARGB);

    Graphics2D g = imageBoard.createGraphics();
    this.activeState.printAll(g);
    g.dispose();
    ImageIO.write(imageBoard, "png", new File(filename));
  }


  @Override
  public void gameOver() {

  }
}
