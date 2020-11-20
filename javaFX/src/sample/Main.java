package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.sql.Time;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
    LocalTime localTime = LocalTime.now();
    private int difference = 90;
    ToggleGroup group = new ToggleGroup();
    RadioButton up = new RadioButton("Up");
    RadioButton down = new RadioButton("Down");
    TextField sNumber = new TextField("S-Number");
    TextField password= new PasswordField();
    TextField whatTime = new TextField("Please give the time you want to go Today HH:MM ");

    @Override
    public void start(Stage stage) throws Exception{
        System.setProperty(
                "webdriver.chrome.driver",
                "C:/Program Files (x86)/chromedriver.exe");
        Button btn = new Button();
        btn.setText("Start the bot");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(5);
        Label title = new Label("Enter s-number and password");
        title.setFont(Font.font(18));
        grid.add(title,0,0,2,1);
        grid.add(new Label("s-number"),0,1);
        grid.add(new Label("password"),0,2);
        grid.add(new Label("Time"),0,3);


        grid.add(sNumber,1,1);
        grid.add(password,1,2);
        grid.add(whatTime,1,3);
        grid.add(btn,1,6);

        up.setToggleGroup(group);
        down.setToggleGroup(group);
        grid.add(up,1,4);
        grid.add(down,1,5);
        grid.add(new Label("Where do you want to go"),0,4);
        btn.setOnAction( (e) -> {
            try {
                runBoot();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });
        Scene scene = new Scene(grid,500,300);
        stage.setTitle("Ticket Bot");
        stage.setScene(scene);
        stage.show();


    }


    private void runBoot() throws InterruptedException {
        String sNumb = sNumber.getText();
        String passwo = password.getText();
        String time = whatTime.getText();
        int hour = Integer.parseInt(time.split(":")[0]);
        int min = Integer.parseInt(time.split(":")[1]);
        int total = (hour*60) + min;
        int now =  (localTime.getHour() * 60 )+ localTime.getMinute();
        System.out.println(localTime.getHour());
        int gymTime = total - now;
        int finish = (gymTime/difference) + 1;
        System.out.println(finish);
        RadioButton selected = (RadioButton) group.getSelectedToggle();
        boolean up = selected.getText().equals("Up");
        WebDriver driver = new ChromeDriver();
        driver.get("https://publiek.usc.ru.nl/publiek/login.php");
        driver.findElement(By.xpath("//*[@id='inputNummer']")).sendKeys(sNumb);
        driver.findElement(By.xpath("//*[@id='inputPassword']")).sendKeys(passwo);
        driver.findElement(By.xpath("//*[@id='login-panel']/div[2]/form/div[3]/div/button")).click();
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.xpath("/html/body/header/div/div/div/div[3]/div[2]/ul/li[4]/a")).click();
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.xpath("/html/body/div[3]/article/form/table/tbody/tr[4]/td[1]/input")).click();
        StringBuilder desired = new StringBuilder();
        if(up){
            desired.append("//*[@id='pagecontent']/article/table/tbody/tr[");
            desired.append(finish);
            desired.append("]");



        }
        else{
            desired.append("//*[@id='pagecontent']/article/table/tbody/tr[");
            desired.append(finish+80);
            desired.append("]");

        }
        TimeUnit.SECONDS.sleep(2);
        driver.findElement(By.xpath(desired.toString())).click();
        String m = driver.findElement(By.xpath("//*[@id='pagecontent']/article/div/div[1]/div")).getText();
        System.out.println(m);

       while( m.equals(" VOL") || m.equals("Je staat al ingeschreven op dit uur.")){
           driver.navigate().refresh();
           m =  driver.findElement(By.xpath("//*[@id='pagecontent']/article/div/div[1]/div")).getText();
           System.out.println(m);
           TimeUnit.SECONDS.sleep(3);
       }

       getTickets(driver);
    }

    private void getTickets(WebDriver driver) throws InterruptedException {
        driver.findElement(By.xpath("//*[@id='pagecontent']/article/div/div[1]/div")).click();

        TimeUnit.MILLISECONDS.sleep(25);
        driver.findElement(By.xpath("//*[@id='pagecontent']/article/article[1]/b/a")).click();

        TimeUnit.MILLISECONDS.sleep(25);
        driver.findElement(By.xpath("//*[@id='pagecontent']/article/div/form/fieldset/div/div[2]/div/button")).click();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
