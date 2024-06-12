document.addEventListener('DOMContentLoaded', function() {
    // Определение переменных
    var toggleButton = document.getElementById('toggleButton');
    var textContainer = document.querySelector('.text');
    var filterContainer = document.querySelector('.filter');
    var overlay = document.getElementById('overlay');


    ///////////////////////////* ОПРЕДЕЛЕНИЕ ВЫСОТЫ ОСНОВНОГО КОНТЕЙНЕРА СТРАНИЦЫ *///////////////////////////
    // Получаем высоту элементов, которые хотим учесть
    var headerHeight = document.querySelector('header').clientHeight;
    var footerHeight = document.querySelector('footer').clientHeight;
    var settingsBar = document.querySelector('.settings-bar');
    var settingsBarStyles = window.getComputedStyle(settingsBar); // Получаем вычисленные стили элемента

    // Получаем вертикальные отступы элемента .settings-bar
    var settingsBarMarginTop = parseFloat(settingsBarStyles.marginTop);
    var settingsBarMarginBottom = parseFloat(settingsBarStyles.marginBottom);

    // Получаем высоту элемента .settings-bar с учетом отступов
    var settingsBarHeight = settingsBar.clientHeight + settingsBarMarginTop + settingsBarMarginBottom;

    // Вычисляем сумму высоты всех элементов, которые хотим учесть
    var totalHeightOfOtherElements = headerHeight + footerHeight + settingsBarHeight;

    // Получаем высоту всего документа
    var bodyHeight = document.body.clientHeight;

    // Вычисляем высоту оставшегося пространства
    var containerHeight = bodyHeight - totalHeightOfOtherElements;

    // Устанавливаем высоту вашего контейнера
    document.querySelector('.container').style.height = containerHeight + 'px';


    // Обработчик события для кнопки "Toggle"
    toggleButton.addEventListener('click', function() {
        // Проверка состояния видимости текстового контейнера
        if (textContainer.classList.contains('hidden')) {
            // Если текстовый контейнер скрыт
            textContainer.classList.remove('hidden');

            filterContainer.style.width = '30%';
            textContainer.style.width = '70%';

            toggleButton.classList.remove('reverse');
            toggleButton.style.right = 'calc(70% - 18px)';
            toggleButton.textContent = '⮞';
        } else {
            // Если текстовый контейнер отображается
            textContainer.classList.add('hidden');

            filterContainer.style.width = '100%';
            textContainer.style.width = '0';

            toggleButton.textContent = '⮜';
            toggleButton.style.right = '11px';
            toggleButton.classList.add('reverse');
        }
    });

});
