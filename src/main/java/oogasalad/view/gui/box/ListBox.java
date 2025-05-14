package oogasalad.view.gui.box;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Function;
import oogasalad.model.builder.UndoRedoManager;
import oogasalad.model.builder.actions.BuilderPanelEditAction;
import oogasalad.model.config.GameConfig;

/**
 * A reusable UI component that manages a vertical list of editable rows (HBox).
 * Each row is generated from a provided HBox generator (e.g., text field + delete button).
 * Also allows registering a callback to be triggered whenever the list is updated.
 *
 * @param <T> the type of initial value passed to the row generator (typically String)
 *
 * @author Hsuan-Kai Liao
 */
public class ListBox<T> extends VBox {

  private final Function<T, HBox> rowGenerator;
  private final Function<HBox, T> rowDecomposer;
  private final Button addButton = new Button(GameConfig.getText("addText"));
  private Consumer<List<T>> onListUpdated = values -> {};

  /**
   * Constructs a ListBox with a row generator function.
   *
   * @param rowGenerator a function that takes an initial value and returns an HBox row
   */
  public ListBox(Function<T, HBox> rowGenerator, Function<HBox, T> rowDecomposer, Supplier<T> defaultValueSupplier) {
    this.rowGenerator = rowGenerator;
    this.rowDecomposer = rowDecomposer;
    this.setSpacing(5);
    this.setPadding(new Insets(5));

    addButton.setOnAction(e -> {
      HBox newRow = createRow(defaultValueSupplier.get());
      int insertIndex = this.getChildren().size() - 1;
      this.getChildren().add(insertIndex, newRow);
      onListUpdated.accept(getValues());

      UndoRedoManager.addAction(new BuilderPanelEditAction<>(
          () -> { this.getChildren().remove(newRow); onListUpdated.accept(getValues()); },
          () -> { this.getChildren().add(insertIndex, newRow); onListUpdated.accept(getValues()); }
      ));
    });

    this.getChildren().add(addButton);
  }

  /**
   * Sets the values of the ListBox and populates the rows accordingly.
   *
   * @param values the list of initial values to set in the box
   */
  public void setValues(List<T> values) {
    this.getChildren().clear();
    for (T val : values) {
      this.getChildren().add(createRow(val));
    }
    this.getChildren().add(addButton);
    onListUpdated.accept(getValues());
  }

  /**
   * Retrieves the values of the ListBox.
   */
  public List<T> getValues() {
    List<T> values = new ArrayList<>();
    for (var node : this.getChildren()) {
      if (node instanceof HBox row) {
        values.add(rowDecomposer.apply(row));
      }
    }
    return values;
  }

  /**
   * Registers a callback that will be triggered whenever the list is updated.
   *
   * @param callback a consumer that receives the updated list of non-empty strings
   */
  public void setOnListUpdated(Consumer<List<T>> callback) {
    this.onListUpdated = callback;
  }

  private HBox createRow(T initialValue) {
    HBox row = rowGenerator.apply(initialValue);

    Button removeButton = new Button(GameConfig.getText("minusText"));
    removeButton.setOnAction(e -> {
      int removeIndex = this.getChildren().indexOf(row);
      this.getChildren().remove(row);
      onListUpdated.accept(getValues());

      UndoRedoManager.addAction(new BuilderPanelEditAction<>(
          () -> {
            this.getChildren().add(removeIndex, row);
            onListUpdated.accept(getValues());
          },
          () -> {
            this.getChildren().remove(row);
            onListUpdated.accept(getValues());
          }
      ));
    });

    row.getChildren().add(removeButton);
    return row;
  }
}
