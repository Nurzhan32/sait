import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class main extends Application {

    private XYChart.Series<String, Number> stepData;
    private Label stepsLabel;
    private Label caloriesLabel;
    private Label distanceLabel;
    private Label avgStepsLabel;
    private Label avgCaloriesLabel;
    private Label heartRateLabel;
    private Label heartRateStatusLabel;
    private BarChart<String, Number> stepChart;
    private VBox rightPane;
    private SplitPane mainSplit;
    private SplitPane rightSplit;
    private CheckBox chartVisibilityCheckBox;
    private ProgressBar progressBar;
    private Label progressLabel;
    private int currentSteps = 0;
    private final int DAILY_GOAL = 10000;
    private boolean chartVisible = true;
    private TabPane tabPane;
    private VBox chartContainer;
    private VBox exercisesContainer;
    private Random random = new Random();
    
    // Главная верхняя панель вкладок
    private Label mainInfoLabel;

    @Override
    public void start(Stage stage) {
        
        HBox topNavBar = new HBox(15);
        topNavBar.setPadding(new Insets(8, 12, 8, 12));
        topNavBar.setAlignment(Pos.CENTER_LEFT);
        topNavBar.getStyleClass().add("top-nav-bar");

        Button homeBtn = new Button("🏠 Главная");
        homeBtn.getStyleClass().add("nav-button");
        homeBtn.setOnAction(e -> mainInfoLabel.setText("Smart Fitness - Трекер активности"));

        Button infoBtn = new Button("ℹ️ Информация");
        infoBtn.getStyleClass().add("nav-button");
        infoBtn.setOnAction(e -> showAppInfo());

        Button statsBtn = new Button("📈 Аналитика");
        statsBtn.getStyleClass().add("nav-button");
        statsBtn.setOnAction(e -> mainInfoLabel.setText("Просмотр детальной аналитики тренировок"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Меню 
        Button burger = new Button("\u2630");
        burger.getStyleClass().add("burger-small");
        burger.setFocusTraversable(false);
        ContextMenu menu = new ContextMenu();
        MenuItem miSettings = new MenuItem("Настройки");
        MenuItem miAbout = new MenuItem("О приложении");
        MenuItem miExit = new MenuItem("Выход");

        miSettings.setOnAction(e -> showInfo("Настройки", "Здесь будут настройки приложения"));
        miAbout.setOnAction(e -> showInfo("О приложении", "Smart Fitness v2.0\nПриложение для отслеживания активности"));
        miExit.setOnAction(e -> Platform.exit());

        menu.getItems().addAll(miSettings, miAbout, new SeparatorMenuItem(), miExit);
        burger.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> menu.show(burger, e.getScreenX(), e.getScreenY()));

        topNavBar.getChildren().addAll(homeBtn, infoBtn, statsBtn, spacer, burger);

        // Информационная строка
        mainInfoLabel = new Label("Smart Fitness - Трекер активности");
        mainInfoLabel.getStyleClass().add("main-info-label");
        mainInfoLabel.setPadding(new Insets(5, 12, 5, 12));

        VBox topContainer = new VBox(topNavBar, mainInfoLabel);

        // --- Left panel ---
        VBox leftBox = new VBox(16);
        leftBox.getStyleClass().add("card");
        leftBox.setPadding(new Insets(20));
        leftBox.setMinWidth(260);
        leftBox.setMaxWidth(420);

        Label title = new Label("Smart Fitness");
        title.getStyleClass().add("label-title");

        Button startBtn = new Button("Начать тренировку");
        startBtn.getStyleClass().add("primary");
        startBtn.setMaxWidth(Double.MAX_VALUE);

        // Прогресс-бар
        progressLabel = new Label("Цель: 0 / " + DAILY_GOAL + " шагов");
        progressLabel.getStyleClass().add("label-main");

        progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(12);

        stepsLabel = new Label("Шаги: 0");
        stepsLabel.getStyleClass().add("label-main");

        caloriesLabel = new Label("Калории: 0 ккал");
        caloriesLabel.getStyleClass().add("label-main");

        distanceLabel = new Label("Дистанция: 0.0 км");
        distanceLabel.getStyleClass().add("label-main");

        Separator sep = new Separator();
        sep.setPadding(new Insets(8, 0, 8, 0));

        // Средняя недельная активность
        Label weeklyActivityLabel = new Label("Средняя активность за неделю");
        weeklyActivityLabel.getStyleClass().addAll("muted", "section-sublabel");
        
        Label avgStepsLabel = new Label("⚡ Средние шаги: 2490 / день");
        avgStepsLabel.getStyleClass().add("label-stats");
        
        Label avgCaloriesLabel = new Label("🔥 Средние калории: 100 ккал / день");
        avgCaloriesLabel.getStyleClass().add("label-stats");

        // Пульс
        Label heartRateLabel = new Label("💓 Пульс: 72 уд/мин");
        heartRateLabel.getStyleClass().addAll("label-main", "heart-rate");
        
        Label heartRateStatusLabel = new Label("Состояние: Нормальное");
        heartRateStatusLabel.getStyleClass().add("muted");

        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(8, 0, 8, 0));

        Button resetBtn = new Button("Сбросить статистику");
        resetBtn.getStyleClass().add("ghost");
        resetBtn.setMaxWidth(Double.MAX_VALUE);
        resetBtn.setOnAction(e -> resetStats());

        leftBox.getChildren().addAll(
                title,
                startBtn,
                progressLabel,
                progressBar,
                stepsLabel,
                caloriesLabel,
                distanceLabel,
                sep,
                weeklyActivityLabel,
                avgStepsLabel,
                avgCaloriesLabel,
                sep2,
                heartRateLabel,
                heartRateStatusLabel,
                new Separator(),
                resetBtn
        );
        leftBox.setAlignment(Pos.TOP_CENTER);

        // --- Chart section ---
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("День недели");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Количество шагов");

        stepChart = new BarChart<>(xAxis, yAxis);
        stepChart.setTitle("Аналитика шагов за неделю");
        stepChart.setAnimated(true);
        stepChart.setLegendVisible(false);

        stepData = new XYChart.Series<>();
        stepData.getData().addAll(
                new XYChart.Data<>("Пн", 1200),
                new XYChart.Data<>("Вт", 3500),
                new XYChart.Data<>("Ср", 1800),
                new XYChart.Data<>("Чт", 3700),
                new XYChart.Data<>("Пт", 2400),
                new XYChart.Data<>("Сб", 3800),
                new XYChart.Data<>("Вс", 1500)
        );
        stepChart.getData().add(stepData);

        // Чекбокс для показа/скрытия графика
        chartVisibilityCheckBox = new CheckBox("Показать график");
        chartVisibilityCheckBox.setSelected(true);
        chartVisibilityCheckBox.getStyleClass().add("chart-checkbox");
        chartVisibilityCheckBox.setOnAction(e -> toggleChartVisibility());

        HBox chartHeader = new HBox(10, chartVisibilityCheckBox);
        chartHeader.setAlignment(Pos.CENTER_LEFT);
        chartHeader.setPadding(new Insets(10, 10, 5, 10));

        chartContainer = new VBox(5);
        chartContainer.getChildren().addAll(stepChart);
        VBox.setVgrow(chartContainer, Priority.ALWAYS);

        VBox chartSection = new VBox(chartHeader, chartContainer);
        chartSection.setPadding(new Insets(10));
        VBox.setVgrow(chartSection, Priority.ALWAYS);

        // --- Tab pane with exercises and statistics ---
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Tab exercisesTab = new Tab("💪 Упражнения");
        exercisesTab.setContent(createExercisesPane());
        
        Tab statisticsTab = new Tab("📊 Статистика");
        statisticsTab.setContent(createStatisticsPane());
        
        tabPane.getTabs().addAll(exercisesTab, statisticsTab);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // --- Right split pane (chart + tabs) ---
        rightSplit = new SplitPane();
        rightSplit.setOrientation(javafx.geometry.Orientation.VERTICAL);
        rightSplit.getItems().addAll(chartSection, tabPane);
        rightSplit.setDividerPositions(0.45);

        // --- Main split pane ---
        mainSplit = new SplitPane();
        mainSplit.getItems().addAll(leftBox, rightSplit);
        mainSplit.setDividerPositions(0.25);

        // Высота графика адаптируется
        stepChart.prefHeightProperty().bind(Bindings.createDoubleBinding(
                () -> Math.max(200, chartSection.getHeight() - 60),
                chartSection.heightProperty()
        ));

        // --- Кнопка "Начать тренировку" ---
        startBtn.setOnAction(e -> {
            int addSteps = 1000 + (int) (Math.random() * 3000);
            currentSteps += addSteps;

            int calories = (int) (currentSteps * 0.04);
            double distance = currentSteps * 0.0008;

            stepsLabel.setText("Шаги: " + currentSteps);
            caloriesLabel.setText("Калории: " + calories + " ккал");
            distanceLabel.setText(String.format("Дистанция: %.2f км", distance));

            double progress = Math.min(1.0, (double) currentSteps / DAILY_GOAL);
            progressBar.setProgress(progress);
            progressLabel.setText(String.format("Цель: %d / %d шагов (%.0f%%)",
                    currentSteps, DAILY_GOAL, progress * 100));

            for (XYChart.Data<String, Number> d : stepData.getData()) {
                int v = 800 + (int) (Math.random() * 3500);
                d.setYValue(v);
            }

            if (chartVisible) {
                ScaleTransition st = new ScaleTransition(Duration.millis(300), stepChart);
                st.setFromX(0.95);
                st.setFromY(0.95);
                st.setToX(1.0);
                st.setToY(1.0);
                st.play();
            }

            if (currentSteps >= DAILY_GOAL && currentSteps - addSteps < DAILY_GOAL) {
                showInfo("Поздравляем! 🎉",
                        "Вы достигли дневной цели в " + DAILY_GOAL + " шагов!\nОтличная работа!");
            }
        });

        BorderPane root = new BorderPane();
        root.setTop(topContainer);
        root.setCenter(mainSplit);
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 1400, 800);

        try {
            scene.getStylesheets().add(new File("styles.css").toURI().toString());
        } catch (Exception ex) {
            System.out.println("CSS не найден: " + ex.getMessage());
        }

        stage.setTitle("Smart Fitness - Трекер активности");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createExercisesPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));
        pane.getStyleClass().add("exercises-pane");

        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label header = new Label("План тренировок");
        header.getStyleClass().addAll("label-title", "section-header");
        HBox.setHgrow(header, Priority.ALWAYS);

        Button generateBtn = new Button("🔄 Новый план");
        generateBtn.getStyleClass().add("generate-button");
        generateBtn.setOnAction(e -> regenerateExercises());

        headerBox.getChildren().addAll(header, generateBtn);

        exercisesContainer = new VBox(12);
        generateInitialExercises();

        pane.getChildren().addAll(headerBox, exercisesContainer);
        
        ScrollPane scroll = new ScrollPane(pane);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        return new VBox(scroll);
    }

    private void generateInitialExercises() {
        exercisesContainer.getChildren().clear();
        
        VBox ex1 = createExerciseCard("Отжимания", "3 подхода × 15 повторений", 
                "Укрепление грудных мышц, трицепсов и плеч", "🔥");
        VBox ex2 = createExerciseCard("Подтягивания", "3 подхода × 10 повторений", 
                "Развитие мышц спины и бицепсов", "💪");
        VBox ex3 = createExerciseCard("Планка", "3 подхода × 60 секунд", 
                "Укрепление мышц кора и стабилизаторов", "⚡");
        VBox ex4 = createExerciseCard("Кардио", "20 минут", 
                "Повышение выносливости и сжигание калорий", "🏃");

        exercisesContainer.getChildren().addAll(ex1, ex2, ex3, ex4);
    }

    private void regenerateExercises() {
        exercisesContainer.getChildren().clear();

        List<ExerciseData> exercises = Arrays.asList(
            new ExerciseData("Отжимания", "3 подхода × 15 повторений", "Укрепление грудных мышц, трицепсов и плеч", "🔥"),
            new ExerciseData("Подтягивания", "3 подхода × 10 повторений", "Развитие мышц спины и бицепсов", "💪"),
            new ExerciseData("Планка", "3 подхода × 60 секунд", "Укрепление мышц кора и стабилизаторов", "⚡"),
            new ExerciseData("Приседания", "4 подхода × 20 повторений", "Укрепление ног и ягодиц", "🦵"),
            new ExerciseData("Берпи", "3 подхода × 12 повторений", "Комплексное упражнение на все тело", "💥"),
            new ExerciseData("Выпады", "3 подхода × 15 повторений", "Развитие мышц ног и координации", "🎯"),
            new ExerciseData("Скакалка", "5 подходов × 2 минуты", "Кардио и координация движений", "🪢"),
            new ExerciseData("Велосипед", "25 минут", "Аэробная нагрузка и выносливость", "🚴"),
            new ExerciseData("Пресс", "4 подхода × 20 повторений", "Укрепление мышц живота", "💎"),
            new ExerciseData("Бег", "30 минут", "Кардио и общая выносливость", "🏃"),
            new ExerciseData("Жим гантелей", "3 подхода × 12 повторений", "Развитие плеч и грудных мышц", "🏋️"),
            new ExerciseData("Становая тяга", "4 подхода × 8 повторений", "Силовое упражнение для спины", "⚡")
        );

        List<ExerciseData> shuffled = new ArrayList<>(exercises);
        java.util.Collections.shuffle(shuffled, random);

        for (int i = 0; i < Math.min(5, shuffled.size()); i++) {
            ExerciseData ex = shuffled.get(i);
            VBox card = createExerciseCard(ex.name, ex.reps, ex.description, ex.emoji);
            exercisesContainer.getChildren().add(card);
        }

        FadeTransition fade = new FadeTransition(Duration.millis(400), exercisesContainer);
        fade.setFromValue(0.3);
        fade.setToValue(1.0);
        fade.play();
    }

    private VBox createExerciseCard(String name, String reps, String description, String emoji) {
        VBox card = new VBox(8);
        card.getStyleClass().add("exercise-card");
        card.setPadding(new Insets(15));

        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 24px;");
        
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().addAll("label-main", "exercise-name");
        
        titleBox.getChildren().addAll(emojiLabel, nameLabel);

        Label repsLabel = new Label(reps);
        repsLabel.getStyleClass().addAll("muted", "exercise-reps");

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("exercise-desc");
        descLabel.setWrapText(true);

        Button trackBtn = new Button("Отметить выполнение");
        trackBtn.getStyleClass().add("ghost");
        trackBtn.setMaxWidth(Double.MAX_VALUE);
        trackBtn.setOnAction(e -> {
            trackBtn.setText("✓ Выполнено");
            trackBtn.setDisable(true);
        });

        card.getChildren().addAll(titleBox, repsLabel, descLabel, trackBtn);
        return card;
    }

    private VBox createStatisticsPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20));

        Label header = new Label("Статистика тренировок");
        header.getStyleClass().addAll("label-title", "section-header");

        TableView<WorkoutStat> table = new TableView<>();
        table.getStyleClass().add("stats-table");

        TableColumn<WorkoutStat, String> dateCol = new TableColumn<>("Дата");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(120);

        TableColumn<WorkoutStat, String> typeCol = new TableColumn<>("Тип");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(150);

        TableColumn<WorkoutStat, Integer> durationCol = new TableColumn<>("Длительность");
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        durationCol.setPrefWidth(120);

        TableColumn<WorkoutStat, Integer> caloriesCol = new TableColumn<>("Калории");
        caloriesCol.setCellValueFactory(new PropertyValueFactory<>("calories"));
        caloriesCol.setPrefWidth(100);

        table.getColumns().addAll(dateCol, typeCol, durationCol, caloriesCol);

        ObservableList<WorkoutStat> data = FXCollections.observableArrayList(
                new WorkoutStat("15.10.2025", "Кардио", 30, 240),
                new WorkoutStat("14.10.2025", "Силовая", 45, 320),
                new WorkoutStat("13.10.2025", "Йога", 60, 180),
                new WorkoutStat("12.10.2025", "Бег", 25, 280),
                new WorkoutStat("11.10.2025", "Силовая", 50, 350),
                new WorkoutStat("10.10.2025", "Кардио", 35, 260)
        );

        table.setItems(data);
        VBox.setVgrow(table, Priority.ALWAYS);

        GridPane summary = new GridPane();
        summary.setHgap(20);
        summary.setVgap(10);
        summary.setPadding(new Insets(15));
        summary.getStyleClass().add("summary-grid");

        Label totalLabel = new Label("Всего тренировок:");
        totalLabel.getStyleClass().add("muted");
        Label totalValue = new Label("6");
        totalValue.getStyleClass().add("label-main");

        Label avgLabel = new Label("Средняя длительность:");
        avgLabel.getStyleClass().add("muted");
        Label avgValue = new Label("41 мин");
        avgValue.getStyleClass().add("label-main");

        Label totalCalLabel = new Label("Всего калорий:");
        totalCalLabel.getStyleClass().add("muted");
        Label totalCalValue = new Label("1630 ккал");
        totalCalValue.getStyleClass().add("label-main");

        summary.add(totalLabel, 0, 0);
        summary.add(totalValue, 1, 0);
        summary.add(avgLabel, 0, 1);
        summary.add(avgValue, 1, 1);
        summary.add(totalCalLabel, 0, 2);
        summary.add(totalCalValue, 1, 2);

        pane.getChildren().addAll(header, table, summary);
        return pane;
    }

    private void toggleChartVisibility() {
        if (chartVisibilityCheckBox.isSelected()) {
            chartContainer.setVisible(true);
            chartContainer.setManaged(true);
            
            FadeTransition fade = new FadeTransition(Duration.millis(300), chartContainer);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();
            
            chartVisible = true;
        } else {
            FadeTransition fade = new FadeTransition(Duration.millis(300), chartContainer);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(e -> {
                chartContainer.setVisible(false);
                chartContainer.setManaged(false);
            });
            fade.play();
            
            chartVisible = false;
        }
    }

    private void showAppInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация о приложении");
        alert.setHeaderText("Smart Fitness v2.0");
        alert.setContentText(
            "🏋️ Умное приложение для отслеживания фитнес-активности\n\n" +
            "Возможности:\n" +
            "• Отслеживание шагов, калорий и дистанции\n" +
            "• Персональный план тренировок\n" +
            "• Аналитика активности по дням недели\n" +
            "• История тренировок и статистика\n" +
            "• Настраиваемые цели и напоминания\n\n" +
            "Разработано для здорового образа жизни 💪"
        );
        alert.showAndWait();
    }

    private void resetStats() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText(null);
        confirm.setContentText("Вы уверены, что хотите сбросить всю статистику?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                currentSteps = 0;
                stepsLabel.setText("Шаги: 0");
                caloriesLabel.setText("Калории: 0 ккал");
                distanceLabel.setText("Дистанция: 0.0 км");
                progressBar.setProgress(0);
                progressLabel.setText("Цель: 0 / " + DAILY_GOAL + " шагов");

                for (XYChart.Data<String, Number> d : stepData.getData()) {
                    d.setYValue(0);
                }
            }
        });
    }

    private void showInfo(String title, String text) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, text, ButtonType.OK);
        a.setHeaderText(null);
        a.setTitle(title);
        a.showAndWait();
    }

    // Класс для данных упражнений
    private static class ExerciseData {
        String name;
        String reps;
        String description;
        String emoji;

        ExerciseData(String name, String reps, String description, String emoji) {
            this.name = name;
            this.reps = reps;
            this.description = description;
            this.emoji = emoji;
        }
    }

    // Класс для статистики тренировок
    public static class WorkoutStat {
        private String date;
        private String type;
        private Integer duration;
        private Integer calories;

        public WorkoutStat(String date, String type, Integer duration, Integer calories) {
            this.date = date;
            this.type = type;
            this.duration = duration;
            this.calories = calories;
        }

        public String getDate() { return date; }
        public String getType() { return type; }
        public Integer getDuration() { return duration; }
        public Integer getCalories() { return calories; }
    }

    public static void main(String[] args) {
        launch();
    }
}