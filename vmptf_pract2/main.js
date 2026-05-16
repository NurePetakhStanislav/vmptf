function runCheck() {
    const text = document.getElementById("input").value;

    try {
        const result = JSON.parse(text);
        document.getElementById("result").textContent = "Дійсний JSON";
    } catch {
        document.getElementById("result").textContent = "Недійсний JSON";
    }
}

document.getElementById("btn")
    .addEventListener("click", runCheck);