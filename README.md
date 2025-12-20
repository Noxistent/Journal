# Journal APP
 Journal APP - это Android-приложение для ведения еженедельного расписания, использующее  Tencent MMKV для  хранения  данных  и  Jetpack Compose для  создания  интерактивного  интерфейса.

## Функционал
- **Вход в систему:**  
  Пользователь могут  заполнять  свои  данные  (имя  пользователя  и  пароль)  на  странице  входа,  которое  сохраняется  в  Tencent MMKV.

- **Получение расписания:**  
  После  успешного  входа  приложение  загружает  расписание  с  сайта,  которое  может  обновляться.
  Данные  отображаются  в красивом списке при  помощи  `HorizontalPager`  и  `LazyColumn`.

- **UpdateTimeManager (Управление временем):**  
  Отвечает  за  вычисление  текущего  времени  и  определение,  на  какую  неделю  относится  текущее  расписание.

![Black тема] (https://github.com/Noxistent/Journal/blob/master/Image/Screenshot_20251221_014502.png) ![Write тема] (https://github.com/Noxistent/Journal/blob/master/Image/Screenshot_20251221_014530.png)

## Стек технологий
* **UI:** Jetpack Compose
* **Network:** Retrofit + Gson
* **Storage:** Tencent MMKV (высокопроизводительное хранилище)

## Лицензия проекта
Данный проект распространяется под лицензией **GNU GPL v3.0**. Полный текст лицензии находится в файле [LICENSE](./LICENSE).

---

## Использованные библиотеки (Credits)

Этот проект использует стороннее программное обеспечение с открытым исходным кодом. Мы благодарим авторов за их вклад:

| Библиотека | Автор / Правообладатель | Лицензия |
| :--- | :--- | :--- |
| **[MMKV](https://github.com/Tencent/MMKV)** | Tencent (THL A29 Limited) | BSD 3-Clause |
| **[Retrofit](https://github.com/square/retrofit)** | Square, Inc. | Apache 2.0 |
| **[Gson](https://github.com/google/gson)** | Google Inc. | Apache 2.0 |
| **[Jetpack Compose](https://developer.android.com/jetpack/compose)** | Google (AOSP) | Apache 2.0 |

### Важные уведомления:
* **OpenSSL:** В составе библиотеки MMKV используется программное обеспечение, разработанное проектом OpenSSL для использования в OpenSSL Toolkit ([http://www.openssl.org/](http://www.openssl.org/)).
* **Cryptographic software:** Этот продукт включает криптографическое ПО, написанное Эриком Янгом (eay@cryptsoft.com).
