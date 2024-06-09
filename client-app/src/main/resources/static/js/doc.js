document.addEventListener("DOMContentLoaded", function() {
// Получаем байтовый массив из модели
    var fileContent = "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";

    // Создаем Blob из байтового массива
    var blob = new Blob([new Uint8Array(fileContent)], { type: "application/pdf" });

    // Создаем URL для Blob
    var url = URL.createObjectURL(blob);

    // Устанавливаем URL в iframe для отображения файла
    var iframe = document.getElementById('documentView');
    iframe.src = url;
});

//function uploadTemplate() {
//    var file = document.getElementById("file").files[0];
//    if (file) {
//        uploadFile(file);
//    }
//}
//
//function uploadFile(file) {
//    var formData = new FormData();
//    formData.append("file", file);
//
//    var xhr = new XMLHttpRequest();
//    xhr.open("POST", "/template-upload");
//    xhr.onload = function() {
//        if (xhr.status === 200) {
//            var response = JSON.parse(xhr.responseText);
//            var fileId = response.id; // Сервер возвращает объект с полем id
//
//            console.log("File uploaded successfully, ID: " + fileId);
//
//            // Сохранение id в скрытое поле
//            document.getElementById("fileId").value = fileId;
//            // Далее можно выполнить второй запрос или другие действия
//            viewReplaceWords(fileId); // Передаем id загруженного файла для использования во втором запросе
//        } else {
//            console.error("Failed to upload file");
//        }
//    };
//    xhr.send(formData);
//}
//
//function viewReplaceWords(fileId) {
//    var xhr = new XMLHttpRequest();
//    var url = "/view-replace-word";
//    url += "?fileId=" + encodeURIComponent(fileId);
//
//    xhr.open("GET", url);
//    xhr.onload = function() {
//        if (xhr.status === 200) {
//            var response = JSON.parse(xhr.responseText);
//            updateForm(response);
//        } else {
//            console.error("Failed to view replace words");
//        }
//    };
//    xhr.send();
//}
//
//function updateForm(data) {
//    // Обновляем форму согласно полученным данным
//    console.log(data);
//}