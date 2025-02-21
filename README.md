[![Build status](https://ci.appveyor.com/api/projects/status/384y9559tckeb5gk?svg=true)](https://ci.appveyor.com/project/Elena-Yakovleva/javaselenium)

### Домашнее задание к занятию «2.1. Тестирование веб-интерфейсов»

 **Селекторы**

Перед выполнением ДЗ рекомендуем вам ознакомиться с кратким руководством по работе с селекторами.

#### Настройка
1. Целевой сервис

   Ваш целевой сервис (SUT — System under test) расположен в файле ````app-order.jar````, его нужно положить в каталог artifacts вашего проекта.
   Затем, небходимо принудительно заставить GIT следить за ним: git add `````-f artifacts/app-order.jar.`````
   Запушить файл в удаленный репозиторий и проверить его наличие.

2. build.gradle
   Файл build.gradle в проектах на базе Selenium должен выглядеть следующим образом:
````
plugins {
    id 'java'
}

group 'ru.netology'
version '1.0-SNAPSHOT'

    sourceCompatibility = 11

    // кодировка файлов (если используете русский язык в файлах)
    compileJava.options.encoding = "UTF-8"
    compileTestJava.options.encoding = "UTF-8"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.6.1'
    testImplementation 'org.seleniumhq.selenium:selenium-java:4.18.1'
    testImplementation 'io.github.bonigarcia:webdrivermanager:5.7.0'
}

test {
    useJUnitPlatform()
    // свойство chromeoptions.prefs используется для задания настроек браузера в проектах на основе Selenide, выключаем менеджер паролей
    systemProperty 'chromeoptions.prefs', System.getProperty('chromeoptions.prefs', "profile.password_manager_leak_detection=false")
}

````

#### Headless-режим браузера
- На серверах сборки чаще всего нет графического интерфейса, поэтому, запуская браузер в режиме headless, мы отключаем графический интерфейс. При этом все процессы браузера продолжают работать так же. При использовании Selenium этот режим настраивается непосредственно в коде во время инициализации драйвера, примеры инициализации ниже.

- Включение headless-режима при использовании Selenium необходимо реализовать в коде во время создания экземпляра WebDriver:
````
ChromeOptions options = new ChromeOptions();
options.addArguments("--disable-dev-shm-usage");
options.addArguments("--no-sandbox");
options.addArguments("--headless");
driver = new ChromeDriver(options);
````

#### WebDriver для разных операционных систем

Если вы выполняете работу с использованием Selenium, то будьте готовы, что ваша сборка может упасть из-за того, что у вас в репозитории WebDriver для одной ОС, например, для Windows, а в CI используется Linux. Для решения этой проблемы можно использовать библиотеку Webdriver Manager. Она автоматически определяет ОС и версию браузера, скачивает и устанавливает подходящий файл драйвера. Кстати, в Selenide используется именно эта библиотека. Для автоматической настройки хромдрайвера с помощью Webdriver Manager в проектах на основе Selenium добавьте в тестовый класс метод
``````
@BeforeAll
public static void setupAll() {
WebDriverManager.chromedriver().setup();
}

``````

При использовани Webdriver Manager отпадает необходимость в хранении файлов вебдрайвера в репозитории проекта и задании значения свойства webdriver.chrome.driver вручную.

3. .appveyor.yml
   AppVeyor настраивается аналогично задаче в предыдущей лекции.

Команда запуска SUT в секции install будет выглядеть следующим образом
``````
- java -jar ./artifacts/app-card-delivery.jar &
``````
4. gradle.yml

Если вы используете Github Actions для интеграции с проектом, то в gradle.yml надо уточнить команду запуска SUT аналогично настройке Appveyor.

### Задача №1: заказ карты
Вам необходимо автоматизировать тестирование формы заказа карты:

![](https://github.com/netology-code/aqa-homeworks/raw/master/web/pic/order.png)

Требования к содержимому полей:

* В поле фамилии и имени разрешены только русские буквы, дефисы и пробелы.
* В поле телефона — только 11 цифр, символ + на первом месте.
* Флажок согласия должен быть выставлен.

Тестируемая функциональность: отправка формы.

Условия: если все поля заполнены корректно, то вы получаете сообщение об успешно отправленной заявке:

![](https://github.com/netology-code/aqa-homeworks/raw/master/web/pic/success.jpg)

Вам необходимо самостоятельно изучить элементы на странице, чтобы подобрать правильные селекторы.

Подсказка
<details>
Смотрите на `data-test-id` и внутри него ищите нужный вам `input` — используйте вложенность для селекторов.
</details>

* Проект с автотестами должен быть выполнен на базе Gradle с использованием Selenium.

* Для запуска тестируемого приложения, находясь в корне проекта, выполните в терминале команду: java -jar ./artifacts/app-order.jar

Приложение будет запущено на порту 9999. Убедиться, что приложение работает, вы можете, открыв в браузере страницу: http://localhost:9999

Если по каким-то причинам порт 9999 на вашей машине используется другим приложением, используйте:
``````
java -jar app-order.jar -port=7777

``````

### Задача №2: проверка валидации (необязательная)

После того как вы протестировали happy path, необходимо протестировать остальные варианты.

Тестируемая функциональность: валидация полей перед отправкой.

Условия: если какое-то поле не заполнено или заполнено неверно, то при нажатии на кнопку «Продолжить» должны появляться сообщения об ошибке. Будет подсвечено только первое неправильно заполненное поле:

![](https://github.com/netology-code/aqa-homeworks/raw/master/web/pic/error.png)

Подсказка
<details>
У некоторых элементов на странице появится css-класс `input_invalid`.
</details>