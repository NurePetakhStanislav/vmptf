let tasks = [];
fetch("tasks.json")
    .then(response => response.json())
    .then(task => {
        task.forEach(element => {
            tasks.push(element)
        })
    })

function is_done(bool) {
    const list = document.getElementById("list");

    while (list.firstChild) {
        list.removeChild(list.firstChild);
    }

    tasks
        .filter(t => t.done === bool)
        .forEach(element => {
            const li = document.createElement("li");
            li.textContent = element.title;
            list.appendChild(li);
        });
}

function add_to_list() {
    const value = document.getElementById("taskInput").value

    const task = {
        title: value,
        done: false
    };

    const json = JSON.stringify(task);

    tasks.push(task);

    is_done(false);
}

const checkbox = document.getElementById("task1")
checkbox.addEventListener("click", () => {
    is_done(checkbox.checked);
});

const button = document.getElementById("addBtn")
    .addEventListener('click', add_to_list)