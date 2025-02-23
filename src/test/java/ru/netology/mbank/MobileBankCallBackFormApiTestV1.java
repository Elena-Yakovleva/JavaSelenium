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
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Петров Иван"); //ввести в поле фамилию и имя
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+71112223344"); //ввести номер телефона

        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click(); // подтвердить согласие на обработку перс.данных
        driver.findElement(By.cssSelector("[class='button__content']")).click(); //нажать на кнопку для отправки

        result = driver.findElement(By.cssSelector("[data-test-id='order-success']"));//проверяем наличие информации о создании заявки на обратный звонок через появление инф.сообщения

        assertTrue(result.isDisplayed());  // проверяем появление сообщения
        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", result.getText().trim()); //проверяем текст сообщения

    }

    // Не заполнены Фамилия и Имя
    @Test
    public void shouldNotCreateCallbackRequestNotInputName() {

        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys(""); //оставить поле для ввода имени не заполненным
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+71112223344"); //ввести номер телефона

        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click(); // подтвердить согласие на обработку перс.данных
        driver.findElement(By.cssSelector("[class='button__content']")).click(); //нажать на кнопку для отправки

        result = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub")); // сохраняем сообщение об ошибке

        assertTrue(result.isDisplayed()); // проверяем, что сообщение отображается
        assertEquals("Поле обязательно для заполнения", result.getText().trim()); // проверяем текст сообщения

    }

    // Имя заполнено на латинице
    @Test
    public void shouldNotCreateCallbackRequestWithLatinName() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Petrov Ivan"); // ввести имя и фамилию на латинице
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+71112223344"); //ввести номер телефона

        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click(); // подтвердить согласие на обработку перс.данных
        driver.findElement(By.cssSelector("[class='button__content']")).click(); //нажать на кнопку для отправки

        result = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub")); // сохраняем сообщение об ошибке

        assertTrue(result.isDisplayed()); // проверяем, что сообщение отображается
        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", result.getText().trim()); // проверяем текст сообщения
    }

    // Не заполнен телефон
    @Test
    public void shouldNotCreateCallbackRequestNotPhoneNumber() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Петров Иван"); // ввести имя и фамилию
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys(""); // оставить поле для ввода телефона пустым

        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click(); // подтвердить согласие на обработку перс.данных
        driver.findElement(By.cssSelector("[class='button__content']")).click(); //нажать на кнопку для отправки

        result = driver.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub")); // сохраняем сообщение об ошибке

        assertTrue(result.isDisplayed()); // проверяем, что сообщение отображается
        assertEquals("Поле обязательно для заполнения", result.getText().trim()); // проверяем текст сообщения

    }

    // Номер телефона введен без плюса
    @Test
    public void shouldNotCreateCallbackRequestIncorrectPhoneNumber() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Петров Иван"); // ввести имя и фамилию
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("71112223344"); // ввести номер телефона без плюса

        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click(); // подтвердить согласие на обработку перс.данных
        driver.findElement(By.cssSelector("[class='button__content']")).click(); //нажать на кнопку для отправки

        result = driver.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub")); // сохраняем сообщение об ошибке

        assertTrue(result.isDisplayed()); // проверяем, что сообщение отображается
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", result.getText().trim()); // проверяем текст сообщения
    }

    // Нет согласия на обработку персональных данных
    @Test
    public void shouldNotCreateCallbackRequestNoConsentProcessing() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Петров Иван"); // ввести имя и фамилию
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+71112223344"); // ввести номер телефона

        driver.findElement(By.cssSelector("[data-test-id='agreement']")); //   согласие на обработку перс.данных не ставить
        driver.findElement(By.cssSelector("[class='button__content']")).click(); //нажать на кнопку для отправки

        assertFalse(driver.findElement(By.cssSelector("[data-test-id='agreement'].input_invalid")).isSelected()); // проверяем отсутствие заполнения чек-бокса. 2. вариант привязки к блоку ("label.input_invalid")).isSelected())
        assertTrue(driver.findElement(By.cssSelector("[data-test-id='agreement'].input_invalid")).isDisplayed());

    }
        // Проверка соответствия цвета сообщения ожидаемому результату, возможна через значения CSS-свойства "color"
        //String actualColor = driver.findElement(By.cssSelector("label.input_invalid")).getCssValue("color");
        // String expectedColor = "rgba(255, 92, 92, 1)"; // ожидаемый результат можно посмотреть при нажатии на index.css:16 в блоке style для селектора .input_invalid
        //assertEquals(expectedColor, actualColor);


}
