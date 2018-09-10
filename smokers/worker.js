// This smoker's ingredient (to be assigned)
let type = "";

self.addEventListener("message", function(e) {
    if(e.data.indexOf("type:") === 0) {
        // Message assigning an ingredient to this smoker
        type = e.data.split("type:")[1];
        console.log(`Defined smoker of type [${type}]`);
    } else if(e.data.indexOf("awake") === 0) {
        // Message telling smoker to wake up (and smoke)
        console.log(`[${type}] smoker has waken up!`);
        smoke(self);
    }
});

async function smoke(me) {
    // Tell agent this smoker has started smoking
    me.postMessage(`smoking:start:${type}`);
    // Smoke for a certain period of time (1 ± [0,2) seconds)
    await sleep(~~(Math.random()*2000) + 1000);
    // Tell agent this smoker is done smoking
    me.postMessage(`smoking:end:${type}`);
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}