// Получаем категории из скрытого поля
let categories = JSON.parse(document.getElementById('categoriesData').value);

document.addEventListener('DOMContentLoaded', function() {
    displayCategories();

    document.getElementById('searchCategory').addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase();
        displayCategories(searchTerm);
    });
});

function displayCategories(searchTerm = '') {
    const categoryList = document.getElementById('categoryList');
    categoryList.innerHTML = '';

    categories.filter(category => category.categoryName.toLowerCase().includes(searchTerm)).forEach(category => {
        const categoryItem = document.createElement('div');
        categoryItem.className = 'category-item';
        categoryItem.innerHTML = `
            <span>${category.categoryName}</span>
            <div>
                <button class="btn btn-secondary" onclick="editCategory(${category.id}, '${category.categoryName}')">Редактировать</button>
                <button class="btn btn-danger" onclick="deleteCategory(${category.id})">Удалить</button>
            </div>
        `;
        categoryList.appendChild(categoryItem);
    });
}

function addCategory() {
    const newCategoryName = document.getElementById('newCategoryName').value.trim();
    if (newCategoryName && !categories.some(category => category.categoryName === newCategoryName)) {
        fetch('/categories', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({ 'categoryName': newCategoryName })
        })
        .then(response => response.json()) // Парсим ответ как JSON
        .then(data => {
            if (data && data.id) { // Проверяем, что ответ содержит ID новой категории
                categories.push(data); // Добавляем новую категорию в список
                displayCategories();
                document.getElementById('newCategoryName').value = ''; // Очищаем поле ввода
            } else {
                alert('Ошибка при добавлении категории');
            }
        })
        .catch(error => {
            console.error('Ошибка:', error);
            alert('Ошибка при добавлении категории');
        });
    }
}

function editCategory(categoryId, categoryName) {
    const newCategoryName = prompt('Введите новое имя для категории', categoryName);
    if (newCategoryName) {
        fetch(`/categories/${categoryId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({ 'categoryName': newCategoryName })
        })
        .then(response => response.json())
        .then(data => {
            const categoryIndex = categories.findIndex(category => category.id === categoryId);
            if (categoryIndex !== -1) {
                categories[categoryIndex].categoryName = newCategoryName;
                displayCategories();
            }
        })
        .catch(error => {
            console.error('Ошибка:', error);
            alert('Ошибка при редактировании категории');
        });
    }
}

function deleteCategory(categoryId) {
    if (confirm(`Вы уверены, что хотите удалить категорию?`)) {
        fetch(`/categories/${categoryId}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                categories = categories.filter(category => category.id !== categoryId);
                displayCategories();
            } else {
                alert('Ошибка при удалении категории');
            }
        })
        .catch(error => {
            console.error('Ошибка:', error);
            alert('Ошибка при удалении категории');
        });
    }
}