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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MobileBankCallBackFormApiTestV1 {

    //инициализация переменной объекта driver
    private WebDriver driver;

    @BeforeAll
    static void setupAll() {
        WebDriverManager.chromedriver().setup(); //настройка драйвера для хрома
    }

    // загрузка драйвера с определенными параметрами.
    @BeforeEach
    void setup() {
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

    @Test
    public void shouldCreateCallbackRequest() {

        WebElement imageCart = driver.findElement(By.cssSelector("[alt='Альфа-Карта Premium']"));// найти рекламное изображение карты
        assertTrue(imageCart.isDisplayed()); //подтверждение наличия фотографии рекламной карты

        //Форма заявки
        List<WebElement> inputElements = driver.findElements(By.cssSelector("[class='input__control']")); //найти список элементов
        inputElements.get(0).sendKeys("Петров Иван"); //ввести в поле фамилию и имя
        inputElements.get(1).sendKeys("+71112223344"); //ввести номер телефона

        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click(); //поставить подтвердить согласие на обработку перс.данных
        driver.findElement(By.cssSelector("[class='button button_view_extra button_size_m button_theme_alfa-on-white']")).click(); //нажать на кнопку для отправки

        WebElement result = driver.findElement(By.cssSelector("[data-test-id='order-success']"));//проверяем наличие информации о создании заявки на обратный звонок через появление инф.сообщения

        assertTrue(result.isDisplayed());
        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", result.getText().trim());

    }

}
