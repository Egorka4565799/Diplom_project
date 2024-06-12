document.addEventListener('DOMContentLoaded', function() {
     const searchInput = document.getElementById('searchInput');
     const checkboxes = document.querySelectorAll('.category-checkbox');
     const templates = document.querySelectorAll('.template');

     function filterTemplates() {
         const searchValue = searchInput.value.toLowerCase();
         const selectedCategories = Array.from(checkboxes)
             .filter(checkbox => checkbox.checked)
             .map(checkbox => parseInt(checkbox.getAttribute('data-category-id')));

         templates.forEach(template => {
             const templateName = template.querySelector('.template-link').textContent.toLowerCase();
             const templateCategoryId = parseInt(template.getAttribute('data-category-id'));

             const matchesSearch = !searchValue || templateName.includes(searchValue);
             const matchesCategory = !selectedCategories.length || selectedCategories.includes(templateCategoryId);

             if (matchesSearch && matchesCategory) {
                 template.style.display = '';
             } else {
                 template.style.display = 'none';
             }
         });
     }

     checkboxes.forEach(checkbox => {
         checkbox.addEventListener('change', filterTemplates);
     });

     searchInput.addEventListener('input', filterTemplates);

     // При загрузке страницы отображаем все шаблоны
     filterTemplates();
 });