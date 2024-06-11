document.addEventListener('DOMContentLoaded', function() {
    // Определение переменных
    var toggleButton = document.getElementById('toggleButton');
    var textContainer = document.querySelector('.text');
    var filterContainer = document.querySelector('.filter');
    var docxFile = document.getElementById('docx-img');
    var pdfFile = document.getElementById('pdf-img');
    var downloadButton = document.getElementById('download-button-start');
    var stopButton = document.getElementById('download-button-stop');
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

    // Обработчики событий для выбора типа файла
    docxFile.addEventListener('click', function() {
        // Для файла DOCX
        if (!docxFile.classList.contains('curent-file')) {
            docxFile.classList.add('curent-file');
            pdfFile.classList.remove('curent-file');
        }
        else
        {
            docxFile.classList.remove('curent-file');

        }
    });

    pdfFile.addEventListener('click', function() {
        // Для файла PDF
        if (!pdfFile.classList.contains('curent-file')) {
            docxFile.classList.remove('curent-file');
            pdfFile.classList.add('curent-file');
        }
        else
        {
            pdfFile.classList.remove('curent-file');

        }
    });

    function selectFormat(format) {
        document.getElementById('format').value = format;
        document.getElementById('docx-img').classList.remove('selected');
        document.getElementById('pdf-img').classList.remove('selected');
        if (format === 'DOCX') {
            document.getElementById('docx-img').classList.add('selected');
        } else {
            document.getElementById('pdf-img').classList.add('selected');
        }
    }


    // Обработчики событий для загрузки и остановки загрузки
    downloadButton.addEventListener('click', function() {
        // Начало загрузки
        overlay.style.display = 'flex';


        const form = document.getElementById('documentForm');
        const formData = new FormData(form);
        const action = form.action;

        console.log("formData: " + form);

        fetch(action, {
            method: 'POST',
            body: formData
        }).then(response => {
            if (response.ok) {
                // Останавливаем анимацию после получения ответа
                document.getElementById('overlay').style.display = 'none';
            } else {
                throw new Error('Network response was not ok');
            }
        }).catch(error => {
            document.getElementById('overlay').style.display = 'none';
            alert('Ошибка при загрузке документа: ' + error);
        });
    });


});
