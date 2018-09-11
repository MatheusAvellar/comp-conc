const main = document.getElementById("main");
const canvas = document.getElementById("canvas");

const flags = {
    barber_ready: 0,
    waiting_room_open: 1,
    customer_waiting: 0
};
const line = [];
let free_seats = 5;

async function init() {
    draw_canvas();

    start_barber();
    await sleep(1500);

    const customers = ["Bob","Alice","Fred","Fernando","Joseph","Carol","Joanna","Francis"];
    for(let i = customers.length - 1; i >= 0; i--) {
        // Send customers with 100ms intervals
        await sleep(100);
        start_customer(customers[i]);
    }
}

async function start_barber() {
    while(true) {
        while(flags.customer_waiting === 0) {
            // Barber is sleeping, as there are no customers waiting
            await sleep(1000);
        }
        flags.customer_waiting--;
        while(flags.waiting_room_open === 0) {
            // Barber is sleeping, as the waiting room is closed
            await sleep(1000);
        }
        flags.waiting_room_open--;
        // Barber brings in the first customer in line
        free_seats++;  // Consequently there's a new free seat
        const current = line.shift();
        // Move everyone in line 1 space forward
        update_line_position();
        // Sit first in line to get their hair cut
        update_position(current, "haircut");

        flags.barber_ready++;
        flags.waiting_room_open++;
        // Barber is cutting customer's hair
        await sleep(3500);
        // Barber is done cutting customer's hair
        update_position(current, "left");
    }
}

async function start_customer(id) {
    const div = document.createElement("div");
    div.id = id;
    div.innerText = id;
    main.appendChild(div);

    while(true) {
        // Put customer on the door
        update_position(id, "door");
        while(flags.waiting_room_open === 0) {
            // Customer is waiting, as the waiting room is closed
            await sleep(1000);
        }
        flags.waiting_room_open--;
        await sleep(500);
        // If there are any free seats on the waiting room,
        if(free_seats > 0) {
            // Take that seat
            free_seats--; 
            // Mark as waiting, and allow others to enter the waiting room
            flags.customer_waiting++;
            flags.waiting_room_open++;
            // Add customer to the end of the line
            line.push(id);
            // Reposition every customer on the line
            update_line_position();
            while(flags.barber_ready === 0) {
                // Customer is in the waiting room
                await sleep(1000);
            }
            flags.barber_ready--;
            // Customer is getting their hair cut
            return;
        } else {
            flags.waiting_room_open++;
            update_position(id, "left");
            // Customer didn't find seats, and left
            return;
        }
    }
}

function update_line_position() {
    for(let i = line.length - 1; i >= 0; i--) {
        update_position(line[i], "line");
    }
}

function update_position(id, place) {
    const div = document.getElementById(id);
    if(!div) return;

    switch(place) {
        case "door":
            div.style.setProperty("--left", "-5px");
            div.style.setProperty("--top", "10px");
            break;
        case "line":
            if(line.indexOf(id) >= 0) {
                div.style.setProperty("--left", "150px");
                div.style.setProperty("--top", (150 - line.indexOf(id)*16) + "px");
                break;
            }
        case "haircut":
            div.style.setProperty("--left", "350px");
            div.style.setProperty("--top", "375px");
            break;
        case "left":
            div.style.setProperty("--left", "490px");
            div.style.setProperty("--top", "200px");
            break;
    }
}

function draw_canvas() {
    canvas.height = 500;
    canvas.width = 500;
    const ctx = canvas.getContext("2d");
    ctx.font = "sans-serif";
    ctx.fillText("Entrance",10,10);
    ctx.fillText("Waiting Room",220,125);
    ctx.fillText("Barber's Room",200,375);
    ctx.fillText("Exit",475,200);

    ctx.beginPath();
    ctx.moveTo(0,250);
    ctx.lineTo(500,250);
    ctx.stroke();
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}