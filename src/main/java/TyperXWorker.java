import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.awt.AWTKeyStroke.getAWTKeyStroke;
import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;

public class TyperXWorker {
    public static final int RUNNING = 1;
    public static final int STOPPED = 0;
    private final Robot robot;
    private final TyperXView xFrame;
    private final AtomicInteger isRunning;
    private int startTime = 1;
    private int keyStrokeDelay = 1;
    private boolean removeFrontSpaces=false;


    public TyperXWorker(TyperXView frame) throws AWTException
    {
        this.robot = new Robot();
        this.xFrame = frame;
        this.isRunning = new AtomicInteger(STOPPED);
    }

    public static AWTKeyStroke getKeyStroke(char c)
    {
        final String upperKeys = "`~'\"!@#$%^&*()_+{}|:<>?";
        final String lowerKeys = "`~'\"1234567890-=[]\\;,./";

        int index = upperKeys.indexOf(c);
        if (index != -1) {
            int keyCode;
            boolean shift = false;
            switch (c) {
                // these chars need to be handled specially because
                // they don't directly translate into the correct keycode
                case '~' -> {
                    keyCode = 192;
                    shift = true;
                }
                case '\"' -> {
                    keyCode = 222;
                    shift = true;
                }
                case '`' -> keyCode = KeyEvent.VK_BACK_QUOTE;
                case '\'' -> keyCode = KeyEvent.VK_QUOTE;
                default -> {
                    keyCode = Character.toUpperCase(lowerKeys.charAt(index));
                    shift = true;
                }
            }
            return getAWTKeyStroke(keyCode, shift ? SHIFT_DOWN_MASK : 0);
        }
        return getAWTKeyStroke(Character.toUpperCase(c), 0);
    }


    private void pressKey(char c, int miliSec)
    {
        AWTKeyStroke keyStroke = getKeyStroke(c);
        int keyCode = keyStroke.getKeyCode();
        boolean isShiftRequired = Character.isUpperCase(c) || keyStroke.getModifiers() == (InputEvent.SHIFT_MASK | SHIFT_DOWN_MASK);

        if (isShiftRequired) {
            robot.keyPress(KeyEvent.VK_SHIFT);
        }
        try{
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
            if (miliSec > 0) {
                Thread.sleep(miliSec);
            }
        } catch (Exception exception) {
            System.out.println("Error typing " + KeyEvent.getKeyText(keyCode));
            exception.printStackTrace();
        }

        if (isShiftRequired) {
            robot.keyRelease(KeyEvent.VK_SHIFT);
        }

    }


    private void sendKeys(String keys)
    {
        Runnable typerRunnable = () -> runTyper(keys);
        Thread thread = new Thread(typerRunnable);
        thread.start();
    }

    private void runTyper(String keys)
    {
        if (removeFrontSpaces){
            keys=removeFrontSpaces(keys);
        }
        for (int i = 0; i < startTime; i++) {
            if (isRunning.get() == STOPPED) {
                break;
            }
            this.xFrame.setStatus("Starting typing in " + (startTime - i) + " sec");
            try{
                Thread.sleep(1000L);
            } catch (InterruptedException ex) {
                Logger.getLogger(TyperXView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.xFrame.setStatus("Running  now..");
        char[] charArray = keys.toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (isRunning.get() == STOPPED) {
                break;
            }
            try{
                pressKey(c, keyStrokeDelay);
                int progress = (i * 100) / charArray.length;
                this.xFrame.setProgress(progress);

            } catch (Exception ex) {

                Logger.getLogger(TyperXView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        xFrame.startUI();
        this.isRunning.set(STOPPED);
    }

    public void startRequest(String text)
    {
        if (isRunning.compareAndSet(STOPPED, RUNNING)) {
            xFrame.stopUI();
            String  converted=text.replace("\t","    ").replaceAll("[^\\x00-\\x7F]", " ");

            this.sendKeys(converted);
        }
    }

    public void stopRequest()
    {
        if (this.isRunning.compareAndSet(RUNNING, STOPPED)) {
            xFrame.startUI();
        }
    }

    private String removeFrontSpaces(String text){
        String[] lines = text.split("\\r?\\n");
        StringBuilder builder=new StringBuilder();
        for (String line : lines){
            builder.append(line.replaceFirst("\\s+","")).append("\n");
        }
        return builder.toString();
    }


    public int getStartTime()
    {
        return startTime;
    }

    public void setStartTime(int startTime)
    {
        this.startTime = startTime;
    }

    public int getKeyStrokeDelay()
    {
        return keyStrokeDelay;
    }

    public void setKeyStrokeDelay(int keyStrokeDelay)
    {
        this.keyStrokeDelay = keyStrokeDelay;
    }

    public boolean isRemoveFrontSpaces() {
        return removeFrontSpaces;
    }

    public void setRemoveFrontSpaces(boolean removeFrontSpaces) {
        this.removeFrontSpaces = removeFrontSpaces;
    }
}
