// Ingredients list
const items = [ "tobacco", "paper", "match" ];
// Smokers list
const smokers = {};
const smokers_el = {};
// Visual ingredients list and table
const list = document.getElementById("list");
const table_el = document.getElementById("table");

async function init() {
    // Setup smoker 'threads'
    for(let i = 0; i < items.length; i++) {
        // Create visual indicator for i element
        let ingredient = document.createElement("li");
        ingredient.id = items[i];
        ingredient.innerText = items[i];
        list.appendChild(ingredient);

        // Create thread
        smokers[items[i]] = new Worker("worker.js");
        // Redirect messages sent from the smoker to the agent
        smokers[items[i]].addEventListener("message", e => agent(e.data));
        // Tell smoker what his ingredient is
        smokers[items[i]].postMessage("type:" + items[i]);
        // Get reference to visual indicator
        smokers_el[items[i]] = ingredient;
    }
    // Waits 1s
    await sleep(1000);
    // Starts the picking process
    start();
}

function agent(message) {
    // In case a smoker has finished smoking
    if(message.indexOf("smoking:end") === 0) {
        // Remove visual indicators
        const type = message.split("smoking:end:")[1];
        smokers_el[type].className = "";
        // Reset table
        table_el.innerText = "(nothing)";
        // Start picking again
        start();
    }
}

async function start() {
    // Gets two random ingredients
    let items_copy = items.slice();
    items_copy = shuffle(items_copy);
    let left_out = items_copy.pop();
    // Wait 500ms
    await sleep(500);
    // Update table with picked ingredients
    table_el.innerText = (items_copy+"").replace(",", ", ");
    // Wait 500ms
    await sleep(500);
    // "Wake up" corresponding smoker
    smokers[left_out].postMessage("awake");
    smokers_el[left_out].className = "awake";
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

function shuffle(array) {
    let m = array.length;
    while(m) {
        let i = Math.floor(Math.random() * m--);
        let t = array[m];
        array[m] = array[i];
        array[i] = t;
    }
    return array;
}