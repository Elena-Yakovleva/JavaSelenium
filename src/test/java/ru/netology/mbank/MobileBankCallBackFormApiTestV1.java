package ru.netology.mbank;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MobileBankCallBackFormApiTestV1 {

    //инициализация переменных
    private WebDriver driver;
    private WebElement result;
    private WebElement imageCart;
    private List<WebElement> inputElements;

    @BeforeAll
    static void setupAll() {
        WebDriverManager.chromedriver().setup(); //настройка драйвера для хрома
    }


    @BeforeEach
    void setup() {
        // загрузка драйвера с определенными параметрами.
        ChromeOptions options = new ChromeOptions(); //  набор параметров для драйвера
        options.addArguments("--disable-dev-shm-usage"); // отключает использование временного хранилища
        options.addArguments("--dno-sandbox"); // отключает песочницу безопасности
        options.addArguments("--headless"); // запускает браузер Chrome в режиме без графического интерфейса
        driver = new ChromeDriver(options); //сохраняем скаченный драйвер с нужными параметрами в переменной driver
        driver.get("http://localhost:9999");// запускаем страницу с помощью драйвера
    }

    // удаление и очистка истории после проведения теста
    @AfterEach
    void tearDown() {
        driver.quit();
        driver = null;
    }

    // проверка наличия рекламного изображения карты
    @Test
    public void shouldImageCard() {
        imageCart = driver.findElement(By.cssSelector("[alt='Альфа-Карта Premium']"));// найти рекламное изображение карты
        assertTrue(imageCart.isDisplayed()); //подтверждение наличия фотографии рекламной карты
    }

    @Test
    public void shouldCreateCallbackRequest() {

        //Форма заявки
        inputElements = driver.findElements(By.cssSelector("[class='input__control']")); //найти список элементов
        inputElements.get(0).sendKeys("Петров Иван"); //ввести в поле фамилию и имя
        inputElements.get(1).sendKeys("+71112223344"); //ввести номер телефона

        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click(); // подтвердить согласие на обработку перс.данных
        driver.findElement(By.cssSelector("[class='button button_view_extra button_size_m button_theme_alfa-on-white']")).click(); //нажать на кнопку для отправки

        result = driver.findElement(By.cssSelector("[data-test-id='order-success']"));//проверяем наличие информации о создании заявки на обратный звонок через появление инф.сообщения

        assertTrue(result.isDisplayed());  // проверяем появление сообщения
        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", result.getText().trim()); //проверяем текст сообщения

    }

    // Не заполнены Фамилия и Имя
    @Test
    public void shouldNotCreateCallbackRequestNotInputName() {
        inputElements = driver.findElements(By.cssSelector("[class='input__sub']")); //  создаем коллекцию с тестами ошибок для полей ввода

        driver.findElement(By.cssSelector("[class='button button_view_extra button_size_m button_theme_alfa-on-white']")).click(); // нажимаем на кнопку "Продолжить"
        result = inputElements.get(0); // сохраняем в переменной result первый элемент коллекции
        assertTrue(result.isDisplayed()); // проверяем, что сообщение отображается
        assertEquals("Поле обязательно для заполнения", result.getText().trim()); // проверяем текст сообщения

    }

    // Имя заполнено на латинице
    @Test
    public void shouldNotCreateCallbackRequestWithLatinName() {
        inputElements = driver.findElements(By.cssSelector("[class='input__control']")); // создаем коллекцию полей ввода
        inputElements.get(0).sendKeys("Petrov Ivan"); // вводим в первое поле Фамилию и Имя на латинице

        inputElements = driver.findElements(By.cssSelector("[class='input__sub']")); // создаем коллекцию элементов с текстами ошибок

        driver.findElement(By.cssSelector("[class='button button_view_extra button_size_m button_theme_alfa-on-white']")).click(); // нажимаем на кнопку "Продолжить"
        result = inputElements.get(0); // сохраняем в переменной текст первого элемента коллекции
        assertTrue(result.isDisplayed()); // проверяем, что сообщение отображается
        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", result.getText().trim()); // проверяем текст сообщения
    }

    // Не заполнен телефон
    @Test
    public void shouldNotCreateCallbackRequestNotPhoneNumber() {
        inputElements = driver.findElements(By.cssSelector("[class='input__control']")); // создаем коллекцию полей ввода
        inputElements.get(0).sendKeys("Петров Иван"); // вводим в первое поле корректные Фамилию и Имя

        inputElements = driver.findElements(By.cssSelector("[class='input__sub']")); // создаем коллекцию с текстами ошибок

        driver.findElement(By.cssSelector("[class='button button_view_extra button_size_m button_theme_alfa-on-white']")).click(); // нажимаем на кнопку "Продолжить"
        result = inputElements.get(1);   // сохраняем в переменной текст второго элемента коллекции
        assertTrue(result.isDisplayed()); // проверяем, что сообщение отображается
        assertEquals("Поле обязательно для заполнения", result.getText().trim()); // проверяем текст сообщения

    }

    // Номер телефона введен с ошибкой
    @Test
    public void shouldNotCreateCallbackRequestIncorrectPhoneNumber() {
        inputElements = driver.findElements(By.cssSelector("[class='input__control']")); // создаем коллекцию полей ввода
        inputElements.get(0).sendKeys("Петров Иван"); // вводим в первое поле корректные Фамилию и Имя
        inputElements.get(1).sendKeys("799922211333"); // вводим номер телефона с ошибкой

        inputElements = driver.findElements(By.cssSelector("[class='input__sub']")); // создаем коллекцию элементов с текстами ошибок

        driver.findElement(By.cssSelector("[class='button button_view_extra button_size_m button_theme_alfa-on-white']")).click(); // нажимаем на кнопку "Продолжить"
        result = inputElements.get(1);  // сохраняем в переменной текст второго элемента коллекции
        assertTrue(result.isDisplayed()); // проверяем, что сообщение отображается
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", result.getText().trim()); // проверяем текст сообщения
    }

    // Не стоит согласие на обработку персональных данных
    @Test
    public void shouldNotCreateCallbackRequestNoConsentProcessing() {
        inputElements = driver.findElements(By.cssSelector("[class='input__control']")); // создаем коллекцию полей ввода
        inputElements.get(0).sendKeys("Петров Иван"); // вводим в первое поле корректные Фамилию и Имя
        inputElements.get(1).sendKeys("+71112223344"); // вводим номер телефона

        assertFalse(driver.findElement(By.cssSelector("[data-test-id='agreement']")).isSelected()); // проверяем отсутствие заполнения чек-бокса
    }

}
