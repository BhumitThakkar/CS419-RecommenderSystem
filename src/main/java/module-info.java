module Group4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    opens Group4.CS419RecommenderSystem to javafx.fxml;
    exports Group4.CS419RecommenderSystem;
}