package com.paint;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;

public class App extends Application {

    // Current brush state (what the user selected)
    private Color currentColor = Color.BLACK;  // current color
    private double brushSize = 4;              // current brush size
    private boolean eraserOn = false;          // whether the eraser is active

    // Java Collection Framework: Map (LinkedHashMap keeps insertion order)
    private final Map<String, Color> colorPalette = new LinkedHashMap<>();

    @Override
    public void start(Stage stage) {

        // ---- Build palette using a LOOP (for) ----
        // (This also demonstrates loops clearly)
        String[] names = {"Black", "Red", "Blue", "Green", "Purple"};
        Color[] colors = {Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.PURPLE};

        for (int i = 0; i < names.length; i++) {
            colorPalette.put(names[i], colors[i]);
        }

        // === 1) Canvas (your "paper") ===
        Canvas canvas = new Canvas(900, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Initial white background (so the eraser works correctly)
        fillBackgroundWhite(gc, canvas);

        // === 2) Mouse events for drawing ===
        canvas.setOnMousePressed(e -> {
            gc.beginPath();

            // Apply brush settings immediately (so the first point is correct)
            gc.setLineWidth(brushSize);
            gc.setStroke(eraserOn ? Color.WHITE : currentColor);

            gc.moveTo(e.getX(), e.getY());
            gc.stroke();
        });

        canvas.setOnMouseDragged(e -> {
            // Apply current brush settings before drawing
            gc.setLineWidth(brushSize);

            // Conditional
            if (eraserOn) {
                gc.setStroke(Color.WHITE); // eraser = paint with white
            } else {
                gc.setStroke(currentColor); // normal brush = current color
            }

            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        });

        canvas.setOnMouseReleased(e -> gc.closePath());

        // === 3) Toolbar (UI) ===
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10));

        // --- Color palette (generated using LOOP + Collection) ---
        Label colorLabel = new Label("Color:");
        toolbar.getChildren().add(colorLabel);

        // LOOP (for-each) over the Map entries
        for (Map.Entry<String, Color> entry : colorPalette.entrySet()) {
            Button colorBtn = makeColorButton(entry.getKey(), entry.getValue());
            toolbar.getChildren().add(colorBtn);
        }

        // --- Brush types: Brush / Eraser ---
        toolbar.getChildren().add(new Label(" | "));

        Button brushBtn = new Button("Brush");
        brushBtn.setOnAction(e -> eraserOn = false);

        Button eraserBtn = new Button("Eraser");
        eraserBtn.setOnAction(e -> eraserOn = true);

        toolbar.getChildren().addAll(brushBtn, eraserBtn);

        // --- Brush size ---
        toolbar.getChildren().add(new Label(" | "));

        Label sizeLabel = new Label("Size:");
        Slider sizeSlider = new Slider(1, 30, brushSize);
        sizeSlider.setPrefWidth(140);

        // Update brushSize when the slider changes
        sizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            brushSize = newVal.doubleValue();
        });

        toolbar.getChildren().addAll(sizeLabel, sizeSlider);

        // --- Restart / Clear button ---
        toolbar.getChildren().add(new Label(" | "));

        Button clearBtn = new Button("Restart (Clear)");
        clearBtn.setOnAction(e -> {
            fillBackgroundWhite(gc, canvas);
            // optional: reset tool state
            // eraserOn = false;
            // currentColor = Color.BLACK;
            // brushSize = 4;
            // sizeSlider.setValue(brushSize);
        });

        toolbar.getChildren().add(clearBtn);

        // === 4) Main layout ===
        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(canvas);

        Scene scene = new Scene(root);

        stage.setTitle("My Paint App");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates a button that changes the current color when clicked.
     */
    private Button makeColorButton(String text, Color color) {
        Button btn = new Button(text);
        btn.setOnAction(e -> {
            currentColor = color; // save selected color
            eraserOn = false;     // switch back to normal brush
        });
        return btn;
    }

    /**
     * Paints the entire canvas white (used as "restart").
     */
    private void fillBackgroundWhite(GraphicsContext gc, Canvas canvas) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public static void main(String[] args) {
        launch();
    }
}
