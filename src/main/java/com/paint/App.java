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

public class App extends Application {

    // Current brush state (what the user selected)
    private Color currentColor = Color.BLACK; // current color
    private double brushSize = 4;              // current brush size
    private boolean eraserOn = false;           // whether the eraser is active

    @Override
    public void start(Stage stage) {

        // === 1) Canvas (your "paper") ===
        Canvas canvas = new Canvas(900, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Initial white background (so the eraser works correctly)
        fillBackgroundWhite(gc, canvas);

        // === 2) Mouse events for drawing ===
        canvas.setOnMousePressed(e -> {
            gc.beginPath();
            gc.moveTo(e.getX(), e.getY());
            gc.stroke();
        });

        canvas.setOnMouseDragged(e -> {
            // Apply current brush settings before drawing
            gc.setLineWidth(brushSize);

            if (eraserOn) {
                gc.setStroke(Color.WHITE); // eraser = paint with white
            } else {
                gc.setStroke(currentColor); // normal brush = current color
            }

            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        });

        // === 3) Toolbar (UI) ===
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10));

        // --- Color palette (buttons) ---
        Label colorLabel = new Label("Color:");

        Button blackBtn = makeColorButton("Black", Color.BLACK);
        Button redBtn = makeColorButton("Red", Color.RED);
        Button blueBtn = makeColorButton("Blue", Color.BLUE);
        Button greenBtn = makeColorButton("Green", Color.GREEN);
        Button purpleBtn = makeColorButton("Purple", Color.PURPLE);

        // --- Brush types: Brush / Eraser ---
        Button brushBtn = new Button("Brush");
        brushBtn.setOnAction(e -> eraserOn = false);

        Button eraserBtn = new Button("Eraser");
        eraserBtn.setOnAction(e -> eraserOn = true);

        // --- Brush size ---
        Label sizeLabel = new Label("Size:");
        Slider sizeSlider = new Slider(1, 30, brushSize);
        sizeSlider.setPrefWidth(140);

        // Update brushSize when the slider changes
        sizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            brushSize = newVal.doubleValue();
        });

        // --- Restart / Clear button ---
        Button clearBtn = new Button("Restart (Clear)");
        clearBtn.setOnAction(e -> {
            fillBackgroundWhite(gc, canvas); // clears everything and keeps white background
        });

        // Add everything to the toolbar
        toolbar.getChildren().addAll(
                colorLabel, blackBtn, redBtn, blueBtn, greenBtn, purpleBtn,
                new Label(" | "),
                brushBtn, eraserBtn,
                new Label(" | "),
                sizeLabel, sizeSlider,
                new Label(" | "),
                clearBtn
        );

        // === 4) Main layout ===
        BorderPane root = new BorderPane();
        root.setTop(toolbar);     // top: tools
        root.setCenter(canvas);   // center: canvas

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
