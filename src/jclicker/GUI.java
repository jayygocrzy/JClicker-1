package jclicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

public class GUI {
    private JComboBox buttonTypeCombo;
    private JComboBox clickTypeCombo;
    private JRadioButton repeatRadio;
    private JRadioButton untilStoppedRadio;
    private JSpinner repeatCountSpinner;
    private JButton startButton;
    private JButton stopButton;
    private JLabel hoursLabel;
    private JLabel minutesLabel;
    private JLabel secondsLabel;
    private JLabel milliLabel;
    private JLabel mouseButtonLabel;
    private JLabel clickTypeLabel;
    private JSpinner milliSpinner;
    private JSpinner hoursSpinner;
    private JSpinner minutesSpinner;
    private JSpinner secondsSpinner;
    private JPanel mainPanel;
    private JButton setHotkeyButton;
    private JTextField hotkeyField;

    private Clicker autoClicker = new Clicker();

    public int hotkeyCode = 118;
    public String hotkeyText = KeyEvent.getKeyText(hotkeyCode);

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("JClicker");
        frame.setContentPane(new GUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setMaximumSize(new Dimension(400,600));
        frame.setVisible(true);
    }

    public boolean setupClicker(){
        // Commit edits to the spinners to make sure their values are updated
        try{
            hoursSpinner.commitEdit();
            minutesSpinner.commitEdit();
            secondsSpinner.commitEdit();
            milliSpinner.commitEdit();
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(mainPanel,
                    "Error properly updating time values",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Grab the values from each of the spinners
        int hourVal = (Integer) hoursSpinner.getValue();
        int minuteVal = (Integer) minutesSpinner.getValue();
        int secondVal = (Integer) secondsSpinner.getValue();
        int milliVal = (Integer) milliSpinner.getValue();

        // TIMER SPINNER CHECK
        // If there is no time to go off of, notify the user
        if((hourVal == 0) && (minuteVal == 0) && (secondVal == 0) && (milliVal == 0)){
            JOptionPane.showMessageDialog(mainPanel,
                    "Time values cannot all be zero",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // TIMER SPINNER CHECK (NEGATIVE)
        // If there are any negative values, notify the user
        if((hourVal < 0) || (minuteVal < 0) || (secondVal < 0) || (milliVal < 0)){
            JOptionPane.showMessageDialog(mainPanel,
                    "Time values cannot all be zero",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Create a value to determine the milliseconds we must sleep for
        long sleepDuration = (TimeUnit.HOURS.toMillis(hourVal)
                + TimeUnit.MINUTES.toMillis(minuteVal)
                + TimeUnit.SECONDS.toMillis(secondVal)
                + milliVal);

        // Set the clicker's sleep duration
        autoClicker.setSleepDuration(sleepDuration);

        //REPETITION OPTIONS CHECK
        // If the "repeat until..." radio button is checked, let's make sure the spinner has a valid value
        if(repeatRadio.isSelected() && ((Integer) repeatCountSpinner.getValue() <= 0)){
            JOptionPane.showMessageDialog(mainPanel,
                    "Repetition counter must be greater than 0",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        boolean repeatUntil = false;

        if(repeatRadio.isSelected()){
            repeatUntil = true;
            autoClicker.isRepeatUntil(repeatUntil);
            autoClicker.setRepeatCount((Integer) repeatCountSpinner.getValue());
        }

        autoClicker.isRepeatUntil(repeatUntil);


        // Create a variable to determine what kind of button we're clicking
        // Grab the mask for the mouse button plus one (no mask for button 0)
/*
        int mouseButton = InputEvent.getMaskForButton(buttonTypeCombo.getSelectedIndex() + 1);
        System.out.println("Selected Index: " + buttonTypeCombo.getSelectedIndex());
        System.out.println("Mouse Mask: " + mouseButton);
*/

        // Create a variable to determine what kind of button we're clicking
        int mouseButton = InputEvent.BUTTON1_DOWN_MASK;

        switch(buttonTypeCombo.getSelectedItem().toString()){
            case "Left" : mouseButton = InputEvent.BUTTON1_DOWN_MASK; break;
            case "Middle" : mouseButton = InputEvent.BUTTON2_DOWN_MASK; break;
            case "Right" : mouseButton = InputEvent.BUTTON3_DOWN_MASK; break;
        }

        // Set the clicker's click type
        autoClicker.setMouseButton(mouseButton);

        // Create a variable to determine the click type
        boolean doubleClick = false;

        if(clickTypeCombo.getSelectedItem().toString().equals("Double"))
            doubleClick = true;

        autoClicker.isDoubleClick(doubleClick);

        return true;
    }

    public GUI() {
        // Make the buttons say their correct hotkeys
        startButton.setText("Start (" + hotkeyText + ")");
        stopButton.setText("Stop (" + hotkeyText + ")");
        hotkeyField.setText(hotkeyText);


 /*       // See how many mouse buttons the user's mouse has
        int numMouseButtons = MouseInfo.getNumberOfButtons();

        // If they have a mouse...
        if(numMouseButtons > 0) {
            // Add each mouse button to the dropdown
            for (int i = 1; i < numMouseButtons; i++) {
                // If we're dealing with mouse buttons 1-3, give them actual labels
                if(i <= 3){
                    String mouseButtonName = "";
                    switch(i){
                        case 1:
                            mouseButtonName = "Left";
                            break;
                        case 2:
                            mouseButtonName = "Middle";
                            break;
                        case 3:
                            mouseButtonName = "Right";
                            break;
                    }
                    buttonTypeCombo.addItem(mouseButtonName);
                // Else, just call them "mouse i"
                }else{
                    buttonTypeCombo.addItem("Mouse " + i);
                }
            }
        }*/

        // If the "Set Hotkey" button is pressed, begin the setting
        setHotkeyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);

                // Let the user know that we're waiting on user input
                hotkeyField.setText("Press Any Key");

                // Wait for a keypress
                setHotkeyButton.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent keyEvent) {
                        super.keyPressed(keyEvent);

                        // Get the keypress code and set that as the keyCode
                        hotkeyCode = keyEvent.getKeyCode();
                        hotkeyField.setText(KeyEvent.getKeyText(hotkeyCode));

                        // Update the buttons when a new hotkey is picked
                        startButton.setText("Start (" + KeyEvent.getKeyText(hotkeyCode) + ")");
                        stopButton.setText("Stop (" + KeyEvent.getKeyText(hotkeyCode) + ")");
                    }
                });

            }
        });

        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);

                // If there were no errors in setup, this will be true and start simulating clicks
                if(setupClicker() && !(autoClicker.getRunLoop())){
                    autoClicker.simulateClicks();
                }
            }
        });

        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if(autoClicker.getRunLoop()) {
                    autoClicker.setRunLoop(false);
                    System.out.println("Stopping Clicker");
                }
            }
        });
    }
}
