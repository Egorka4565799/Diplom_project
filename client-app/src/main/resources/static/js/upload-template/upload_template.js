   function uploadTemplate() {
    var file = document.getElementById("file").files[0];
    var category = document.getElementById("categoryId").value;

     if (!file) {
        console.error("No file selected");
        showModal("Пожалуйста, выберите файл.");
        return;
     }

    if (!category) {
        console.error("Category not selected");
       showModal("Пожалуйста, выберите категорию.");
        return;
    }

    uploadFile(file, category); // Вызываем функцию загрузки файла
}

   function uploadFile(file, category) {
       var formData = new FormData();
       formData.append("file", file);

       var xhr = new XMLHttpRequest();
       xhr.open("POST", "/template-upload");
       xhr.onload = function() {
           if (xhr.status === 200) {
               var response = JSON.parse(xhr.responseText);
               var fileId = response.id; // Сервер возвращает объект с полем id

               console.log("File uploaded successfully, ID: " + fileId);

               // Сохранение id в скрытое поле
               document.getElementById("fileId").value = fileId;
               // Далее можно выполнить второй запрос или другие действия
               setCategoryToTemplate(fileId,category);
               viewReplaceWords(fileId); // Передаем id загруженного файла для использования во втором запросе
           } else {
               console.error("Failed to upload file");
           }
       };
       xhr.onerror = function() {
           console.error("Network error");
       };
       xhr.send(formData);
   }

   function setCategoryToTemplate(fileId, category){
      var xhr = new XMLHttpRequest();

      var url = "/set-category";
      url += "?fileId=" + encodeURIComponent(fileId);
      url += "&categoryId=" + encodeURIComponent(category);

      xhr.open("PUT", url);
      xhr.onload = function() {
          if (xhr.status === 200) {
            console.log("Gut to set category");
          } else {
              console.error("Failed to set category");
          }
      };
      xhr.onerror = function() {
          console.error("Network error");
      };
      xhr.send();
   }
   function viewReplaceWords(fileId) {
       var xhr = new XMLHttpRequest();

       // Получаем значение элемента fileId
       var fileIdValue = document.getElementById("fileId").value;

       var url = "/view-replace-word";
       url += "?fileId=" + encodeURIComponent(fileIdValue);

       xhr.open("GET", url);
       xhr.onload = function() {
           if (xhr.status === 200) {
               try {
                   var response = JSON.parse(xhr.responseText);
                   updateForm(response);
               } catch (e) {
                   console.error("Failed to parse JSON response: ", e);
               }
           } else {
               console.error("Failed to view replace words");
           }
       };
       xhr.onerror = function() {
           console.error("Network error");
       };
       xhr.send();
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
         var button = document.createElement("button");
           button.type = "button";
           button.textContent = "Загрузить замены";
           button.onclick = function() {
               uploadData();
           };
           button.id = "submitButton"
           wordsBlockUp.appendChild(button);
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

function uploadData() {
            var form = document.getElementById("uploadForm");

            // Получаем выбранный файл и его имя
            var input = document.getElementById('fileId');
            var fileId = input.value;

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
            var url = "/template-replace-word";
            url += "?fileId=" + encodeURIComponent(fileId);


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

//------------------------------------Модальное окно
function showModal(message) {
            var modal = document.getElementById("modal");
            var modalMessage = document.getElementById("modalMessage");
            modalMessage.textContent = message;
            modal.style.display = "block";
        }

        function closeModal() {
            var modal = document.getElementById("modal");
            modal.style.display = "none";
        }

        // Закрытие модального окна при клике вне его
        window.onclick = function(event) {
            var modal = document.getElementById("modal");
            if (event.target == modal) {
                modal.style.display = "none";
            }
        }