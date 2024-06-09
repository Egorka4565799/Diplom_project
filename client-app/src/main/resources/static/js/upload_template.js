document.addEventListener("DOMContentLoaded", function() {
    document.getElementById('addCategoryBtn').addEventListener('click', function() {
        var categoryName = prompt("Please enter the new category name:", "");
        if (categoryName) {
            addCategory(categoryName);
        }
    });

    function addCategory(categoryName) {

        var formData = new FormData();
        formData.append("categoryName", categoryName);

        var xhr = new XMLHttpRequest();
        xhr.open("POST", "/add-category");
        xhr.onload = function() {
            if (xhr.status === 200) {
                console.log("Category add successfully");

                //updateCategoriesDropdown();
            } else {
                console.error("Failed to category add");
            }
        };
        xhr.send(formData);

    }

    function updateCategoriesDropdown() {

    }

    //------------------------------------

        function toggleReplacements(key) {
            var replacementsList = document.getElementById("replacements-" + key);
            if (replacementsList.style.display === "none") {
                replacementsList.style.display = "block"; // Отображаем список замен, если он скрыт
            } else {
                replacementsList.style.display = "none"; // Скрываем список замен, если он отображается
            }
        }

        function uploadTemplate() {
            var form = document.getElementById("uploadForm");
            var formData = new FormData(form);

            var xhr = new XMLHttpRequest();
            xhr.open("POST", "/view-replace-word");


            xhr.onload = function() {
                if (xhr.status === 200) {
                    console.log("Template uploaded successfully");
                    // Вставьте здесь код для обновления страницы или других действий после успешной загрузки

                    // Получаем данные из ответа сервера
                    var response = JSON.parse(xhr.responseText);

                    // Обновляем форму согласно полученным данным
                    updateForm(response);
                } else {
                    console.error("Failed to upload template");
                    // Вставьте здесь код для обработки ошибки при загрузке шаблона
                }
            };

            xhr.send(formData);
        }

        function addReplacementInput1(button) {
            var li = button.parentNode;
            var input = document.createElement("input");
            input.type = "text";
            input.name = "words[" + li.dataset.index + "].value[]";
            input.placeholder = "Enter value";
            li.appendChild(input);
        }
    function addReplacementInput(key) {
        var ul = document.getElementById("replacements-" + key);
        var input = document.createElement("input");
        input.type = "text";
        input.name = "words[" + key + "].value[" + (ul.children.length - 1) + "]";
        input.placeholder = "Enter value";
        var li = document.createElement("li");
        li.appendChild(input);
        ul.appendChild(li);
    }

        function uploadFile(file) {
            var formData = new FormData();
            formData.append("file", file);

            var xhr = new XMLHttpRequest();
            xhr.open("POST", "/template-api-upload-file");
            xhr.onload = function() {
                if (xhr.status === 200) {
                    console.log("File uploaded successfully");
                    //var response = JSON.parse(xhr.responseText);
                    // Далее можно выполнить второй запрос или другие действия
                    uploadData(); // Передаем имя загруженного файла для использования во втором запросе
                } else {
                    console.error("Failed to upload file");
                }
            };
            xhr.send(formData);
        }

        function uploadData() {
            var form = document.getElementById("uploadForm");

            // Получаем выбранный файл и его имя
            var input = document.getElementById('file');
            var fileName = input.files[0].name;

            // Получаем выбранный идентификатор категории
            var categorySelect = document.getElementById('categoryId');
            if (!categorySelect) {
                console.error("Category select box not found");
                return; // Прекратить выполнение, если элемент не найден
            }
            var categoryId = categorySelect.value;

            var key = "НАЧАЛО"
            var requestBody = [];
            // Получаем все элементы <li> в блоке с id="words"
            var wordItems = document.querySelectorAll("#words > ul > li");

            // Проходим по каждому элементу
            wordItems.forEach(function(item) {

                // Получаем ключ из элемента <span>
                key = item.querySelector("span").textContent;

                // Получаем значения из элементов <input> внутри элемента <ul>
                var values = [];

                // Получаем элемент <ul> для данного ключа
                var ul = item.querySelector("ul");

                // Проверяем, скрыт ли элемент <ul>
                if (ul.style.display === "none") {
                    values = []; // Если скрыт, создаем пустой массив значений
                } else {
                    // Получаем значения из элементов <input>
                    var inputItems = ul.querySelectorAll("input");
                    inputItems.forEach(function(input) {
                        values.push(input.value); // Добавляем значение в список
                    });
                }

                // Создаем объект ReplaceWordMapping и добавляем его в requestBody
                requestBody.push({ key: key, value: values });
            });

            // Формирование URL с добавлением двух параметров
            var url = "/template-api-upload-replace-word";
            url += "?fileName=" + encodeURIComponent(fileName);
            url += "&categoryId=" + encodeURIComponent(categoryId);

            var xhr = new XMLHttpRequest();
            xhr.open("POST", url);
            xhr.setRequestHeader("Content-Type", "application/json");

            xhr.onload = function() {
                if (xhr.status === 200) {
                    console.log('Данные успешно отправлены');
                    console.log(requestBody);
                    console.log(key);
                    console.log(wordItems);
                } else {
                    console.error('Ошибка при отправке данных');
                    console.log(requestBody);
                    console.log(key);
                    console.log(wordItems);
                }
            };

            xhr.send(JSON.stringify(requestBody));
        }

        function submitForm() {
            var file = document.getElementById("file").files[0];
            if (file) {
                uploadFile(file); // Вызываем функцию загрузки файла
            } else {
                console.error("No file selected");
            }
        }


        function updateForm(data) {
            var wordsBlockUp = document.getElementById("words");
            wordsBlockUp.innerHTML = ""; // Очищаем блок слов

            var wordsBlock = document.createElement("ul");
            wordsBlockUp.appendChild(wordsBlock);

            // Создаем новые элементы формы на основе полученных данных
            data.forEach(function(word, index) {
                var li = document.createElement("li");
                var span = document.createElement("span");
                span.textContent = word;
                span.name = "key"
                var addReplacementLabel = document.createElement("label");
                addReplacementLabel.className = "add-replacement-label";
                addReplacementLabel.id = "addReplacementLabel-" + word;
                addReplacementLabel.textContent = "+";
                addReplacementLabel.onclick = function() {
                    toggleReplacements(word);
                };
                var ul = document.createElement("ul");
                ul.id = "replacements-" + word;
                ul.style.display = "none"; // Список замен скрыт по умолчанию

                var button = document.createElement("button");
                button.type = "button";
                button.textContent = "Add Replacement";
                button.onclick = function() {
                    addReplacementInput(word);
                };
                var liReplacement = document.createElement("li");
                var input = document.createElement("input");
                input.type = "text";
                input.name = "words[" + word + "].value[0]";
                input.placeholder = "Enter value";

                // Добавляем элементы в DOM
                li.appendChild(span);
                li.appendChild(addReplacementLabel);
                li.appendChild(ul);
                ul.appendChild(button);
                ul.appendChild(liReplacement);
                liReplacement.appendChild(input);
                wordsBlock.appendChild(li);
        });
    }
    // Ваш JavaScript код здесь
});