<h1 align="center">Weather SDK</h1>

<p align="center">
  Этот SDK предоставляет удобный интерфейс для доступа к API OpenWeatherMap и получения данных о погоде для заданного местоположения. SDK поддерживает два режима работы: <strong>по запросу</strong> и <strong>режим опроса</strong>. Реализован на [укажите язык программирования, например, Python].
</p>

<h2 align="center">Оглавление</h2>

<ul>
  <li><a href="#установка">Установка</a></li>
  <li><a href="#использование">Использование</a>
    <ul>
      <li><a href="#инициализация-sdk">Инициализация SDK</a></li>
      <li><a href="#получение-данных-о-погоде">Получение данных о погоде</a></li>
      <li><a href="#обработка-ошибок">Обработка ошибок</a></li>
      <li><a href="#удаление-объекта-sdk">Удаление объекта SDK</a></li>
    </ul>
  </li>
  <li><a href="#режимы-работы">Режимы работы</a>
    <ul>
      <li><a href="#режим-по-запросу-on-demand">Режим по запросу (On-Demand)</a></li>
      <li><a href="#режим-опроса-polling">Режим опроса (Polling)</a></li>
    </ul>
  </li>
  <li><a href="#лимиты">Лимиты</a></li>
  <li><a href="#тестирование">Тестирование</a></li>
</ul>

<h2 id="установка">Установка</h2>

<p>Для установки SDK используйте менеджер пакетов [укажите менеджер пакетов, например, maven/gradle]:</p>
Maven:
<pre><code>&lt;dependency&gt;
    &lt;groupId&gt;io.github.mojoqqq&lt;/groupId&gt;
    &lt;artifactId&gt;weather-sdk&lt;/artifactId&gt;
    &lt;version&gt;2.0.0&lt;/version&gt;
&lt;/dependency&gt;
</code></pre>


Gradle:
<pre>
<code>
implementation("io.github.mojoqqq:weather-sdk:2.0.0")
</code>
</pre>

<h2 id="использование">Использование</h2>

<h3 id="инициализация-sdk">Инициализация SDK</h3>

<p>Для начала работы с SDK необходимо инициализировать объект, передав API ключ от OpenWeatherMap и режим работы ON_DEMAND или POLLING.</p>

<pre><code class="language-java">
# Инициализация SDK с API ключом и режимом по запросу
 WeatherSDK weatherSDK = WeatherSDK.create("api_key", Mode.POLLING);

# Инициализация SDK с режимом опроса
 WeatherSDK weatherSDK = WeatherSDK.create("api_key", Mode.ON_DEMAND);

</code></pre>

<h3 id="получение-данных-о-погоде">Получение данных о погоде</h3>

<p>Для получения данных о погоде используйте метод <code>get_weather</code>, передав название города.</p>

<pre><code class="language-java"># WeatherData weather = weatherSDK.getWeather("Saint-Petersburg");

# Вывод данных
log.info(weather); или System.out.println(weather);
</code></pre>

<p>Пример ответа:</p>

<pre><code class="language-json">{
  "weather": {
    "main": "Clouds",
    "description": "scattered clouds"
  },
  "temperature": {
    "temp": 269.6,
    "feels_like": 267.57
  },
  "visibility": 10000,
  "wind": {
    "speed": 1.38
  },
  "datetime": 1675744800,
  "sys": {
    "sunrise": 1675751262,
    "sunset": 1675787560
  },
  "timezone": 3600,
  "name": "Moscow"
}
</code></pre>

<h3 id="обработка-ошибок">Обработка ошибок</h3>

<p>SDK выбрасывает исключения в случае возникновения ошибок, таких как неверный API ключ, проблемы с сетью или неверное название города.</p>

<pre><code class="language-java">
###############
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("API Key cannot be null or empty");
        }
###########
        if (instances.containsKey(apiKey)) {
            throw new IllegalStateException("An instance with the same API key already exists");
        }
################
        throw new OpenApiHttpException("OpenWeather API throws an client-side 4xx exception");
</code></pre>

<h3 id="удаление-объекта-sdk">Удаление объекта SDK</h3>

<p>Для удаления объекта SDK используйте метод <code>removeInstance(String apiKey)</code>.</p>

<pre><code class="language-java">WeatherSDK.removeInstance(your_api_key)
</code></pre>

<h2 id="режимы-работы">Режимы работы</h2>

<h3 id="режим-по-запросу-on-demand">Режим по запросу (On-Demand)</h3>

<p>В этом режиме SDK обновляет данные о погоде только при явном запросе. Это позволяет минимизировать количество запросов к API.</p>

<h3 id="режим-опроса-polling">Режим опроса (Polling)</h3>

<p>В этом режиме SDK периодически обновляет данные о погоде для всех сохраненных местоположений, чтобы обеспечить нулевую задержку при запросах.</p>

<h2 id="лимиты">Лимиты</h2>

<ul>
  <li>SDK хранит данные о погоде для не более чем <strong>10 городов</strong> одновременно.</li>
  <li>Данные считаются актуальными в течение <strong>10 минут</strong>.</li>
</ul>

<h2 id="тестирование">Тестирование</h2>

<p>Тесты используют моки для сетевых запросов, чтобы избежать реальных вызовов API.</p>

