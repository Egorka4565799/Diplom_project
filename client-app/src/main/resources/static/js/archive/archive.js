document.addEventListener('DOMContentLoaded', function() {
        // Получаем ссылки на элементы формы и результатов
        var nameInput = document.getElementById('name');
        var startDateInput = document.getElementById('startDate');
        var endDateInput = document.getElementById('endDate');
        var tableRows = document.querySelectorAll('.table tbody tr');
         var resetButton = document.querySelector('.btn.btn-primary');

        // Функция для фильтрации документов по имени
        function filterByName() {
            var filterText = nameInput.value.trim().toLowerCase();
            tableRows.forEach(function(row) {
                var documentName = row.querySelector('td:nth-child(2)').textContent.trim().toLowerCase();
                if (documentName.includes(filterText)) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            });
        }

        // Функция для фильтрации документов по дате
        function filterByDate() {
            var startDate = new Date(startDateInput.value);
            var endDate = new Date(endDateInput.value);
            tableRows.forEach(function(row) {
                var documentDate = new Date(row.querySelector('td:nth-child(3)').textContent);
                if ((!startDateInput.value || documentDate >= startDate) && (!endDateInput.value || documentDate <= endDate)) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            });
        }
         // Функция для сброса фильтрации и возвращения полей ввода к начальному состоянию
    function resetFilters() {
        nameInput.value = '';
        startDateInput.value = '';
        endDateInput.value = '';
        tableRows.forEach(function(row) {
            row.style.display = '';
        });
    }

        // Добавляем обработчики событий для полей ввода
        nameInput.addEventListener('input', filterByName);
        startDateInput.addEventListener('input', filterByDate);
        endDateInput.addEventListener('input', filterByDate);
        resetButton.addEventListener('click', resetFilters);
    });