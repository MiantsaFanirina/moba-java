package Engine;

import java.awt.event.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * Utilitaires pour gérer les entrées clavier et souris.
 * (Actuellement non utilisé - les entrées sont gérées directement dans GamePanel)
 */
public final class InputUtils {

    private InputUtils() {}

    private static final Map<Integer, Runnable> keyPressActions = new HashMap<>();
    private static final Set<Integer> pressedKeys = new HashSet<>();

    public static void onKeyPressed(int keyCode, Runnable action) {
        keyPressActions.put(keyCode, action);
    }

    public static boolean isKeyDown(int keyCode) {
        return pressedKeys.contains(keyCode);
    }

    public static KeyListener createKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
                Runnable action = keyPressActions.get(e.getKeyCode());
                if (action != null) action.run();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        };
    }

    public static class MousePosition {
        public final int x;
        public final int y;

        public MousePosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final Map<Integer, Consumer<MousePosition>> mouseClickActions = new HashMap<>();

    public static void onMouseClick(int button, Consumer<MousePosition> action) {
        mouseClickActions.put(button, action);
    }

    public static MouseListener createMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Consumer<MousePosition> action = mouseClickActions.get(e.getButton());
                if (action != null) {
                    action.accept(new MousePosition(e.getX(), e.getY()));
                }
            }
        };
    }
}
